package com.exrade.runtime.contract;

import com.exrade.core.ExLogger;
import com.exrade.models.Role;
import com.exrade.models.activity.ObjectType;
import com.exrade.models.activity.Verb;
import com.exrade.models.contract.*;
import com.exrade.models.informationmodel.Attribute;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.models.informationmodel.Tag;
import com.exrade.models.messaging.Agreement;
import com.exrade.models.messaging.Offer;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.negotiation.NegotiationType;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.signatures.NegotiationSignatureContainer;
import com.exrade.models.signatures.RegisteredSignedDocument;
import com.exrade.models.upcomingevent.UpcomingLifecycleEventType;
import com.exrade.models.upcomingevent.dto.UpcomingLifecycleEventCreateDTO;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.security.ContractRole;
import com.exrade.platform.exception.*;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.contract.persistence.ContractPersistenceManager;
import com.exrade.runtime.contract.persistence.ContractQuery;
import com.exrade.runtime.contract.persistence.ContractSearchSummaryQuery;
import com.exrade.runtime.filemanagement.FileManager;
import com.exrade.runtime.filemanagement.IFileManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.ContractNotificationEvent;
import com.exrade.runtime.rest.RestParameters.ContractLifecycleSettingFields;
import com.exrade.runtime.security.RoleManager;
import com.exrade.runtime.signatures.ISignatureManager;
import com.exrade.runtime.signatures.SignatureManager;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.upcomingevent.IUpcomingLifecycleEventManager;
import com.exrade.runtime.upcomingevent.UpcomingLifecycleEventManager;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.DateUtil;
import com.exrade.util.ExCollections;
import com.google.common.base.Strings;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ContractManager implements IContractManager {
    private static final String LAST_SIGNATURE_DATE = "LAST_SIGNATURE_DATE";

    private final ContractPersistenceManager persistenceManager = new ContractPersistenceManager();
    private final NotificationManager notificationManager = new NotificationManager();
    private final IMembershipManager membershipManager = new MembershipManager();
    private final IFileManager fileManager = new FileManager();
    private final IUpcomingLifecycleEventManager upcomingLifecycleEventManager = new UpcomingLifecycleEventManager();

    @Override
    public Contract createContract(Contract iContract) {
        Membership requestorMembership = (Membership) ContextHelper.getMembership();
        if (requestorMembership == null)
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

        if (iContract == null)
            throw new ExParamException(ErrorKeys.PARAM_INVALID);

        generateLifecycleEvents(iContract);
        Contract createdContract = persistenceManager.create(iContract);
        fileManager.updateFileMetadata(createdContract);

        notificationManager.process(new ContractNotificationEvent(NotificationType.CONTRACT_CREATED, createdContract));
        ActivityLogger.log(ContextHelper.getMembership(), Verb.CREATE, createdContract, Collections.singletonList(membershipManager.getOwnerMembership(createdContract.getOwnerProfileUUID())));

        return createdContract;
    }

    @Override
    public Contract updateContract(Contract iContract) {
        // TODO check authorisation
        generateLifecycleEvents(iContract);
        persistenceManager.update(iContract);

        fileManager.updateFileMetadata(iContract);
        return this.getContractByUUID(iContract.getUuid());
    }

    @Override
    public void deleteContract(String uuid) {
        // TODO Auto-generated method stub

    }

    @Override
    public Contract getContractByUUID(String iContractUUID) {
        // TODO check authorisation
        Contract contract = persistenceManager.readObjectByUUID(Contract.class, iContractUUID);
        return contract;
    }

    @Override
    public List<Contract> listContracts(QueryFilters iFilters) {
        return persistenceManager.listObjects(new ContractQuery(), iFilters);
    }

    @Override
    public IContractMember addContractMember(String iContractUUID, String membershipUUID, String roleName) {
        Contract contract = this.getContractByUUID(iContractUUID);
        if (contract == null)
            throw new ExNotFoundException(iContractUUID);

        if (contract.findMyContractMember(membershipUUID) != null)
            return contract.findMyContractMember(membershipUUID);

        Membership membership = membershipManager.findByUUID(membershipUUID, true);
        if (membership == null)
            throw new ExNotFoundException(membershipUUID);

        RoleManager roleManager = new RoleManager();
        Role role = roleManager.findByName(roleName);
        if (role == null)
            throw new ExNotFoundException(roleName);

        ContractUserMember contractMember = new ContractUserMember();
        contractMember.setNegotiator(membership);
        contractMember.getRoles().add(role);
        contract.getOwnPartyMembers().add(contractMember);
        contract = persistenceManager.update(contract);

        notificationManager.process(new ContractNotificationEvent(NotificationType.CONTRACT_MEMBER_ADDED, contract, membership));
        ActivityLogger.log((Membership) ContextHelper.getMembership(), Verb.ADD, membership, contract, getMemberships(contract.getOwnPartyMembers()));

        return contractMember;
    }

    @Override
    public IContractMember getContractMember(String iContractUUID, String membershipUUID) {
        IContractMember contractMember = null;
        Contract contract = this.getContractByUUID(iContractUUID);
        if (contract == null)
            throw new ExNotFoundException(iContractUUID);

        Iterator<IContractMember> memberItr = contract.getOwnPartyMembers().iterator();
        while (memberItr.hasNext()) {
            IContractMember member = memberItr.next();
            if (member.getUuid().equals(membershipUUID)) {
                contractMember = member;
                break;
            }
        }
        return contractMember;
    }

    @Override
    public void removeContractMember(String iContractUUID, String membershipUUID) {
        Contract contract = this.getContractByUUID(iContractUUID);
        if (contract == null)
            throw new ExNotFoundException(iContractUUID);

        Iterator<IContractMember> memberItr = contract.getOwnPartyMembers().iterator();
        while (memberItr.hasNext()) {
            IContractMember member = memberItr.next();
            if (member.getUuid().equals(membershipUUID)) {
                memberItr.remove();
                contract = persistenceManager.update(contract);

                notificationManager.process(new ContractNotificationEvent(NotificationType.CONTRACT_MEMBER_REMOVED, contract, (Membership) ((ContractUserMember) member).getNegotiator()));
                ActivityLogger.log((Membership) ContextHelper.getMembership(), Verb.REMOVE, ((ContractUserMember) member).getNegotiator(), contract, getMemberships(contract.getOwnPartyMembers()));
                break;
            }
        }
    }

    @Override
    public IContractMember updateContractMember(String iContractUUID, String membershipUUID, String roleName) {
        IContractMember contractMember = null;
        Contract contract = this.getContractByUUID(iContractUUID);
        if (contract == null)
            throw new ExNotFoundException(iContractUUID);

        RoleManager roleManager = new RoleManager();
        Role role = roleManager.findByName(roleName);
        if (role == null)
            throw new ExNotFoundException(roleName);

        for (IContractMember member : contract.getOwnPartyMembers()) {
            if (member.getUuid().equals(membershipUUID)) {
                member.getRoles().clear();
                member.getRoles().add(role);
                contractMember = member;
                break;
            }
        }
        persistenceManager.update(contract);

        return contractMember;
    }

    @Override
    public List<SearchResultSummary> listSearchResultSummary(QueryFilters iFilters) {
        List<SearchResultSummary> searchResultSummaries = new ArrayList<SearchResultSummary>();
        searchResultSummaries.add(persistenceManager.getSearchResultSummary(new ContractSearchSummaryQuery(), iFilters));
        return searchResultSummaries;
    }

    @Override
    public void createContract(Negotiation negotiation) {
        ExLogger.get().info("Creating contract for Negotiation: {}", negotiation.getUuid());

        if (NegotiationType.DOCUMENT == negotiation.getNegotiationType()) {
            ExLogger.get().info("Negotiation type is Document, skipped creating contract for Negotiation: {}", negotiation.getUuid());
            return;
        }

        RoleManager roleManager = new RoleManager();
        // TODO check authorisation
        NegotiationSignatureContainer negSigContainer = null;
        Contract parentContractForNegotiationTemplate = getParentContractForNegotiationTemplate(negotiation.getNegotiationTemplateUUID());
        List<Agreement> agreements = negotiation.getMessageBox().getAgreements(negotiation.getOwner());
        for (Agreement agreement : agreements) {
            Contract contract = new Contract();
            contract.setTitle(negotiation.getTitle());
            contract.setDescription(negotiation.getDescription());
            contract.setCurrencyCode(negotiation.getCurrencyCode());

            contract.setStatus(ContractStatus.ACTIVE);
            contract.setNegotiation(negotiation);
            contract.setAgreementUUID(agreement.getUuid());
            contract.setBlockchainEnabled(negotiation.isBlockchainEnabled());

            Offer offer = null;
            if (agreement.getOffer() != null)
                offer = agreement.getOffer();
            else if (agreement.getOfferResponse() != null)
                offer = agreement.getOfferResponse().getOffer();
            contract.setAgreementInformationModel(offer);

            if (!Strings.isNullOrEmpty(negotiation.getFinalAmountAttribute())) {
                Attribute finalAmountAttribute = Attribute.getAttributeByName(contract.getAgreementInformationModel().getItems(), negotiation.getFinalAmountAttribute());
                if (finalAmountAttribute != null && !Strings.isNullOrEmpty(finalAmountAttribute.getValue()))
                    contract.setValue(new BigDecimal(finalAmountAttribute.getValue()));
            }

            if (parentContractForNegotiationTemplate != null) {
                contract.setParentContractUUID(parentContractForNegotiationTemplate.getUuid());
                contract.setContractType(ContractType.LINKED);
                contract.setExpiryDate(parentContractForNegotiationTemplate.getExpiryDate());
            } else
                contract.setParentContractUUID(negotiation.getParentContractUUID());


            List<String> contractFiles = new ArrayList<>();
            if (negotiation.isAgreementSigningEnabled()) {
                ISignatureManager signatureManager = new SignatureManager();
                negSigContainer = signatureManager.getRegisterSignedContainer(negotiation.getUuid());
                if (negSigContainer != null && !Strings.isNullOrEmpty(negSigContainer.getFinalSignedAgreementPDFUUID())) {
                    contract.setSignatureContainerUUID(negSigContainer.getUuid());
                    contractFiles = Arrays.asList(negSigContainer.getFinalSignedAgreementPDFUUID());
                } else {
                    ExLogger.get().error("Could not find final signed agreement pdf for Negotiation: {}, Agreement: {}", negotiation.getUuid(), agreement.getUuid());
                    return;
                }
            } else if (!Strings.isNullOrEmpty(agreement.getAgreementDocumentID())) {
                contractFiles = Arrays.asList(agreement.getAgreementDocumentID());
            } else {
                ExLogger.get().error("Could not find agreement pdf for Negotiation: {}, Agreement: {}", negotiation.getUuid(), agreement.getUuid());
                return;
            }
            contract.setContractFiles(contractFiles);

            List<ContractingParty> contractingParties = new ArrayList<>();
            for (Negotiator agreedParty : agreement.getAgreedParticipants()) {
                ContractingParty contractingParty = new ContractingParty();
                contractingParty.setProfile(agreedParty.getProfile());
                contractingParty.setCategory(negotiation.getCategory());
                Set<String> tags = new HashSet<String>();
                for (Tag tag : negotiation.getTags()) {
                    tags.add(tag.getValue());
                }
                contractingParty.setTags(tags);

                if (negotiation.isOwner(agreedParty)) {
                    contractingParty.setPartyType(ContractingPartyType.OWNER);
                    contract.setOwnerProfile(agreedParty.getProfile());
                    contract.setCreator(agreedParty);
                    if (negotiation.getCustomFields() != null && negotiation.getCustomFields().get("referenceId") != null) {
                        contractingParty.setReferenceId(negotiation.getCustomFields().get("referenceId").toString());
                    }
                    if (parentContractForNegotiationTemplate != null) {
                        contractingParty.setRisk(parentContractForNegotiationTemplate.getRisk());
                    }
                } else {
                    contractingParty.setPartyType(ContractingPartyType.PARTICIPANT);
                }

                Role managerRole = roleManager.findByName(ContractRole.MANAGER);
                ContractUserMember contractMember = new ContractUserMember();
                contractMember.setNegotiator(agreedParty);
                contractMember.getRoles().add(managerRole);
                contractingParty.getMembers().add(contractMember);
                contractingParties.add(contractingParty);
            }
            contract.setContractingParties(contractingParties);

            try {
                ContractLifecycleSetting lifecycleSetting = buildLifecycleSetting(negotiation, offer, negSigContainer);
                contract.setLifecycleSetting(lifecycleSetting);
                generateLifecycleEvents(contract);
            } catch (Exception ex) {
                ExLogger.get().error("Could not generate lifecycle setting/event for Negotiation: {}, Agreement: {}", negotiation.getUuid(), agreement.getUuid());
            }
            contract = persistenceManager.create(contract);
            fileManager.updateFileMetadata(contract);

            notificationManager.process(new ContractNotificationEvent(NotificationType.CONTRACT_CREATED, contract));
            ActivityLogger.log(ContextHelper.getMembership(), Verb.CREATE, contract, Collections.singletonList(membershipManager.getOwnerMembership(contract.getOwnerProfileUUID())));
        }
    }

    @Override
    public Contract getParentContractForNegotiationTemplate(String negotiationTemplateUUID) {
        if (!Strings.isNullOrEmpty(negotiationTemplateUUID)) {
            QueryFilters filters = QueryFilters.create("negotiationTemplateUUID", negotiationTemplateUUID);
            List<Contract> contracts = listContracts(filters);
            if (ExCollections.isNotEmpty(contracts))
                return contracts.get(0);
        }
        return null;
    }

    private List<Negotiator> getMemberships(List<IContractMember> members) {
        List<Negotiator> memberships = new ArrayList<>();
        for (IContractMember member : members) {
            if (member.getMemberObjectType().equals(ContractUserMember.MEMBER_OBJECT_TYPE)) {
                memberships.add(((ContractUserMember) member).getNegotiator());
            }
        }
        return memberships;
    }

    @Override
    public void generateLifecycleEvents(Contract contract) {
        if (contract.getLifecycleSetting() == null || contract.getLifecycleSetting().getStartDate() == null)
            return;

        validateLifecycleSetting(contract);
        
        // don't allow to reset lifecycle events if there is TRAK related events
        // TODO: allow to reset without removing Trak related events;
        if(ExCollections.isNotEmpty(contract.getLifecycleEvents())) {
        	for(ContractLifecycleEvent lifecycleEvent : contract.getLifecycleEvents()) {
        		if(lifecycleEvent.getEventType() == ContractLifecycleEventType.TRAK_DUE || lifecycleEvent.getEventType() == ContractLifecycleEventType.TRAK_START)
        			return;
        	}
        }

        Date now = TimeProvider.now();

        contract.getLifecycleEvents().clear();
        createStartEvent(contract, now);

        createRenewalEvents(contract, now);

        createEndEvent(contract, now);

        updateContractStatusAndCurrentExecutionDuration(contract, now);

        //TODO: fixme
        //createUpcomingLifeCycleEvents(contract);
    }

    private void createStartEvent(Contract contract, Date now) {
        // first event of lifecycle always must be START event
        if (ExCollections.isEmpty(contract.getLifecycleEvents())) { // create START event of the lifecycle
            //contract.setEffectiveDate(contract.getLifecycleSetting().getStartDate());
            //contract.setExpiryDate(contract.getLifecycleSetting().getEndDate());

            ContractLifecycleEvent event = new ContractLifecycleEvent();
            event.setEventType(ContractLifecycleEventType.START);
            event.setStartDate(contract.getLifecycleSetting().getStartDate());
            event.setEndDate(contract.getLifecycleSetting().getEndDate());

            contract.getLifecycleEvents().add(event);
        } else {  // update START event of the lifecycle
            ContractLifecycleEvent event = contract.getLifecycleEvents().get(0);
            if (event.getEventType() == ContractLifecycleEventType.START) {
                event.setStartDate(contract.getLifecycleSetting().getStartDate());
                event.setEndDate(contract.getLifecycleSetting().getEndDate());
            }
        }
    }

    private void createRenewalEvents(Contract contract, Date now) {
        if (contract.getLifecycleSetting().getRenewStartDate() != null) { // generate past and current renew events, future renew events should be generated by scheduled task
            ContractLifecycleEvent nextEvent = nextRenewalEvent(contract, now);
            while (nextEvent != null) {
                contract.getLifecycleEvents().add(nextEvent);
                nextEvent = nextRenewalEvent(contract, now);
                ;
            }
        }
    }

    @Override
    public void createEndEvent(Contract contract, Date now) {
        ContractLifecycleEvent latestEvent = contract.getLifecycleEvents().get(contract.getLifecycleEvents().size() - 1);

        if ((latestEvent.getEventType() == ContractLifecycleEventType.START || latestEvent.getEventType() == ContractLifecycleEventType.RENEW)
                && isEventCompleted(latestEvent, now)//start from the last event and add next renewal event
                && !hasNextRenewal(latestEvent, contract.getLifecycleSetting())
        ) {
            ContractLifecycleEvent event = new ContractLifecycleEvent();
            event.setEventType(ContractLifecycleEventType.FINISH);
            event.setStartDate(latestEvent.getEndDate());
            event.setEndDate(latestEvent.getEndDate());

            contract.getLifecycleEvents().add(event);
        }
    }

    @Override
    public void updateContractStatusAndCurrentExecutionDuration(Contract contract, Date now) {
        // update contract status and effective/expiry date based on the lifecycle events
        if (ExCollections.isNotEmpty(contract.getLifecycleEvents())) {
            ContractLifecycleEvent event = contract.getLifecycleEvents().get(contract.getLifecycleEvents().size() - 1);
            if ((event.getEventType() == ContractLifecycleEventType.START || event.getEventType() == ContractLifecycleEventType.RENEW)
                    && event.getStartDate() != null) {
                if (now.after(event.getStartDate())) {
                    if (event.getEndDate() == null)
                        contract.setStatus(ContractStatus.ACTIVE);
                    else if (now.before(event.getEndDate()))
                        contract.setStatus(ContractStatus.ACTIVE);
                    else if (now.after(event.getEndDate()))
                        contract.setStatus(ContractStatus.EXPIRED);
                } else if (now.before(event.getStartDate())) {
                    contract.setStatus(ContractStatus.ACTIVE_FUTURE);
                }
                contract.setEffectiveDate(event.getStartDate());
                contract.setExpiryDate(event.getEndDate());
            } else if (event.getEventType() == ContractLifecycleEventType.FINISH && contract.getStatus() != ContractStatus.EXPIRED) {
                contract.setStatus(ContractStatus.EXPIRED);
                contract.setExpiryDate(event.getEndDate());
            }
        }
    }

    private ContractLifecycleSetting buildLifecycleSetting(Negotiation negotiation, Offer offer, NegotiationSignatureContainer negSigContainer) {
        InformationModelTemplate informationModel = persistenceManager.readObjectByUUID(InformationModelTemplate.class, negotiation.getInformationModelDocument().getInformationTemplateUUID());
        if (informationModel.getCustomFields() != null) {
            Map<String, Object> lifecycleMappingFields = informationModel.getCustomFields();
            ContractLifecycleSetting lifecycleSetting = new ContractLifecycleSetting();

            if (lifecycleMappingFields.containsKey(ContractLifecycleSettingFields.START_DATE)
                    && !Strings.isNullOrEmpty(lifecycleMappingFields.get(ContractLifecycleSettingFields.START_DATE).toString())) {
                Date startDate = null;
            	Attribute attribute = Attribute.getAttributeByName(offer.getItems(), lifecycleMappingFields.get(ContractLifecycleSettingFields.START_DATE).toString());
                if (attribute != null && !Strings.isNullOrEmpty(attribute.getValue())) {
                	startDate = new Date(Long.parseLong(attribute.getValue()));
                } else if (lifecycleMappingFields.get(ContractLifecycleSettingFields.START_DATE).toString().equals(LAST_SIGNATURE_DATE)) {
                	startDate = getLatestDate(negSigContainer);
                }
                
                if(startDate != null) {
                	lifecycleSetting.setStartDate(DateUtil.toBeginningOfTheDay(startDate));
                }
            }

            if (lifecycleMappingFields.containsKey(ContractLifecycleSettingFields.END_DATE)
                    && !Strings.isNullOrEmpty(lifecycleMappingFields.get(ContractLifecycleSettingFields.END_DATE).toString())) {
                Attribute attribute = Attribute.getAttributeByName(offer.getItems(), lifecycleMappingFields.get(ContractLifecycleSettingFields.END_DATE).toString());
                if (attribute != null && !Strings.isNullOrEmpty(attribute.getValue())) {
                    lifecycleSetting.setEndDate(DateUtil.toEndOfTheDay(new Date(Long.parseLong(attribute.getValue()))));
                }
            }

            if (lifecycleMappingFields.containsKey(ContractLifecycleSettingFields.RENEW_START_DATE)
                    && !Strings.isNullOrEmpty(lifecycleMappingFields.get(ContractLifecycleSettingFields.RENEW_START_DATE).toString())) {
                Attribute attribute = Attribute.getAttributeByName(offer.getItems(), lifecycleMappingFields.get(ContractLifecycleSettingFields.RENEW_START_DATE).toString());
                if (attribute != null && !Strings.isNullOrEmpty(attribute.getValue())) {
                    lifecycleSetting.setRenewStartDate(DateUtil.toBeginningOfTheDay(new Date(Long.parseLong(attribute.getValue()))));
                    lifecycleSetting.setRenewable(true);
                }
            }

            if (lifecycleMappingFields.containsKey(ContractLifecycleSettingFields.RENEW_END_DATE)
                    && !Strings.isNullOrEmpty(lifecycleMappingFields.get(ContractLifecycleSettingFields.RENEW_END_DATE).toString())) {
                Attribute attribute = Attribute.getAttributeByName(offer.getItems(), lifecycleMappingFields.get(ContractLifecycleSettingFields.RENEW_END_DATE).toString());
                if (attribute != null && !Strings.isNullOrEmpty(attribute.getValue())) {
                    lifecycleSetting.setRenewEndDate(DateUtil.toEndOfTheDay(new Date(Long.parseLong(attribute.getValue()))));
                }
            }

            if (lifecycleMappingFields.containsKey(ContractLifecycleSettingFields.RENEW_OCCURRENCE_LIMIT)
                    && !Strings.isNullOrEmpty(lifecycleMappingFields.get(ContractLifecycleSettingFields.RENEW_OCCURRENCE_LIMIT).toString())) {
                Attribute attribute = Attribute.getAttributeByName(offer.getItems(), lifecycleMappingFields.get(ContractLifecycleSettingFields.RENEW_OCCURRENCE_LIMIT).toString());
                if (attribute != null && !Strings.isNullOrEmpty(attribute.getValue())) {
                    lifecycleSetting.setRenewOccurrenceLimit(Integer.parseInt(attribute.getValue()));
                }
            }

            if (lifecycleMappingFields.containsKey(ContractLifecycleSettingFields.RENEW_DURATION_VALUE)
                    && !Strings.isNullOrEmpty(lifecycleMappingFields.get(ContractLifecycleSettingFields.RENEW_DURATION_VALUE).toString())) {
                Attribute attribute = Attribute.getAttributeByName(offer.getItems(), lifecycleMappingFields.get(ContractLifecycleSettingFields.RENEW_DURATION_VALUE).toString());
                if (attribute != null && !Strings.isNullOrEmpty(attribute.getValue())) {
                    lifecycleSetting.setRenewDurationValue(Integer.parseInt(attribute.getValue()));
                }
            }

            if (lifecycleMappingFields.containsKey(ContractLifecycleSettingFields.RENEW_DURATION_UNIT)
                    && !Strings.isNullOrEmpty(lifecycleMappingFields.get(ContractLifecycleSettingFields.RENEW_DURATION_UNIT).toString())) {
                lifecycleSetting.setRenewDurationUnit(extractDurationUnit(offer.getItems(), lifecycleMappingFields.get(ContractLifecycleSettingFields.RENEW_DURATION_UNIT).toString()));
            }

            if (lifecycleMappingFields.containsKey(ContractLifecycleSettingFields.TERMINATION_NOTICE_PERIOD_VALUE)
                    && !Strings.isNullOrEmpty(lifecycleMappingFields.get(ContractLifecycleSettingFields.TERMINATION_NOTICE_PERIOD_VALUE).toString())) {
                Attribute attribute = Attribute.getAttributeByName(offer.getItems(), lifecycleMappingFields.get(ContractLifecycleSettingFields.TERMINATION_NOTICE_PERIOD_VALUE).toString());
                if (attribute != null && !Strings.isNullOrEmpty(attribute.getValue())) {
                    lifecycleSetting.setTerminationNoticePeriodValue(Integer.parseInt(attribute.getValue()));
                    lifecycleSetting.setTerminationNoticeRequired(true);
                }
            }

            if (lifecycleMappingFields.containsKey(ContractLifecycleSettingFields.TERMINATION_NOTICE_PERIOD_UNIT)
                    && !Strings.isNullOrEmpty(lifecycleMappingFields.get(ContractLifecycleSettingFields.TERMINATION_NOTICE_PERIOD_UNIT).toString())) {
                lifecycleSetting.setTerminationNoticePeriodUnit(extractDurationUnit(offer.getItems(), lifecycleMappingFields.get(ContractLifecycleSettingFields.TERMINATION_NOTICE_PERIOD_UNIT).toString()));
            }
            return lifecycleSetting;
        }

        return null;
    }

    private Date getLatestDate(NegotiationSignatureContainer negSigContainer) {
        List<RegisteredSignedDocument> registeredSignedDocuments = negSigContainer.getAllUploaded();
        List<Date> dates = new ArrayList<Date>();
        for (RegisteredSignedDocument registeredSignedDocument : registeredSignedDocuments) {
            dates.add(registeredSignedDocument.getSignatureDate());
        }
        Date latestDate = Collections.max(dates);
        return latestDate;
    }

    @Override
    public void validateLifecycleSetting(Contract contract) {
        if (contract.getLifecycleSetting() == null || contract.getLifecycleSetting().getStartDate() == null) {
            if (ExCollections.isEmpty(contract.getLifecycleEvents()))
                return;
            else
                throw new ExException("Lifecycle setting cannot be empty");
        }

        if (contract.getLifecycleSetting().getStartDate() == null)
            throw new ExException("Start date cannot be empty"); //throw new ExParamException(ErrorKeys.PARAM_INVALID, ContractLifecycleSettingFields.START_DATE);

        if (contract.getLifecycleSetting().getEndDate() != null && contract.getLifecycleSetting().getEndDate().before(contract.getLifecycleSetting().getStartDate()))
            throw new ExException("End date cannot be before start date");

        if (contract.getLifecycleSetting().getRenewStartDate() != null) {
            if (contract.getLifecycleSetting().getEndDate() == null)
                throw new ExException("Renewal cannot be set without an end date");

            if (contract.getLifecycleSetting().getRenewStartDate().before(contract.getLifecycleSetting().getEndDate()))
                throw new ExException("Renew start date cannot be before end date");

            if (contract.getLifecycleSetting().getRenewEndDate() != null && contract.getLifecycleSetting().getRenewEndDate().before(contract.getLifecycleSetting().getRenewStartDate()))
                throw new ExException("Renew end date cannot be before renew start date");

            ExLogger.get().info("{}", contract.getLifecycleSetting().getRenewOccurrenceLimit());
            if (!(contract.getLifecycleSetting().getRenewOccurrenceLimit() == -1 || contract.getLifecycleSetting().getRenewOccurrenceLimit() >= 1))
                throw new ExException("Invalid number of renewals");

            if ((contract.getLifecycleSetting().getRenewOccurrenceLimit() == -1
                    || contract.getLifecycleSetting().getRenewOccurrenceLimit() > 1)
                    && contract.getLifecycleSetting().getRenewDurationValue() < 1)
                throw new ExException("Invalid renew duration");
        }

		/*
		// if completed start event, cannot change start/end date
		if(!isValidLifecycleStartOrEndDate(contract.getLifecycleEvents(), contract.getLifecycleSetting()))
			throw new ExException("Contract start or end date cannot be changed after renewal has been started");

		// if already renewed cannot change renew start date
		if(!isValidLifecycleRenewalStartDate(contract.getLifecycleEvents(), contract.getLifecycleSetting()))
			throw new ExException("Contract renewal start date cannot be changed after renewal has been started");

		// if renew end date passed, cannot modify renewal setting
		if(!isValidLifecycleRenewalEndDate(contract.getLifecycleEvents(), contract.getLifecycleSetting()))
			throw new ExException("Contract renewal end date cannot be changed after renewal has been started");

		// renewal times cannot be less than already passed renewals
		if(!isValidLifecycleRenewalOccurenceLimit(contract.getLifecycleEvents(), contract.getLifecycleSetting()))
			throw new ExException("Contract renewal occurence limit cannot be changed");
		*/
    }

    @Override
    public ContractLifecycleEvent nextRenewalEvent(Contract contract, Date now) {
        ContractLifecycleEvent nextEvent = null;
        ContractLifecycleEvent latestEvent = contract.getLifecycleEvents().get(contract.getLifecycleEvents().size() - 1);

        if (isEventCompleted(latestEvent, now)//start from the last event and add next renewal event
                && hasNextRenewal(latestEvent, contract.getLifecycleSetting())
        ) {
            Date nextStartDate = null; // renew start date or endDate+1
            if (latestEvent.getEventType() == ContractLifecycleEventType.START)
                nextStartDate = contract.getLifecycleSetting().getRenewStartDate();
            else
                nextStartDate = DateUtils.addDays(latestEvent.getEndDate(), 1);

            Date nextEndDate = null; // start date + interval
            if (contract.getLifecycleSetting().getRenewEndDate() != null)
                nextEndDate = contract.getLifecycleSetting().getRenewEndDate();
            else if (contract.getLifecycleSetting().getRenewDurationValue() != 0) {
                if (contract.getLifecycleSetting().getRenewDurationUnit() == DurationUnit.DAY) {
                    nextEndDate = DateUtils.addDays(nextStartDate, contract.getLifecycleSetting().getRenewDurationValue());
                } else if (contract.getLifecycleSetting().getRenewDurationUnit() == DurationUnit.WEEK) {
                    nextEndDate = DateUtils.addWeeks(nextStartDate, contract.getLifecycleSetting().getRenewDurationValue());
                } else if (contract.getLifecycleSetting().getRenewDurationUnit() == DurationUnit.MONTH) {
                    nextEndDate = DateUtils.addMonths(nextStartDate, contract.getLifecycleSetting().getRenewDurationValue());
                } else if (contract.getLifecycleSetting().getRenewDurationUnit() == DurationUnit.YEAR) {
                    nextEndDate = DateUtils.addYears(nextStartDate, contract.getLifecycleSetting().getRenewDurationValue());
                }
                nextEndDate = DateUtils.addDays(nextEndDate, -1);
            }

            ContractLifecycleEvent event = new ContractLifecycleEvent();
            event.setEventType(ContractLifecycleEventType.RENEW);
            event.setStartDate(nextStartDate);
            event.setEndDate(nextEndDate);
            event.setRenewalNumber(latestEvent.getRenewalNumber() + 1);
            event.setMaxRenewal(contract.getLifecycleSetting().getRenewOccurrenceLimit());
            nextEvent = event;
        }

        return nextEvent;
    }

    private boolean isEventCompleted(ContractLifecycleEvent latestEvent, Date now) {
        return latestEvent.getEndDate() != null && now.after(latestEvent.getEndDate());
    }

    private boolean hasNextRenewal(ContractLifecycleEvent latestEvent, ContractLifecycleSetting lifecycleSetting) {
        return lifecycleSetting.getRenewOccurrenceLimit() == -1
                || latestEvent.getRenewalNumber() < lifecycleSetting.getRenewOccurrenceLimit();
    }

    private DurationUnit extractDurationUnit(List<Attribute> items, String field) {
        DurationUnit durationUnit = null;

        try {
            durationUnit = EnumUtils.getEnum(DurationUnit.class, field);
        } catch (Exception ex) {
            Attribute attribute = Attribute.getAttributeByName(items, field);
            if (attribute != null && !Strings.isNullOrEmpty(attribute.getValue())) {
                durationUnit = EnumUtils.getEnum(DurationUnit.class, attribute.getValue());
            }
        }

        return durationUnit;
    }


    private void createUpcomingLifeCycleEvents(Contract contract) {
        //generate upcoming life cycle event
        upcomingLifecycleEventManager.createFromManager(new UpcomingLifecycleEventCreateDTO(
                contract.getUuid(),
                ObjectType.CONTRACT, UpcomingLifecycleEventType.CONTRACT_EXPIRATION,
                contract.getLifecycleSetting().getStartDate(),
                contract.getContractingParties()
                        .parallelStream()
                        .map(e -> e.getMembers().get(0).getUuid())
                        .collect(Collectors.toList())
        ));
    }

/*
	private boolean isValidLifecycleStartOrEndDate(List<ContractLifecycleEvent> lifecycleEvents, ContractLifecycleSetting lifecycleSetting) {
		return lifecycleEvents.size() <= 1 ||
				(isSameDay(lifecycleSetting.getStartDate(), lifecycleEvents.get(0).getStartDate())
				&& isSameDay(lifecycleSetting.getEndDate(), lifecycleEvents.get(0).getEndDate()));
	}

	private boolean isValidLifecycleRenewalStartDate(List<ContractLifecycleEvent> lifecycleEvents, ContractLifecycleSetting lifecycleSetting) {
		return lifecycleEvents.size() <= 2
				|| isSameDay(lifecycleSetting.getRenewStartDate(), lifecycleEvents.get(1).getStartDate());
	}

	private boolean isValidLifecycleRenewalEndDate(List<ContractLifecycleEvent> lifecycleEvents, ContractLifecycleSetting lifecycleSetting) {
		return lifecycleEvents.size() <= 2
				|| isSameDay(lifecycleSetting.getRenewEndDate(), lifecycleEvents.get(1).getEndDate())
				|| ((lifecycleEvents.size() == -1 || lifecycleEvents.size() > 2) && lifecycleSetting.getRenewEndDate() == null);
	}

	private boolean isValidLifecycleRenewalOccurenceLimit(List<ContractLifecycleEvent> lifecycleEvents, ContractLifecycleSetting lifecycleSetting) {
		return  lifecycleSetting.getRenewOccurrenceLimit() == -1
				|| lifecycleEvents.size() <= 2
				|| lifecycleEvents.get(lifecycleEvents.size()-1).getRenewalNumber() <= lifecycleSetting.getRenewOccurrenceLimit();
	}
*/
}
