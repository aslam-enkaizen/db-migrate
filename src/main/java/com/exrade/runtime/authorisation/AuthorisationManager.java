package com.exrade.runtime.authorisation;

import com.exrade.models.activity.Verb;
import com.exrade.models.authorisation.*;
import com.exrade.models.informationmodel.Clause;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.negotiation.NegotiationParameter;
import com.exrade.models.negotiation.PublishStatus;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthorizationException;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.exception.ExParamException;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.authorisation.persistence.AuthorisationPersistenceManager;
import com.exrade.runtime.authorisation.persistence.AuthorisationRequestQuery;
import com.exrade.runtime.clause.ClauseManager;
import com.exrade.runtime.informationmodel.IInformationModelManager;
import com.exrade.runtime.informationmodel.InformationModelManager;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.negotiation.NegotiationTemplateManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.AuthorisationNotificationEvent;
import com.exrade.runtime.rest.RestParameters.AuthorisationFields;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.List;

public class AuthorisationManager implements IAuthorisationManager {

	private AuthorisationPersistenceManager authorisationPersistentManager;
	private IInformationModelManager informationModelManager = new InformationModelManager();
	private INegotiationManager negotiationManager = new NegotiationManager();
	private NotificationManager notificationManager = new NotificationManager();
	private final NegotiationTemplateManager negotiationTemplateManager = new NegotiationTemplateManager();
	private ClauseManager clauseManager = new ClauseManager();

	public AuthorisationManager() {
		this(new AuthorisationPersistenceManager());
	}

	public AuthorisationManager(AuthorisationPersistenceManager iTemplatePersistentManager) {
		this.authorisationPersistentManager = iTemplatePersistentManager;
	}

	@Override
	public AuthorisationRequest createAuthorisationRequest(
			AuthorisationRequest iRequest) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.AUTHORISATIONS);

		iRequest.setCreationDate(TimeProvider.now());
		
		QueryFilters filter = QueryFilters.create(AuthorisationFields.OBJECTTYPE, iRequest.getObjectType());
		filter.put(AuthorisationFields.OBJECTID, iRequest.getObjectID());
		
		List<AuthorisationRequest> existingRequests = listAuthorisationRequests(filter);
		if(ExCollections.isNotEmpty(existingRequests)) {
			for(AuthorisationRequest request : existingRequests) {
				if(request.getStatus() == AuthorisationStatus.PENDING) {
					throw new ExException("There is a pending AuthorisationRequest for this item");
				}
				if(request.getStatus() == AuthorisationStatus.ACCEPTED) {
					throw new ExException("AuthorisationRequest has been already accepted for this item");
				}
			}
		}
		
		if(iRequest.getObjectType() == AuthorisationObjectType.INFORMATION_MODEL_TEMPLATE){
			InformationModelTemplate template = informationModelManager.readByUUID(iRequest.getObjectID());
			template.setPublishStatus(PublishStatus.PENDING);
			template.setPublicationDate(TimeProvider.now());
			//template.setModelVersion(template.getModelVersion() + 1);
			informationModelManager.update(template.getUuid(), template);
			iRequest.setTitle(template.getTitle());
		}
		else if(iRequest.getObjectType() == AuthorisationObjectType.NEGOTIATION){
			NegotiationParameter negotiationParameter = negotiationManager.getNegotiationParameter(iRequest.getObjectID(), new QueryFilters());
			negotiationParameter.publishStatus = PublishStatus.PENDING;
			negotiationManager.updateNegotiationDraft(negotiationParameter, iRequest.getObjectID());
			iRequest.setTitle(negotiationParameter.title);
		}
		else if(iRequest.getObjectType() == AuthorisationObjectType.NEGOTIATION_TEMPLATE){
			NegotiationParameter negotiationParameter = negotiationTemplateManager.getNegotiationParameter(iRequest.getObjectID());
			negotiationParameter.publishStatus=PublishStatus.PENDING;
			negotiationTemplateManager.updateNegotiationDraft(negotiationParameter, iRequest.getObjectID());
			iRequest.setTitle(negotiationParameter.title);
		}
		else if(iRequest.getObjectType() == AuthorisationObjectType.NEGOTIATION_MESSAGE){
			if(iRequest.getExtraContext() != null && !Strings.isNullOrEmpty(iRequest.getExtraContext().get("negotiationUUID").toString())) {
				Negotiation negotiation = negotiationManager.getNegotiation(iRequest.getExtraContext().get("negotiationUUID").toString());
				iRequest.setTitle(negotiation.getTitle());
			}
		}
		else if(iRequest.getObjectType() == AuthorisationObjectType.CLAUSE){
			Clause clause = clauseManager.getClause(iRequest.getObjectID());
			clause.setPublicationStatus(PublishStatus.PENDING);
			clauseManager.updateClause(clause);
			iRequest.setTitle(clause.getTitle());
		}
		else{
			throw new ExParamException(ErrorKeys.PARAM_INVALID, "objectType");
		}
		
		AuthorisationRequest createdAuthorisationRequest = authorisationPersistentManager.create(iRequest);
		
		notificationManager.process(new AuthorisationNotificationEvent(NotificationType.AUTHORISATION_REQUESTED, createdAuthorisationRequest));
		ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.REQUEST, createdAuthorisationRequest, createdAuthorisationRequest.getReceivers());
		
		return createdAuthorisationRequest;
	}

	@Override
	public AuthorisationRequest updateAuthorisationRequest(String iRequestUUID,
			Negotiator iResponder, AuthorisationStatus iStatus, String iNote) {
		//checking profile permission
		//Security.hasAccessPermission(Security.ProfilePermissions.AUTHORISATIONS);

		AuthorisationRequest request = this.getAuthorisationRequestByUUID(iRequestUUID);
		
		if(!request.getReceivers().contains(iResponder))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
		
		for(AuthorisationResponse response : request.getResponses()){
			if(response.getSender().equals(iResponder))
				throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
		}
		
		if(iStatus == AuthorisationStatus.ACCEPTED)
			ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.ACCEPT, request, Arrays.asList(request.getSender()));
		else
			ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.REJECT, request, Arrays.asList(request.getSender()));
		
		AuthorisationResponse authorisationResponse = new AuthorisationResponse(iResponder, iStatus, iNote);
		request.getResponses().add(authorisationResponse);
		request.setUpdateDate(TimeProvider.now());
		
		if(request.getAcceptanceRule() == AcceptanceRule.ANY){
			if(iStatus == AuthorisationStatus.ACCEPTED)
				request.setStatus(AuthorisationStatus.ACCEPTED);
			else{
				boolean hasPending = false;
				for(AuthorisationResponse response : request.getResponses()){
					if(response.getStatus() == AuthorisationStatus.PENDING){
						hasPending = true;
						break;
					}
				}
				if(!hasPending){
					request.setStatus(AuthorisationStatus.REJECTED);
				}
			}
		}
		else if(request.getAcceptanceRule() == AcceptanceRule.ALL) {
			if(request.getReceivers().size() == request.getResponses().size()){
				boolean allAccepted = true;
				for(AuthorisationResponse response : request.getResponses()){
					if(response.getStatus() == AuthorisationStatus.REJECTED){
						allAccepted = false;
						break;
					}
				}
				if (allAccepted) {
					request.setStatus(AuthorisationStatus.ACCEPTED);
				} else {
					request.setStatus(AuthorisationStatus.REJECTED);
				}
			}
		}
		
		if(request.getStatus() == AuthorisationStatus.ACCEPTED){
			ContextHelper.setUserProfile(request.getSender());
			
			if(request.getObjectType() == AuthorisationObjectType.INFORMATION_MODEL_TEMPLATE){
				InformationModelTemplate template = informationModelManager.readByUUID(request.getObjectID());
				template.setPublicationDate(TimeProvider.now());
				template.setPublishStatus(PublishStatus.ACTIVE);
				template.setModelVersion(template.getModelVersion() + 1);
				informationModelManager.update(template.getUuid(), template);
			}
			else if(request.getObjectType() == AuthorisationObjectType.NEGOTIATION){
				Negotiation negotiation = negotiationManager.getNegotiation(request.getObjectID());
				negotiationManager.publishNegotiation(negotiation);
			}
			else if(request.getObjectType() == AuthorisationObjectType.NEGOTIATION_TEMPLATE){
				NegotiationParameter negotiation = negotiationTemplateManager.getNegotiationParameter(request.getObjectID());
				negotiationTemplateManager.createAndPublishNegotiation(negotiation);
			}
			else if(request.getObjectType() == AuthorisationObjectType.NEGOTIATION_MESSAGE){
				if (request.getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID) != null
						&& request.getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_ACTION) != null) {
					String note = request.getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_NOTE) == null ? ""
							: request.getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_NOTE).toString();
					negotiationManager.fireTransitionWithNote(
							request.getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID).toString(),
							request.getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_ACTION).toString(), note);
				}
			}
			else if(request.getObjectType() == AuthorisationObjectType.CLAUSE){
				Clause clause = clauseManager.getClause(request.getObjectID());
				clause.setPublicationStatus(PublishStatus.ACTIVE);
				clauseManager.updateClause(clause);
			}
			else{
				throw new ExParamException(ErrorKeys.PARAM_INVALID, "objectType");
			}
		}
		else if(request.getStatus() == AuthorisationStatus.REJECTED){
			if(request.getObjectType() == AuthorisationObjectType.INFORMATION_MODEL_TEMPLATE){
				InformationModelTemplate template = informationModelManager.readByUUID(request.getObjectID());
				template.setPublishStatus(PublishStatus.DRAFT);
				informationModelManager.update(template.getUuid(), template);
			}
			else if(request.getObjectType() == AuthorisationObjectType.NEGOTIATION){
				Negotiation negotiation = negotiationManager.getNegotiation(request.getObjectID());
				negotiation.setPublishStatus(PublishStatus.DRAFT);
				negotiationManager.storeNegotiation(negotiation);
				//NegotiationParameter negotiationParameter = negotiationManager.getNegotiationParameter(request.getObjectID(), new QueryFilters());
				//negotiationParameter.publishStatus = PublishStatus.DRAFT;
				//negotiationManager.updateNegotiationDraft(negotiationParameter, request.getObjectID());
			}
			else if(request.getObjectType() == AuthorisationObjectType.NEGOTIATION_TEMPLATE){
				NegotiationParameter negotiation = negotiationTemplateManager.getNegotiationParameter(request.getObjectID());
				negotiation.publishStatus=PublishStatus.DRAFT;
				negotiationTemplateManager.updateNegotiationDraft(negotiation, request.getObjectID());
			}
			else if(request.getObjectType() == AuthorisationObjectType.NEGOTIATION_MESSAGE){
				
			}
			else if(request.getObjectType() == AuthorisationObjectType.CLAUSE){
				Clause clause = clauseManager.getClause(request.getObjectID());
				clause.setPublicationStatus(PublishStatus.DRAFT);
				clauseManager.updateClause(clause);
			}
			else{
				throw new ExParamException(ErrorKeys.PARAM_INVALID, "objectType");
			}
		}
		
		request = authorisationPersistentManager.update(request);
		
		return request;
	}

	@Override
	public void deleteAuthorisationRequest(String iRequestUUID) {
		//checking profile permission
		//Security.hasAccessPermission(Security.ProfilePermissions.AUTHORISATIONS);

		AuthorisationRequest request = this.getAuthorisationRequestByUUID(iRequestUUID);
		//checkTemplateOwnership(template);
		authorisationPersistentManager.delete(request);
	}

	@Override
	public AuthorisationRequest getAuthorisationRequestByUUID(
			String iRequestUUID) {
		return authorisationPersistentManager.readObjectByUUID(AuthorisationRequest.class, iRequestUUID);
	}

	@Override
	public List<AuthorisationRequest> listAuthorisationRequests(
			QueryFilters iFilters) {
		return authorisationPersistentManager.listObjects(new AuthorisationRequestQuery(), iFilters);
	}

	@Override
	public boolean hasPendingAuthorisation(String objectID) {
		QueryFilters filter = QueryFilters.create(AuthorisationFields.STATUS, AuthorisationStatus.PENDING);
		filter.put(AuthorisationFields.OBJECTID, objectID);
		
		return ExCollections.isNotEmpty(listAuthorisationRequests(filter));
	}

}
