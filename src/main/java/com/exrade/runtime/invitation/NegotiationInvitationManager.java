package com.exrade.runtime.invitation;

import com.exrade.models.activity.Verb;
import com.exrade.models.invitations.AbstractInvitation;
import com.exrade.models.invitations.InvitationStatus;
import com.exrade.models.invitations.NegotiationInvitation;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.negotiation.PublishStatus;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.security.NegotiationRole;
import com.exrade.platform.exception.*;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.admissionControl.AdmissionController;
import com.exrade.runtime.invitation.persistence.InvitationQuery;
import com.exrade.runtime.invitation.persistence.NegotiationInvitationPersistence;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.notification.event.NegotiationNotificationEvent;
import com.exrade.runtime.rest.RestParameters.NegotiationInvitationFields;
import com.exrade.runtime.rest.RestParameters.NegotiationInvitationFilters;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NegotiationInvitationManager extends AbstractInvitationManager implements INegotiationInvitationManager {

	public NegotiationInvitationManager() {
		this(new NegotiationInvitationPersistence());
	}

	public NegotiationInvitationManager(NegotiationInvitationPersistence iInvitationPersistenceManager) {
		invitationPersistence = iInvitationPersistenceManager;
	}

	@Override
	public NegotiationInvitation inviteToNegotiation(final Negotiator iNegotiator,final String iNegotiationUUID,
			final Negotiator iInvitedMember,final String iInvitedEmail) {

		INegotiationManager negotiationManager = new NegotiationManager();
		Negotiation negotiationForInvite = negotiationManager.getNegotiation(iNegotiationUUID);
		if (negotiationForInvite == null) {
			throw new ExParamException(ErrorKeys.PARAM_INVALID,NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID);
		}

		Negotiator invitedMember = null;
		if (iInvitedMember == null && Strings.isNullOrEmpty(iInvitedEmail)) {
			throw new ExMultiParamException(ErrorKeys.PARAMS_INVALID,Arrays.asList(NegotiationInvitationFilters.INVITED_MEMBERSHIP_UUID,NegotiationInvitationFields.INVITED_EMAIL));
		}
		else if (iInvitedMember != null){
			invitedMember = iInvitedMember;
		}
		else if (iInvitedMember == null && !Strings.isNullOrEmpty(iInvitedEmail)){ // if the user is invited by email
		// 	check if the provided email is owned by an already registered user
			IMembershipManager membershipManager = new MembershipManager();
			invitedMember = membershipManager.findDefaultMembershipByEmail(iInvitedEmail);
		}

		// retrieve eventual already present invitation
		NegotiationInvitation invitation = getNegotiationInvitation(invitedMember, iInvitedEmail,negotiationForInvite.getUuid());

		checkNegotiationInvitation(negotiationForInvite,invitedMember,invitation);

		if(invitation == null){
			invitation = createNegotiationInvitation(invitedMember, iInvitedEmail, negotiationForInvite);
			createContact(invitation, (Membership)invitation.getInvitedMembership(), (Membership)negotiationForInvite.getOwner()); // automatically create contact in Owner's profile

			logger.info("Created negotiation invitation - {}", invitation);
		}
		else if(invitation.getInvitedNegotiation().isOwner(iNegotiator)
				&& invitation.isInvitationStatus(InvitationStatus.REJECTED)){
			// if a new invite is requested by the owner and the invited user rejected previous invitation
			// the invite status is reverted to PENDING instead of creating a new invite
			updateInvitationStatus(invitation, InvitationStatus.PENDING);
			logger.info("Updated negotiation invitation - {}", invitation);
		}
		else if(negotiationForInvite.getPublishStatus() == PublishStatus.ACTIVE
				&& invitation.isInvitationStatus(InvitationStatus.DRAFT)) {
			updateInvitationStatus(invitation, InvitationStatus.PENDING);
			logger.info("Updated negotiation invitation - {}", invitation);
		}

		if(invitation.getInvitationStatus() == InvitationStatus.PENDING) {
			notificationManager.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_INVITATION_RECEIVED, negotiationForInvite, invitation));
			ActivityLogger.log(ContextHelper.getMembership(), Verb.INVITE, invitation, Arrays.asList(invitation.getInvitedMembership()));
		}

		return invitation;
	}


	private NegotiationInvitation createNegotiationInvitation(Negotiator iInvitedMember,String iTargetUserEmail, Negotiation negotiationForInvite){
		NegotiationInvitation invitation = null;
		if (iInvitedMember != null){
			// if the user is provided the invitation has to be created with the given user
			invitation = new NegotiationInvitation(iInvitedMember, negotiationForInvite);
		}
		else {
			invitation = new NegotiationInvitation(iTargetUserEmail, negotiationForInvite);
		}
		invitation.setInvitedBy((Membership)ContextHelper.getMembership());

		if(negotiationForInvite.getPublishStatus() != PublishStatus.ACTIVE)
			invitation.setInvitationStatus(InvitationStatus.DRAFT);

		return invitationPersistence.create(invitation);
	}

	private void checkNegotiationInvitation(Negotiation negotiation, Negotiator iInvitedMember, NegotiationInvitation invitation) {
		// check if admission is allowed
		AdmissionController admissionController = new AdmissionController();
		try{
			admissionController.checkAdmissionAllowed(iInvitedMember, negotiation);
		}
		catch(ExException  ex){
			throw new ExInvitationException(ex.getMessageKey(), ex);
		}
		catch(Exception ex){
			throw new ExInvitationException(ErrorKeys.INVITATION_NOT_ALLOWED, ex);
		}

		if(iInvitedMember != null && negotiation.isInvolved(iInvitedMember))
			throw new ExInvitationException(ErrorKeys.INVITATION_NOT_ALLOWED);

		if(invitation != null){
			if (invitation.getInvitationStatus() == InvitationStatus.BLOCKED){
				throw new ExInvitationException(ErrorKeys.INVITATION_STATUS_INVALID);
			}
			if (invitation.getInvitationStatus() == InvitationStatus.PENDING){
				throw new ExInvitationException(ErrorKeys.INVITATION_ALREADY_SENT);
			}
		}
	}

	@Override
	public List<NegotiationInvitation> find(QueryFilters iFilters) {
		List<NegotiationInvitation> invitations = new ArrayList<>();

		String iNegotiationUUID = (String) iFilters.get(NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID);

		Negotiation negotiation = null;
		if (!Strings.isNullOrEmpty(iNegotiationUUID)) {
			INegotiationManager negotiationManager = new NegotiationManager();
			negotiation = negotiationManager.getNegotiation(iNegotiationUUID);
		}

		// this is to prevent to select invitations sent on all the profiles of the system
		boolean isOwner = false;
		if (negotiation == null){
			iFilters.put(NegotiationInvitationFilters.INVITATION_INBOX,InvitationQuery.INCOMING);
		}
		else if (negotiation != null){
			isOwner = Security.isNegotiationRole(negotiation,NegotiationRole.OWNER) 
					|| Security.isProfileAdministrator(negotiation.getOwner().getProfile().getUuid());
			if (isOwner) {
				iFilters.put(NegotiationInvitationFilters.INVITATION_INBOX,InvitationQuery.SENT);
			}
			else {
				iFilters.put(NegotiationInvitationFilters.INVITATION_INBOX,InvitationQuery.INCOMING);
			}
		}

		/*if (iFilters.isNullOrEmpty(NegotiationInvitationFields.INVITATION_STATUS)){
			iFilters.put(NegotiationInvitationFields.INVITATION_STATUS, InvitationStatus.PENDING.name());
		}*/

		invitations = invitationPersistence.list(iFilters);

		return invitations;
	}


	@Override
	public void deleteInvitation(String invitationID) {
		NegotiationInvitation invitation = invitationPersistence.readObjectByUUID(NegotiationInvitation.class, invitationID);
		Security.checkNegotiationRole(invitation.getInvitedNegotiation(), NegotiationRole.OWNER);
		if(Security.isNegotiationRole(invitation.getInvitedNegotiation(),NegotiationRole.OWNER) 
					|| Security.isProfileAdministrator(invitation.getInvitedNegotiation().getOwner().getProfile().getUuid())) {
			
			super.deleteInvitation(invitationID);
			logger.info("Deleted negotiation invitation #{}", invitation);
		}
		else {
			throw new ExAuthorizationException(ErrorKeys.NEGOTIATIONROLE_PRIVILEGES);
		}
	}

	@Override
	public NegotiationInvitation getNegotiationInvitation(Negotiator iInvitedMember,String iInvitedUserEmail,String iNegotiationUUID) {
		QueryFilters filters = new QueryFilters();
		filters.putIfNotEmpty(NegotiationInvitationFields.INVITED_EMAIL, iInvitedUserEmail);
		filters.putIfNotNull(NegotiationInvitationFields.INVITED_MEMBERSHIP, iInvitedMember != null ? iInvitedMember.getId() : null);
		filters.put(NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID, iNegotiationUUID);
		NegotiationInvitation invite = invitationPersistence.read(filters);
		return invite;
	}

	@Override
	public void updateInvitationsForNewUser(Negotiator newUser) {
		Objects.requireNonNull(newUser);
		try {
			QueryFilters filters = new QueryFilters();
			filters.put(NegotiationInvitationFields.INVITED_EMAIL, newUser.getUser().getEmail());
			List<NegotiationInvitation> inviteList = invitationPersistence
					.list(filters);
			for (NegotiationInvitation invite : inviteList) {
				invite.setInvitedMembership(newUser);
				invitationPersistence.update(invite);
			}
		} catch (Exception ex) {
			logger.warn("Failed to update inviations for newly registered user. ErrorDetail: " + ex.getStackTrace());
			throw new ExPersistentException(ex);
		}
	}

	@Override
	public void acceptNegotiationInvitationIfExists(String iInvitedUserUUID,
			String iNegotiationUUID) {
		try {
			QueryFilters filters = new QueryFilters();
			filters.put(NegotiationInvitationFilters.INVITED_MEMBERSHIP_UUID, iInvitedUserUUID);
			filters.put(NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID, iNegotiationUUID);
			NegotiationInvitation invite = invitationPersistence.read(filters);
			if (invite != null
					&& invite.getInvitedMembership().getIdentifier()
							.equals(iInvitedUserUUID)) {
				invite.setInvitationStatus(InvitationStatus.ACCEPTED);
				invite = invitationPersistence.update(invite);
				logger.info("Accepted negotiation invitation - {}", invite);
			}
		} catch (Exception ex) {
			logger.warn("Failed to update inviations. ErrorDetail: " + ex.getStackTrace());
			throw new ExPersistentException(ex);
		}
	}

	@Override
	public void doAfterAccept(AbstractInvitation iInvitation) {
		NegotiationInvitation negotiationInvitation = (NegotiationInvitation) iInvitation;
		INegotiationManager negotiationManager = new NegotiationManager();
		negotiationManager.join(negotiationInvitation.getInvitedMembership(),
				negotiationInvitation.getInvitedNegotiation().getUuid());

		createContact(negotiationInvitation, (Membership)negotiationInvitation.getInvitedMembership(), (Membership)negotiationInvitation.getInvitedNegotiation().getOwner());
		notificationManager.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_INVITATION_UPDATED, negotiationInvitation.getInvitedNegotiation(), negotiationInvitation));
		ActivityLogger.log(ContextHelper.getMembership(), Verb.ACCEPT, negotiationInvitation, Arrays.asList(negotiationInvitation.getInvitedNegotiation().getOwner()));
	}

	@Override
	public void doAfterReject(AbstractInvitation iInvitation) {
		NegotiationInvitation negotiationInvitation = (NegotiationInvitation) iInvitation;
		notificationManager.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_INVITATION_UPDATED, negotiationInvitation.getInvitedNegotiation(), negotiationInvitation));
		ActivityLogger.log(ContextHelper.getMembership(), Verb.REJECT, negotiationInvitation, Arrays.asList(negotiationInvitation.getInvitedNegotiation().getOwner()));
	}

	@Override
	public void sendDraftInvitations(Negotiation negotiation) {
		if(negotiation != null) {
			List<NegotiationInvitation> invitations = new ArrayList<>();
			QueryFilters filters = QueryFilters.create(NegotiationInvitationFields.INVITATION_STATUS, InvitationStatus.DRAFT);
			filters.putIfNotNull(NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID, negotiation.getUuid());
			invitations = invitationPersistence.list(filters);
			for(NegotiationInvitation invitation : invitations) {
				updateInvitationStatus(invitation, InvitationStatus.PENDING);
				logger.info("Updated negotiation invitation - {}", invitation);

				notificationManager.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_INVITATION_RECEIVED, negotiation, invitation));
				ActivityLogger.log(invitation.getInvitedBy(), Verb.INVITE, invitation, Arrays.asList(invitation.getInvitedMembership()));
			}
		}
	}
}
