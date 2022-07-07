package com.exrade.api.impl;

import com.exrade.api.NegotiationAPI;
import com.exrade.core.ExLogger;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.common.FAQ;
import com.exrade.models.common.Image;
import com.exrade.models.common.Meta;
import com.exrade.models.contract.Contract;
import com.exrade.models.history.NegotiationEvent;
import com.exrade.models.messaging.AdmissionRequest;
import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.models.negotiation.*;
import com.exrade.models.processmodel.ExradeProcessModel;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.security.NegotiationRole;
import com.exrade.models.userprofile.security.PlatformRole;
import com.exrade.platform.exception.*;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.contract.ContractManager;
import com.exrade.runtime.contract.IContractManager;
import com.exrade.runtime.filemanagement.FileManager;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.INegotiationTemplateManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.negotiation.NegotiationTemplateManager;
import com.exrade.runtime.negotiation.persistence.NegotiationQuery.NegotiationQFilters;
import com.exrade.runtime.negotiation.persistence.NegotiationQuery.NegotiationStageFilter;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.processmodel.IProcessModelManager;
import com.exrade.runtime.processmodel.ProcessModelManager;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.AnalyticRequestFields;
import com.exrade.runtime.rest.RestParameters.NegotiationFields;
import com.exrade.runtime.rest.RestParameters.NegotiationFilters;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.workgroup.IWorkGroupManager;
import com.exrade.runtime.workgroup.WorkGroupManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.google.common.base.Strings;
import org.apache.commons.lang3.time.DateUtils;

import java.util.*;

public class NegotiationManagerAdapter implements NegotiationAPI {

	private INegotiationManager manager = new NegotiationManager();
	private INegotiationTemplateManager negotiationTemplateManager = new NegotiationTemplateManager();
	private IContractManager contractManager = new ContractManager();
	private IWorkGroupManager workgroupManager = new WorkGroupManager();

	@Override
	public boolean acceptAdmission(ExRequestEnvelope request, String iUserID, String iNegotiationID,
			String admissionUUID) {
		ContextHelper.initContext(request);
		return manager.acceptAdmission(iUserID, iNegotiationID, admissionUUID);
	}

	@Override
	public String addFAQ(ExRequestEnvelope request, String iNegotiationID, String iQuestion, String iAnswer) {
		ContextHelper.initContext(request);
		return manager.addFAQ(iNegotiationID, iQuestion, iAnswer);
	}

	@Override
	public void cancel(ExRequestEnvelope request, String iNegotiationID, String iReason) {
		ContextHelper.initContext(request);
		Negotiation negotiation = manager.getNegotiation(iNegotiationID);
		if (negotiation != null) {
			manager.cancel(iNegotiationID, iReason);
		} else {
			negotiationTemplateManager.cancel(iNegotiationID, iReason);
		}
	}

	@Override
	public void cancelByMembership(ExRequestEnvelope request, Membership membership, String iReason) {
		ContextHelper.initContext(request);
		manager.cancelByMembership(membership, iReason);
	}

	@Override
	public String createAndPublishNegotiation(ExRequestEnvelope request, NegotiationParameter negotiationParameter) {
		ContextHelper.initContext(request);
		INegotiation negotiation = null;
		if (!negotiationParameter.isNegotiationTemplate)
			negotiation = manager.createAndPublishNegotiation(negotiationParameter);
		else
			negotiation = negotiationTemplateManager.createAndPublishNegotiation(negotiationParameter);

		handlePostNegotiationUpdateEvent(negotiation);
		return negotiation.getUuid();
	}

	@Override
	public String createNegotiationDraft(ExRequestEnvelope request, NegotiationParameter negotiationParameter) {
		ContextHelper.initContext(request);
		INegotiation negotiation = null;
		if (!negotiationParameter.isNegotiationTemplate)
			negotiation = manager.createNegotiationDraft(negotiationParameter);
		else
			negotiation = negotiationTemplateManager.createNegotiationDraft(negotiationParameter);

		handlePostNegotiationUpdateEvent(negotiation);
		return negotiation.getUuid();
	}

	@Override
	public void updateNegotiationDraft(ExRequestEnvelope request, String negotiationUUID,
			NegotiationParameter negotiationParameter) {
		ContextHelper.initContext(request);
		INegotiation negotiation = null;
		if (!negotiationParameter.isNegotiationTemplate)
			negotiation = manager.updateNegotiationDraft(negotiationParameter, negotiationUUID);
		else
			negotiation = negotiationTemplateManager.updateNegotiationDraft(negotiationParameter, negotiationUUID);

		handlePostNegotiationUpdateEvent(negotiation);
	}

	@Override
	public void discardNegotiationDraft(ExRequestEnvelope request, String iNegotiationID) {
		ContextHelper.initContext(request);
		Negotiation negotiation = manager.getNegotiationDraft(iNegotiationID);

		if (negotiation != null) {
			Security.checkNegotiationDraftAccess(negotiation, ContextHelper.getMembership());
			handlePreNegotiationDeleteEvent(negotiation);
			manager.discardNegotiationDraft(negotiation);
		} else {
			NegotiationTemplate negotiationTemplate = negotiationTemplateManager.getNegotiation(iNegotiationID);
			if (negotiationTemplate != null) {
				Security.checkNegotiationDraftAccess(negotiationTemplate, ContextHelper.getMembership());
				handlePreNegotiationDeleteEvent(negotiationTemplate);
				negotiationTemplateManager.discardNegotiationDraft(negotiationTemplate);
			}
		}
		// TODO: handle delete
	}

	@Override
	public void deleteFAQ(ExRequestEnvelope request, String iNegotiationID, String iFaqUUID) {
		ContextHelper.initContext(request);
		manager.deleteFAQ(iNegotiationID, iFaqUUID);
	}

	@Override
	public void deleteNegotiation(ExRequestEnvelope request, String negotiationID) {
		ContextHelper.initContext(request);

		if (!Security.checkRole(ContextHelper.getMembership(),
				Arrays.asList(PlatformRole.SUPERADMIN, PlatformRole.MODERATOR))) {
			Security.checkNegotiationRole(negotiationID, NegotiationRole.OWNER);
		}

		Negotiation negotiation = manager.getNegotiationDraft(negotiationID);
		handlePreNegotiationDeleteEvent(negotiation);
		manager.deleteNegotiation(negotiationID);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean fireTransition(ExRequestEnvelope request, String iNegotiationID, String iTransitionName,
			String note) {
		ContextHelper.initContext(request);

		boolean success = false;
		boolean fireTransitionSuccess = false;

		for (int retryCount = 0; retryCount < RestParameters.MAX_RETRY; retryCount++) {
			try {
				fireTransitionSuccess = manager.fireTransitionWithNote(iNegotiationID, iTransitionName, note);
				success = true;
				break;
			} catch (ExConcurrentModificationException ex) {
				ExLogger.get().error("Concurrent modification error!", ex);
			}
		}

		if (!success)
			throw new ExException("Failed to complete the action. Please try again");

		if (fireTransitionSuccess) {
			try {
				NotificationManager notificationManager = new NotificationManager();
				List<NotificationEvent> notificationEvents = ContextHelper.retrieveAndClearNotificationEvents();
				for (NotificationEvent notificationEvent : notificationEvents)
					notificationManager.process(notificationEvent);
			} catch (Exception ex) {
				ExLogger.get().error("Error processing queued notification", ex);
			}
		}

		return fireTransitionSuccess;
	}

	@Override
	public List<AdmissionRequest> getAdmissionRequests(ExRequestEnvelope request, String iUserID, String iNegotiationID,
			String messageStatus) {
		ContextHelper.initContext(request);
		return manager.getAdmissionRequests(iUserID, iNegotiationID, messageStatus);
	}

	@Override
	public List<FAQ> getFAQ(ExRequestEnvelope request, String iNegotiationID, String iInvitationCode) {
		ContextHelper.initContext(request);
		return manager.getFAQ(iNegotiationID, iInvitationCode);
	}

	@Override
	public List<NegotiationEvent> getLogEventList(ExRequestEnvelope request, String iUserUUID, String iNegotiationID) {
		ContextHelper.initContext(request);
		return manager.getLogEventList(iUserUUID, iNegotiationID);
	}

	@Override
	public NegotiationMessage getMessage(ExRequestEnvelope request, String iNegotiationUUID, String messageID) {
		ContextHelper.initContext(request);
		return manager.getMessage(iNegotiationUUID, messageID);
	}

	@Override
	public List<NegotiationMessage> getMessages(ExRequestEnvelope request, String iNegotiationID) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();
		return manager.getMessages(iNegotiationID);
	}

	@Override
	public NegotiationParameter getNegotiationDraft(ExRequestEnvelope request, String iNegotiationUUID) {
		ContextHelper.initContext(request);
		Map<String, String> filters = new HashMap<>();
		filters.put(NegotiationFields.PUBLISH_STATUS, PublishStatus.DRAFT.name());
		return getNegotiationParameter(request, iNegotiationUUID, filters);
	}

	@Override
	public NegotiationParameter getNegotiationParameter(ExRequestEnvelope request, String iNegotiationUUID,
			Map<String, String> apiFilters) {
		ContextHelper.initContext(request);
		NegotiationParameter negotiationParameter = null;

		Negotiation negotiation = manager.getNegotiation(iNegotiationUUID);
		if (negotiation != null) {
			QueryFilters filters = QueryFilters.create(apiFilters);

			String publishStatus = apiFilters.get(NegotiationFields.PUBLISH_STATUS);
			if (!Strings.isNullOrEmpty(publishStatus)) {
				filters.put(NegotiationFields.PUBLISH_STATUS, Arrays.asList(PublishStatus.valueOf(publishStatus)));
			}

			negotiationParameter = manager.getNegotiationParameter(iNegotiationUUID, filters);
			if (negotiationParameter == null) {
				throw new ExNotFoundException(iNegotiationUUID);
			}
			Security.checkNegotiationParameterAccess(negotiationParameter, ContextHelper.getMembership());
		} else {
			negotiationParameter = negotiationTemplateManager.getNegotiationParameter(iNegotiationUUID);
		}

		return negotiationParameter;
	}

	@Override
	public NegotiationParameter getNegotiationParameterEmpty(ExRequestEnvelope request,
			Map<String, String> queryParameters) {
		ContextHelper.initContext(request);

		String processmodelUUID = null;
		String informationmodelUUID = null;
		String bundleUUID = queryParameters.get(NegotiationFilters.BUNDLE_UUID);
		NegotiationParameter negotiationParameter = null;
		String language = ContextHelper.getLanguage();
		if (Strings.isNullOrEmpty(bundleUUID)) {
			processmodelUUID = queryParameters.get(NegotiationFilters.PROCESSMODEL_UUID);
			informationmodelUUID = queryParameters.get(NegotiationFilters.INFORMATIONMODEL_UUID);

			if (!Strings.isNullOrEmpty(processmodelUUID) && !Strings.isNullOrEmpty(informationmodelUUID)) {
				negotiationParameter = manager.getNegotiationParameterEmptyByProcessAndInformationModel(
						processmodelUUID, informationmodelUUID, language);
			} else if (!Strings.isNullOrEmpty(processmodelUUID)) {
				negotiationParameter = manager.getNegotiationParameterEmptyByProcess(processmodelUUID, language);
			} else if (!Strings.isNullOrEmpty(informationmodelUUID)) {
				String processModelName = "false".equalsIgnoreCase(queryParameters.get("initNegotiable"))
						? ExradeProcessModel.BILATERAL_WITHOUT_INITIAL_OFFER.getName()
						: ExradeProcessModel.BILATERAL.getName();

				IProcessModelManager processModelManager = new ProcessModelManager();
				negotiationParameter = manager.getNegotiationParameterEmptyByProcessAndInformationModel(
						processModelManager.readByName(processModelName).getUuid(), informationmodelUUID, language);
			}
		} else {
			negotiationParameter = manager.getNegotiationParameterEmptyByBundle(bundleUUID, language);
		}

		if (negotiationParameter == null) {
			throw new ExParamException(ErrorKeys.BADREQUEST_MISSING_PARAMETER);
		}

		// TODO: set other fields from query parameters
		if (!Strings.isNullOrEmpty(queryParameters.get(NegotiationFields.NEGOTIATION_TYPE))) {
			negotiationParameter.negotiationType = Enum.valueOf(NegotiationType.class,
					queryParameters.get(NegotiationFields.NEGOTIATION_TYPE));
		}

		return negotiationParameter;
	}

	@Override
	public NegotiationStatus getStatus(ExRequestEnvelope request, Negotiator negotiator, String iNegotiationID) {
		ContextHelper.initContext(request);
		return manager.getStatus(negotiator, iNegotiationID);
	}

	@Override
	public NegotiationStatus getStatus(ExRequestEnvelope request, String iNegotiationID) {
		ContextHelper.initContext(request);
		return manager.getStatus(iNegotiationID);
	}

	@Override
	public NegotiationStatus getStatus(ExRequestEnvelope request, String iUserProfileUUID, String iNegotiationID) {
		ContextHelper.initContext(request);
		return manager.getStatus(iUserProfileUUID, iNegotiationID);
	}

	@Override
	public String join(ExRequestEnvelope request, String iNegotiationID, String iInvitationCode) {
		ContextHelper.initContext(request);
		return manager.join(ContextHelper.getMembership(), iNegotiationID);
	}

	@Override
	public List<INegotiationSummary> listInvitedNegotiations(ExRequestEnvelope request, QueryFilters iFilters) {
		ContextHelper.initContext(request);
		return manager.listInvitedNegotiations(iFilters);
	}

	@Override
	public List<INegotiationSummary> listInvolvedNegotiations(ExRequestEnvelope request, QueryFilters iFilters,
			String iUserID) {
		ContextHelper.initContext(request);
		return manager.listInvolvedNegotiations(iFilters, iUserID);
	}

	@Override
	public List<INegotiationSummary> listJoinedNegotiations(ExRequestEnvelope request, QueryFilters iFilters,
			String iUserID) {
		ContextHelper.initContext(request);
		return manager.listJoinedNegotiations(iFilters, iUserID);
	}

	@Override
	public List<INegotiationSummary> listNegotiations(ExRequestEnvelope request, Map<String, String> filterParams) {
		ContextHelper.initContext(request);

		QueryFilters filters = getNegotiationFilters(filterParams);
		List<INegotiationSummary> negotiationsSummary = manager.listProfiledNegotiations(filters);
		return negotiationsSummary;
	}

	private QueryFilters getNegotiationFilters(Map<String, String> filterParams) {
		QueryFilters filters = QueryFilters.create(filterParams);

		if (filterParams.get(NegotiationFilters.USER_ADMISSION_STATUS) != null) {
			String admissionStatusFilter = filterParams.get(NegotiationFilters.USER_ADMISSION_STATUS);
			filters.putIfNotNull(admissionStatusFilter, ContextHelper.getMembership());
		}

		if (filterParams.get(NegotiationQFilters.PUBLISHED) != null) {
			Date datePublication = new Date(Long.parseLong(filterParams.get(NegotiationQFilters.PUBLISHED)));
			filters.putIfNotNull(NegotiationQFilters.PUBLISHED, datePublication);
		}

		if (filterParams.get(NegotiationFields.START_DATE) != null) {
			try {
				Date dateStart = new Date(Long.parseLong(filterParams.get(NegotiationFields.START_DATE)));
				filters.putIfNotNull(NegotiationFields.START_DATE, dateStart);
			} catch (NumberFormatException ex) {
			}
		}

		try { // TODO: merge the logic of startDate and creationDate
			if (!filters.containsKey(NegotiationFields.START_DATE)
					&& filterParams.containsKey(AnalyticRequestFields.START_DATE))
				filters.put(NegotiationQFilters.CREATED_AFTER_INCLUSIVE,
						DateUtils.parseDate(filterParams.get(AnalyticRequestFields.START_DATE),
								ExConfiguration.getPropertyAsStringArray("DATE_FORMATS")));
			if (filterParams.containsKey(AnalyticRequestFields.END_DATE))
				filters.put(RestParameters.CREATION_DATE,
						DateUtils.parseDate(filterParams.get(AnalyticRequestFields.END_DATE) + " 23:59:59.999",
								ExConfiguration.getPropertyAsStringArray("DATE_TIME_FORMATS")));
		} catch (Exception ex) {
		}

		List<PublishStatus> publishStatuses = ExCollections
				.commaSeparatedToEnumList(filterParams.get(NegotiationFields.PUBLISH_STATUS), PublishStatus.class);
		filters.putIfNotEmpty(NegotiationFields.PUBLISH_STATUS, publishStatuses);

		List<PrivacyLevel> privacyLevels = ExCollections
				.commaSeparatedToEnumList(filterParams.get(NegotiationFields.PRIVACY_LEVEL), PrivacyLevel.class);
		filters.putIfNotEmpty(NegotiationFields.PRIVACY_LEVEL, privacyLevels);
		filters.putIfNotNull(NegotiationFields.CATEGORY, filterParams.get(NegotiationFields.CATEGORY));
		filters.putIfNotNull(NegotiationFields.TITLE, filterParams.get(NegotiationFields.TITLE));
		filters.putIfNotNull(NegotiationFilters.KEYWORDS, filterParams.get(NegotiationFilters.KEYWORDS));

		filters.putIfNotNull(NegotiationQFilters.COMPANY_OWNER, filterParams.get(NegotiationQFilters.COMPANY_OWNER));
		filters.putIfNotNull(NegotiationQFilters.NOT_COMPANY_OWNER,
				filterParams.get(NegotiationQFilters.NOT_COMPANY_OWNER));
		filters.putIfNotNull(NegotiationFilters.OWNER, filterParams.get(NegotiationFilters.OWNER));
		filters.putIfNotNull(NegotiationFilters.NOT_OWNER, filterParams.get(NegotiationFilters.NOT_OWNER));
		filters.putIfNotNull(NegotiationFilters.LANGUAGE, filterParams.get(NegotiationFilters.LANGUAGE));
		filters.putIfNotNull(NegotiationFilters.NEGOTIATION_TEMPLATE_UUID,
				filterParams.get(NegotiationFilters.NEGOTIATION_TEMPLATE_UUID));
		if (filterParams.containsKey(NegotiationFilters.SUPPORT_PAYMENT)) {
			filters.putIfNotNull(NegotiationFilters.SUPPORT_PAYMENT,
					Boolean.parseBoolean(filterParams.get(NegotiationFilters.SUPPORT_PAYMENT)));
		}
		List<NegotiationStageFilter> negotiationStages = ExCollections.commaSeparatedToEnumList(
				filterParams.get(NegotiationFilters.NEGOTIATION_STAGE), NegotiationStageFilter.class);
		filters.putIfNotEmpty(NegotiationFilters.NEGOTIATION_STAGE, negotiationStages);
		List<String> processModels = ExCollections
				.commaSeparatedToList(filterParams.get(NegotiationFilters.PROCESSMODELS));
		filters.putIfNotEmpty(NegotiationFilters.PROCESSMODELS, processModels);

		String systemTags = filterParams.get(NegotiationFilters.SYSTEM_TAGS);
		if (!Strings.isNullOrEmpty(systemTags)) {
			filters.put(NegotiationFilters.SYSTEM_TAGS, ExCollections.commaSeparatedToList(systemTags.toLowerCase()));
		}
		filters.putIfNotNull(NegotiationFilters.COMPANION_MEMBER,
				filterParams.get(NegotiationFilters.COMPANION_MEMBER));
		filters.putIfNotNull(NegotiationFilters.COMPANION_PROFILE,
				filterParams.get(NegotiationFilters.COMPANION_PROFILE));
		filters.putIfNotNull(NegotiationFilters.NOT_COMPANION_MEMBER,
				filterParams.get(NegotiationFilters.NOT_COMPANION_MEMBER));
		filters.putIfNotNull(NegotiationFilters.MEMBERSHIP_STATUS,
				filterParams.get(NegotiationFilters.MEMBERSHIP_STATUS));
		filters.putIfNotNull(NegotiationFilters.PROFILE_OWNER, filterParams.get(NegotiationFilters.PROFILE_OWNER));
		if (filterParams.containsKey(NegotiationFilters.ADMISSION_OPEN)) {
			filters.putIfNotNull(NegotiationFilters.ADMISSION_OPEN,
					Boolean.parseBoolean(filterParams.get(NegotiationFilters.ADMISSION_OPEN)));
		}
		if (filterParams.containsKey(NegotiationFilters.EXCLUDE_OWNED_COMPANY_DEALS)) {
			filters.putIfNotNull(NegotiationFilters.EXCLUDE_OWNED_COMPANY_DEALS,
					Boolean.parseBoolean(filterParams.get(NegotiationFilters.EXCLUDE_OWNED_COMPANY_DEALS)));
		}
		if (filterParams.containsKey(NegotiationFilters.INCLUDE_ARCHIVED)) {
			filters.putIfNotNull(NegotiationFilters.INCLUDE_ARCHIVED,
					Boolean.parseBoolean(filterParams.get(NegotiationFilters.INCLUDE_ARCHIVED)));
		}

		List<NegotiationType> negotiationTypes = ExCollections
				.commaSeparatedToEnumList(filterParams.get(NegotiationFields.NEGOTIATION_TYPE), NegotiationType.class);
		filters.putIfNotEmpty(NegotiationFields.NEGOTIATION_TYPE, negotiationTypes);

		filters.putIfNotNull(QueryParameters.SORT, filterParams.get(QueryParameters.SORT));
		return filters;
	}

	@Override
	public List<INegotiationSummary> listOwnedNegotiations(ExRequestEnvelope request, QueryFilters iFilters,
			String iUserUUID) {
		ContextHelper.initContext(request);
		return manager.listOwnedNegotiations(iFilters, iUserUUID);
	}

	@Override
	public boolean markAccepted(ExRequestEnvelope request, String iNegotiationID, String iOfferToAcceptID,
			boolean exclusive) {
		ContextHelper.initContext(request);
		return manager.markAccepted(iNegotiationID, iOfferToAcceptID, exclusive);
	}

	@Override
	public boolean markNotAccepted(ExRequestEnvelope request, String iNegotiationID, String iOfferToNotAcceptID) {
		ContextHelper.initContext(request);
		return manager.markNotAccepted(iNegotiationID, iOfferToNotAcceptID);
	}

	@Override
	public boolean rejectAdmission(ExRequestEnvelope request, String iUserID, String iNegotiationID,
			String admissionUUID) {
		ContextHelper.initContext(request);
		return manager.rejectAdmission(iUserID, iNegotiationID, admissionUUID);
	}

	@Override
	public boolean removeParticipant(ExRequestEnvelope request, String userID, String negotiationID,
			String participantID) {
		ContextHelper.initContext(request);
		return manager.removeParticipant(userID, negotiationID, participantID);
	}

	@Override
	public void sendInfoMessage(ExRequestEnvelope request, String userID, String negotiationID, String content,
			List<String> receiverIDs, List<String> files) {
		ContextHelper.initContext(request);

		boolean success = false;

		for (int retryCount = 0; retryCount < RestParameters.MAX_RETRY; retryCount++) {
			try {
				manager.sendInfoMessage(userID, negotiationID, content, receiverIDs, files);
				success = true;
				break;
			} catch (ExConcurrentModificationException ex) {
				ExLogger.get().error("Concurrent modification error!", ex);
			}
		}

		if (!success)
			throw new ExException("Failed to complete the action. Please try again");
	}

	@Override
	public void sendInfoMessageToAll(ExRequestEnvelope request, String userID, String negotiationID, String content,
			List<String> files) {
		ContextHelper.initContext(request);

		boolean success = false;

		for (int retryCount = 0; retryCount < RestParameters.MAX_RETRY; retryCount++) {
			try {
				manager.sendInfoMessageToAll(userID, negotiationID, content, files);
				success = true;
				break;
			} catch (ExConcurrentModificationException ex) {
				ExLogger.get().error("Concurrent modification error!", ex);
			}
		}

		if (!success)
			throw new ExException("Failed to complete the action. Please try again");
	}

	@Override
	public void updateMessageScore(ExRequestEnvelope request, String negotiationID, String evaluableMessageID,
			int score) {
		ContextHelper.initContext(request);
		manager.updateMessageScore(negotiationID, evaluableMessageID, score);
	}

	@Override
	public NegotiationParameter copy(ExRequestEnvelope request, String uuid) {
		ContextHelper.initContext(request);
		return manager.copy(uuid);
	}

	@Override
	public void publishNegotiation(ExRequestEnvelope request, String negotiationUUID) {
		ContextHelper.initContext(request);
		Negotiation negotiation = manager.getNegotiation(negotiationUUID);
		manager.publishNegotiation(negotiation);
	}

	@Override
	public void updateAndPublishNegotiation(ExRequestEnvelope requestEnvelope, String iNegotiationUUID,
			NegotiationParameter negotiationParameter) {
		ContextHelper.initContext(requestEnvelope);
		if (negotiationParameter != null) {
			if (!negotiationParameter.isNegotiationTemplate) {
				manager.publishNegotiation(manager.updateNegotiationDraft(negotiationParameter, iNegotiationUUID));
			} else {
				negotiationTemplateManager.publishNegotiation(
						negotiationTemplateManager.updateNegotiationDraft(negotiationParameter, iNegotiationUUID));
			}

		}

	}

	@Override
	public void updateNegotiation(ExRequestEnvelope requestEnvelope, String iNegotiationUUID,
			NegotiationParameter negotiationParameter) {
		ContextHelper.initContext(requestEnvelope);
		manager.updateNegotiation(negotiationParameter, iNegotiationUUID);
	}

	@Override
	public Meta getNegotiationSchema(ExRequestEnvelope requestEnvelope, String iNegotiationUUID) {
		ContextHelper.initContext(requestEnvelope);
		Negotiation negotiation = manager.getNegotiation(iNegotiationUUID);
		if (negotiation != null) {
			return manager.getNegotiationSchema(negotiation);
		} else {
			return negotiationTemplateManager
					.getNegotiationSchema(negotiationTemplateManager.getNegotiation(iNegotiationUUID));
		}

	}

	@Override
	public String join(ExRequestEnvelope requestEnvelope, String iNegotiationID, String iInvitationCode,
			List<String> signers) {
		ContextHelper.initContext(requestEnvelope);
		return manager.join(ContextHelper.getMembership(), iNegotiationID, signers);
	}

	@Override
	public INegotiationSummary getNegotiationSummary(ExRequestEnvelope request, String iNegotiationID,
			String iInvitationCode) {
		ContextHelper.initContext(request);

		INegotiationSummary negotiationSummary = manager.getNegotiationSummary(iNegotiationID, iInvitationCode);

		if (negotiationSummary == null) {
			negotiationSummary = negotiationTemplateManager.getNegotiationSummary(iNegotiationID);
		}

		return negotiationSummary;
	}

	@Override
	public void toggleArchiveStatus(ExRequestEnvelope requestEnvelope, String iNegotiationID) {
		ContextHelper.initContext(requestEnvelope);
		manager.toggleArchiveStatus(iNegotiationID);
	}

	@Override
	public String generateNegotiationFromPreconfigured(ExRequestEnvelope requestEnvelope,
			String iNegotiationTemplateUUID) {
		ContextHelper.initContext(requestEnvelope);
		NegotiationParameter negotiationParameter = negotiationTemplateManager
				.getNegotiationParameter(iNegotiationTemplateUUID);
		if (negotiationParameter.publishStatus == PublishStatus.ACTIVE) {
			negotiationParameter.publicationDate = TimeProvider.now();
			negotiationParameter.startDate = TimeProvider.now();
			negotiationParameter.privacyLevel = PrivacyLevel.PRIVATE;
			negotiationParameter.editingContractByParticipantAllowed = false;

			FileManager fileManager = new FileManager();
			List<String> filesCopy = new ArrayList<>();
			List<Image> imagesCopy = new ArrayList<>();

			for (String filename : negotiationParameter.files) {
				filesCopy.add(fileManager.copyFile(filename));
			}

			for (Image image : negotiationParameter.images) {
				imagesCopy.add(fileManager.copyImage(image));
			}
			negotiationParameter.files = filesCopy;
			negotiationParameter.images = imagesCopy;

			Negotiation negotiation = manager.createNegotiationDraft(negotiationParameter);
			negotiation.setNegotiationTemplateUUID(iNegotiationTemplateUUID);
			negotiation = manager.publishNegotiation(negotiation);
			// manager.storeNegotiation(negotiation);

			return negotiation.getUuid();
		} else {
			throw new ExException("Not active!");
		}
	}

	@Override
	public INegotiation getNegotiationByUUID(ExRequestEnvelope requestEnvelope, String uuid) {
		ContextHelper.initContext(requestEnvelope);
		Negotiation negotiation = manager.getNegotiation(uuid);
		if(negotiation != null)
			return negotiation;
		else
			return negotiationTemplateManager.getNegotiation(uuid);
	}

	private void handlePostNegotiationUpdateEvent(INegotiation negotiation) {
		Map<String, Object> customFields = negotiation.getCustomFields();
		if (customFields != null) {
			try {
				if (customFields.get(NegotiationFields.CustomFields.WORKGROUP_UUID) != null && !Strings
						.isNullOrEmpty(customFields.get(NegotiationFields.CustomFields.WORKGROUP_UUID).toString())) {
					workgroupManager.addNegotiation(
							customFields.get(NegotiationFields.CustomFields.WORKGROUP_UUID).toString(),
							negotiation.getUuid());
				}
			} catch (Exception ex) {
				ExLogger.get().warn("Failed to add negotiation: {} into workgroup: {}", negotiation.getUuid(),
						customFields.get(NegotiationFields.CustomFields.WORKGROUP_UUID));
			}

			try {
				if (customFields.get(NegotiationFields.CustomFields.CONTRACT_UUID) != null && !Strings
						.isNullOrEmpty(customFields.get(NegotiationFields.CustomFields.CONTRACT_UUID).toString())) {
					Contract contract = contractManager.getContractByUUID(
							customFields.get(NegotiationFields.CustomFields.CONTRACT_UUID).toString());
					if (negotiation.getNegotiationType() == NegotiationType.DOCUMENT
							&& !contract.getSupportingDocumentNegotiationUUIDs().contains(negotiation.getUuid())) {
						contract.getSupportingDocumentNegotiations().add((Negotiation) negotiation);
						contractManager.updateContract(contract);
					}
					if (negotiation.getNegotiationType() == NegotiationType.AMENDMENT
							&& !contract.getAmendmentContractNegotiationUUIDs().contains(negotiation.getUuid())) {
						contract.getAmendmentContractNegotiations().add((Negotiation) negotiation);
						contractManager.updateContract(contract);
					} else if (negotiation.isTemplate()
							&& !contract.getNegotiationTemplateUUIDs().contains(negotiation.getUuid())) {
						contract.getNegotiationTemplates().add((NegotiationTemplate) negotiation);
						contractManager.updateContract(contract);
					}

				}
			} catch (Exception ex) {
				ExLogger.get().warn("Failed to add negotiation: {} into workgroup: {}", negotiation.getUuid(),
						customFields.get(NegotiationFields.CustomFields.CONTRACT_UUID));
			}
		}
	}

	private void handlePreNegotiationDeleteEvent(INegotiation negotiation) {
		Map<String, Object> customFields = negotiation.getCustomFields();
		if (customFields != null) {
			try {
				if (customFields.get(NegotiationFields.CustomFields.WORKGROUP_UUID) != null && !Strings
						.isNullOrEmpty(customFields.get(NegotiationFields.CustomFields.WORKGROUP_UUID).toString())) {
					workgroupManager.removeNegotiation(
							customFields.get(NegotiationFields.CustomFields.WORKGROUP_UUID).toString(),
							negotiation.getUuid());
				}
			} catch (Exception ex) {
				ExLogger.get().warn("Failed to add negotiation: {} into workgroup: {}", negotiation.getUuid(),
						customFields.get(NegotiationFields.CustomFields.WORKGROUP_UUID));
			}

			try {
				if (customFields.get(NegotiationFields.CustomFields.CONTRACT_UUID) != null && !Strings
						.isNullOrEmpty(customFields.get(NegotiationFields.CustomFields.CONTRACT_UUID).toString())) {
					Contract contract = contractManager.getContractByUUID(
							negotiation.getCustomFields().get(NegotiationFields.CustomFields.CONTRACT_UUID).toString());
					if (negotiation.getNegotiationType() == NegotiationType.DOCUMENT
							&& contract.getSupportingDocumentNegotiationUUIDs().contains(negotiation.getUuid())) {
						contract.getSupportingDocumentNegotiations().remove((Negotiation) negotiation);
						contractManager.updateContract(contract);
					}
					if (negotiation.getNegotiationType() == NegotiationType.AMENDMENT
							&& contract.getAmendmentContractNegotiationUUIDs().contains(negotiation.getUuid())) {
						contract.getAmendmentContractNegotiations().remove((Negotiation) negotiation);
						contractManager.updateContract(contract);
					} else if (negotiation.isTemplate()
							&& contract.getNegotiationTemplateUUIDs().contains(negotiation.getUuid())) {
						contract.getNegotiationTemplates().remove((NegotiationTemplate) negotiation);
						contractManager.updateContract(contract);
					}

				}
			} catch (Exception ex) {
				ExLogger.get().warn("Failed to add negotiation: {} into workgroup: {}", negotiation.getUuid(),
						customFields.get(NegotiationFields.CustomFields.CONTRACT_UUID));
			}
		}
	}

}
