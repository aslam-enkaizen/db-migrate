package com.exrade.runtime.admissionControl;

import com.exrade.core.EventLogger;
import com.exrade.core.ExLogger;
import com.exrade.models.Role;
import com.exrade.models.activity.Verb;
import com.exrade.models.contact.Contact;
import com.exrade.models.event.LogEventType;
import com.exrade.models.invitations.InvitationStatus;
import com.exrade.models.invitations.NegotiationInvitation;
import com.exrade.models.messaging.*;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.negotiation.PrivacyLevel;
import com.exrade.models.negotiation.UserAdmissionStatus;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.processmodel.protocol.ProtocolBehaviour;
import com.exrade.models.processmodel.protocol.events.TimeEvent;
import com.exrade.models.processmodel.protocol.stages.FinalStage;
import com.exrade.models.userprofile.Actor;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.security.NegotiationRole;
import com.exrade.modeltemplate.processmodel.ProcessModelLabels.Transitions;
import com.exrade.platform.exception.*;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.security.Security;
import com.exrade.platform.validator.ExValidator;
import com.exrade.runtime.ExradeConstants;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.contact.ContactManager;
import com.exrade.runtime.contact.IContactManager;
import com.exrade.runtime.engine.StateMachine;
import com.exrade.runtime.filemanagement.FileManager;
import com.exrade.runtime.invitation.INegotiationInvitationManager;
import com.exrade.runtime.invitation.NegotiationInvitationManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.NegotiationNotificationEvent;
import com.exrade.runtime.security.RoleManager;
import com.exrade.runtime.timer.TimeEventScheduler;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.exrade.util.ProcessModelUtil;
import com.google.common.base.Strings;

import java.util.*;

/**
 * @author jasonfinnegan This class is responsible for handling the methods
 * related to access to Join, Invite and Ban Participants from
 * Negotiations
 */
public class AdmissionController {

    private NotificationManager notificationManager = new NotificationManager();

    public String join(Negotiator iJoiner, Negotiation iNegotiation) {

        return join(iJoiner, iNegotiation, null);
    }

    public ExAdmissionException validateAdmissionStatus(Negotiation iNegotiation) {
        if (!iNegotiation.getAdmissionBox().isOpen() ||
                (!iNegotiation.isDraft() && !iNegotiation.isPending() && iNegotiation.getStateMachine(iNegotiation.getOwner()).getStage() instanceof FinalStage)) {
            return new ExAdmissionException(ErrorKeys.JOIN_ADMISSION_CLOSED);

        }
        if (iNegotiation.isEndDateInPast()) {
            return new ExAdmissionException(ErrorKeys.JOIN_NEGOTIATION_ENDED);

        }
        // check if maximum number of participant reached
        if (iNegotiation.getParticipants().size() >= iNegotiation.getMaxParticipants()) {
            return new ExJoinException(ErrorKeys.PARTICIPANT_SIZE_MAX);
        }
        return null;
    }

    public boolean isAdmissionOpen(Negotiation negotiation) {
        ExAdmissionException validityCheckException = validateAdmissionStatus(negotiation);
        return validityCheckException == null;
    }

    public void checkAdmissionAllowed(Negotiator iNegotiator, Negotiation negotiation) {

        ExAdmissionException validityCheckException = validateAdmissionStatus(negotiation);
        if (validityCheckException != null) throw validityCheckException;

        if (iNegotiator != null) {
            // check if actor is owner
            IMembershipManager membershipManager = new MembershipManager();
            if (membershipManager.isMembershipOwnedBy(iNegotiator.getUser(), negotiation.getOwnerUUID()))
                throw new ExJoinException(ErrorKeys.USER_IS_OWNER);
            // check if any other user is involved from same profile of the actor
            //if (!PrivacyLevel.PRIVATE.equals(negotiation.getPrivacyLevel())){
            if (iNegotiator.getProfile().equals(negotiation.getOwner().getProfile()))
                throw new ExJoinException(ErrorKeys.JOIN_DUPLICATE_MEMBER);
            for (Negotiator negotiator : negotiation.getParticipants()) {
                if (iNegotiator.getProfile().equals(negotiator.getProfile()))
                    throw new ExJoinException(ErrorKeys.JOIN_DUPLICATE_MEMBER);
            }
            //}
        }

        AdmissionRequest existingUserRequest = negotiation.getAdmissionBox()
                .getAdmissionRequest(iNegotiator);

        if (existingUserRequest != null) {
            if (MessageStatus.PENDING.equals(existingUserRequest.getStatus())) {
                throw new ExJoinException(ErrorKeys.JOIN_ALREADY_REQUESTED);
            } else if (MessageStatus.ACCEPTED.equals(existingUserRequest
                    .getStatus())) {
                throw new ExJoinException(ErrorKeys.USER_ALREADY_JOINED);
            } else if (MessageStatus.REJECTED.equals(existingUserRequest
                    .getStatus())) {
                throw new ExJoinException(ErrorKeys.JOIN_PREVIOUSLY_REJECTED);
            }
        }
    }


    public boolean canJoin(Negotiator iNegotiator, Negotiation negotiation) {
        try {
            checkAdmissionAllowed(iNegotiator, negotiation);
            return true;
        } catch (ExAdmissionException ex) {
            return false;
        }
    }

    private boolean acceptAdmission(Negotiation negotiation, AdmissionRequest admissionRequest) {

        if (!isAdmissionOpen(negotiation)) {
            return false;
        }

        if (admissionRequest == null) {
            throw new ExJoinException(ErrorKeys.JOIN_REQUEST_INVALID);
        }

        admissionRequest.setStatus(MessageStatus.ACCEPTED);

        addParticipant(negotiation, admissionRequest.getSender());
        ActivityLogger.log(admissionRequest.getSender(), Verb.JOIN, admissionRequest, negotiation, Arrays.asList(negotiation.getOwner()));

        if (negotiation.isAgreementSigningEnabled())
            addAgreementSigners(negotiation, admissionRequest);

        createOpeningOfferOrUpdateDraft(negotiation, admissionRequest);

        // Add into contacts
        addContact(admissionRequest.getSender(), negotiation.getOwner());
        addContact(negotiation.getOwner(), admissionRequest.getSender());

        notificationManager.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_USER_JOINED, negotiation, admissionRequest));

        return true;
    }

    public void addNegotiator(Negotiation iNegotiation, Negotiator negotiator, ProtocolBehaviour protocolBehaviour, List<TimeEvent> timeEvents) {
        StateMachine stateMachine = PersistentManager.newDbInstance(StateMachine.class, iNegotiation, protocolBehaviour, negotiator);
        iNegotiation.addStateMachine(stateMachine);
        Date now = TimeProvider.now();

        TimeEventScheduler scheduler = new TimeEventScheduler();
        if (iNegotiation.getStartDate().compareTo(now) > 0) {
            scheduler.schedule(iNegotiation.getUuid(), negotiator.getIdentifier(), Transitions.START, Transitions.START, iNegotiation.getStartDate());
        } else {
            iNegotiation.getStateMachine(negotiator).executeTransition(Transitions.START, null);
        }
        // attach time schedules... scan in protocol model and attach
        // scheduler
        for (TimeEvent timeEvent : timeEvents) {
            String transition = ProcessModelUtil.findTransition(timeEvent, protocolBehaviour);
            if (transition != null && timeEvent.getTime() != null && timeEvent.getTime().compareTo(now) > 0)
                scheduler.schedule(iNegotiation.getUuid(), negotiator.getIdentifier(), transition, timeEvent.getName(), timeEvent.getTime());
        }
    }

    private void addParticipant(Negotiation negotiation, Negotiator iParticipant) {
        RoleManager roleManager = new RoleManager();

        Actor participantNegotiator = PersistentManager.newDbInstance(Actor.class, iParticipant, roleManager.findByName(NegotiationRole.PARTICIPANT));
        //participantNegotiator.addRole(roleManager.findByName(NegotiationRole.SIGNER)); //TODO: make participant singer configurable
        //Actor participantNegotiator = new Actor(iParticipant, role);
        //negotiation.getParticipants().add(iParticipant);
        negotiation.addNegotiator(participantNegotiator);

        addNegotiator(negotiation, iParticipant, negotiation.getProcessModel().getParticipantsProtocolBehaviour(), negotiation.getParticipantTimeEvents());

        // create LogEvent for Owner
        EventLogger.logNegotiationEvent(negotiation, negotiation.getOwner(), iParticipant,
                LogEventType.PARTICIPANT_JOINED, null);
        //ActivityLogger.log(iParticipant, Verb.JOIN, negotiation, Arrays.asList(negotiation.getOwner()));
    }

    private void addAgreementSigners(Negotiation negotiation, AdmissionRequest admissionRequest) {
        ExLogger.get().info("Adding agreement signer for the Negotiation: {}, AdmissionRequest: {}", negotiation.getUuid(), admissionRequest.getUuid());
        if (ExCollections.isNotEmpty(admissionRequest.getAgreementSigners())) {
            //IInformationModelManager informationModelManager = new InformationModelManager();
            RoleManager roleManager = new RoleManager();
            Role signerRole = roleManager.findByName(NegotiationRole.SIGNER);

            for (Negotiator negotiator : admissionRequest.getAgreementSigners()) {
                Negotiator existingNegotiator = negotiation.getNegotiatorForMembership(negotiator.getIdentifier());
                if (existingNegotiator == null) {
                    ExLogger.get().info("Adding agreement signer - new negotiator. Negotiation: {}, Negotiator: {}", negotiation.getUuid(), negotiator.getIdentifier());
                    Actor signerNegotiator = PersistentManager.newDbInstance(Actor.class, negotiator, signerRole);
                    negotiation.addNegotiator(signerNegotiator);
                } else if (!Security.hasRole(existingNegotiator.getRoles(), NegotiationRole.SIGNER)) {
                    ExLogger.get().info("Adding agreement signer - updating role. Negotiation: {}, Negotiator: {}", negotiation.getUuid(), ((Actor) existingNegotiator).getMembership().getIdentifier());
                    ((Actor) existingNegotiator).addRole(signerRole);
                } else {
                    ExLogger.get().info("Ignoring to add agreement signer for the Negotiation: {}, Negotiator: {}", negotiation.getUuid(), negotiator.getIdentifier());
                }
            }
        }

    }

    private void createOpeningOfferOrUpdateDraft(Negotiation negotiation, AdmissionRequest admissionRequest) {
        //Create opening offer only for Processes where participant starts with Evaluate Offer
        String firstAction = negotiation.getStateMachine(admissionRequest.getSender()).readStatus().getAction();
        if (firstAction != null && firstAction.equalsIgnoreCase(ExradeConstants.EVALUATEOFFER_DOACTION)) {

            // send the original offer to the participant
            // NegotiationMessage startOffer =
            // NegotiationMessageFactory.createOwnerMessage(
            // readStartOffer(), iParticipant);
            Offer startOffer = new NegotiationMessageFactory(negotiation).createOffer(negotiation.readStartOffer(), negotiation.getOwner(), admissionRequest.getSender(), null);

            if (negotiation.isAgreementSigningEnabled())
                startOffer.setTemplate(InformationModelUtil.updateAllParticipantSigners(startOffer.getTemplate(), admissionRequest.getAgreementSigners().get(0)));

            ExValidator.validateOffer(startOffer, negotiation);
            InformationModelUtil.removeVariableNamesFromHtmlWhereNoValue(startOffer);
            negotiation.getMessageBox().enqueueProtocolMessage(startOffer);
            //create evaluable message with offer template
            EvaluableMessage evaluableMessage = new EvaluableMessage(startOffer);
            evaluableMessage.setTemplate(startOffer.getTemplate());
            negotiation.getMessageBox().getEvaluableMessageList().add(evaluableMessage);

            new FileManager().updateFileMetadata(negotiation, startOffer);
            //Log Opening Offer
            EventLogger.logNegotiationEvent(negotiation, startOffer.getSender(), startOffer.getReceiver(),
                    LogEventType.MESSAGE_SENT, startOffer);
            EventLogger.logNegotiationEvent(negotiation, startOffer.getReceiver(), startOffer.getSender(),
                    LogEventType.MESSAGE_RECEIVED, startOffer);
            ActivityLogger.log(negotiation.getOwner(), Verb.SEND, startOffer, negotiation, Collections.singletonList(admissionRequest.getSender()));
        } else {
            if (negotiation.isAgreementSigningEnabled()) {
                NegotiationMessage message = negotiation.getMessageDraft(admissionRequest.getSender());
                if (message != null) {
                    ((Offer) message).setTemplate(InformationModelUtil.updateAllParticipantSigners(((Offer) message).getTemplate(), admissionRequest.getAgreementSigners().get(0)));
                }
            }
        }
    }

    private void addContact(Negotiator linkedMembership, Negotiator ownerMembership) {
        try {
            Contact contact = new Contact((Membership) linkedMembership);
            contact.setOwner((Membership) ownerMembership);

            IContactManager contactManager = new ContactManager();
            try {
                contactManager.addContact(contact);
            } catch (ExParamException pex) {
                ExLogger.get().warn("Failed to create contact. {}", pex.getMessage());
            }
        } catch (Exception ex) {
            ExLogger.get().warn("Failed to create contact.", ex);
        } // ignore exception if cannot create contact
    }

    /**
     * This method is called when the negotiation owner accepts an admission
     * request it should delete the admissionrequest and add the participant to
     * the neg
     */
    public boolean acceptAdmission(Negotiation negotiation,
                                   String iAdmissionRequestUUID) {
        AdmissionRequest admissionRequest = negotiation.getAdmissionBox().getAdmissionRequestByUUID(iAdmissionRequestUUID);
        boolean result = acceptAdmission(negotiation, admissionRequest);

        if (result) {
            notificationManager.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_JOIN_REQUEST_UPDATED, negotiation, admissionRequest));
            ActivityLogger.log(negotiation.getOwner(), Verb.ACCEPT, admissionRequest, negotiation, Arrays.asList(admissionRequest.getSender()));
        }

        return result;
    }

    /**
     * This method is called when the negotiation owner accepts an admission
     * request it should delete the admissionrequest and add the participant to
     * the neg
     */
    public boolean rejectAdmission(Negotiation negotiation,
                                   String iAdmissionRequestUUID) {

        negotiation.getAdmissionBox().rejectAdmissionRequest(
                iAdmissionRequestUUID);

        AdmissionRequest admissionRequest = negotiation.getAdmissionBox().getAdmissionRequestByUUID(iAdmissionRequestUUID);
        notificationManager.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_JOIN_REQUEST_UPDATED, negotiation, admissionRequest));
        ActivityLogger.log(negotiation.getOwner(), Verb.REJECT, admissionRequest, negotiation, Arrays.asList(admissionRequest.getSender()));
        return true;
    }

    public boolean invite(Negotiation negotiation, Negotiator invitee) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public String join(Negotiator iJoiner, Negotiation iNegotiation, List<String> iSigners) {
        checkAdmissionAllowed(iJoiner, iNegotiation);

        INegotiationInvitationManager negotiationInvitationManager = new NegotiationInvitationManager();
        NegotiationInvitation negotiationInvitation = null;
        // check valid invitation for private deals
        if (Strings.isNullOrEmpty(iNegotiation.getNegotiationTemplateUUID())) {
            negotiationInvitation = negotiationInvitationManager.getNegotiationInvitation(iJoiner, iJoiner.getUser().getEmail(), iNegotiation.getUuid());

            if (PrivacyLevel.PRIVATE == iNegotiation.getPrivacyLevel() && (negotiationInvitation == null || InvitationStatus.PENDING != negotiationInvitation.getInvitationStatus()))
                throw new ExInvitationException(ErrorKeys.JOIN_REQUEST_INVALID);
        }

        IMembershipManager membershipManager = new MembershipManager();
        List<Negotiator> agreementSigners = new ArrayList<Negotiator>();

        if (iNegotiation.isAgreementSigningEnabled()) {
            if (ExCollections.isNotEmpty(iSigners)) {
                for (String signerUUID : iSigners) {
                    agreementSigners.add(membershipManager.findByUUID(signerUUID, true));
                }
            } else {
                agreementSigners.add(iJoiner);
            }
        }

        AdmissionRequest admissionRequest = PersistentManager.newDbInstance(AdmissionRequest.class, iJoiner,
                iNegotiation.getOwner());
        admissionRequest.setAgreementSigners(agreementSigners);

        iNegotiation.getAdmissionBox().addAdmissionRequest(admissionRequest);

        EventLogger.logNegotiationEvent(iNegotiation, iNegotiation.getOwner(), iJoiner,
                LogEventType.ADMISSION_REQUEST_RECEIVED, admissionRequest);

        if (PrivacyLevel.PUBLIC == iNegotiation.getPrivacyLevel()
                || !Strings.isNullOrEmpty(iNegotiation.getNegotiationTemplateUUID()) // allow joining to private deal if it created from a template
                || negotiationInvitation != null) {
            // The request is automatically accepted when the negotiation is
            // PUBLIC or user comes with valid invitation code or user has received an invitation
            acceptAdmission(iNegotiation, admissionRequest);
            notificationManager.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_USER_JOINED, iNegotiation, admissionRequest));
        } else if (PrivacyLevel.PUBLIC_RESTRICTED == iNegotiation.getPrivacyLevel()) {
            notificationManager.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_JOIN_REQUESTED, iNegotiation, admissionRequest));
            ActivityLogger.log(iJoiner, Verb.REQUEST, admissionRequest, iNegotiation, Arrays.asList(iNegotiation.getOwner()));
        }

        negotiationInvitationManager.acceptNegotiationInvitationIfExists(iJoiner.getIdentifier(), iNegotiation.getUuid());

        return admissionRequest.getUuid();
    }

    /**
     * Load UserStatus according to the provided user uuid Default UserStatus is
     * NOT_AVAILABLE
     */
    public UserAdmissionStatus getUserAdmissionStatus(Negotiation iNegotiation, Negotiator iActor) {
        UserAdmissionStatus userStatus = UserAdmissionStatus.NONE;

        if (iActor != null) {
            // Check if the user is involved
            if (iNegotiation.getOwner().equals(iActor)) {
                userStatus = UserAdmissionStatus.OWNED;
            } else if (iNegotiation.isParticipant(iActor)) {
                userStatus = UserAdmissionStatus.PARTICIPATED;
            } else {
                // The user is not yet involved in the negotiation
                AdmissionRequest userAdmission = iNegotiation.getAdmissionBox().getAdmissionRequest(iActor);
                if (userAdmission != null) {
                    if (MessageStatus.PENDING.equals(userAdmission.getStatus())) {
                        userStatus = UserAdmissionStatus.REQUESTED;
                    } else if (MessageStatus.PENDING.equals(userAdmission.getStatus())) {
                        userStatus = UserAdmissionStatus.REJECTED;
                    }
                } else if (ContextHelper.getMembership() != null) {
                    try {
                        INegotiationInvitationManager invitationManager = new NegotiationInvitationManager();
                        if (invitationManager.getNegotiationInvitation(iActor, iActor.getUser().getEmail(), iNegotiation.getUuid()) != null)
                            userStatus = UserAdmissionStatus.INVITED;
                    } catch (Exception ex) {
                        ExLogger.get().warn("Failed to get user admission status for Negotiation: " + iNegotiation.getUuid() + ", Membership: " + iActor.getIdentifier(), ex);
                    }
                }
            }
        }

        return userStatus;
    }
}
