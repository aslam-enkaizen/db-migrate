package com.exrade.runtime.notification.handler;

import com.exrade.core.ExLogger;
import com.exrade.models.authorisation.AuthorisationObjectType;
import com.exrade.models.authorisation.AuthorisationStatus;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.negotiation.NegotiationParameter;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.informationmodel.IInformationModelManager;
import com.exrade.runtime.informationmodel.InformationModelManager;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.notification.event.AuthorisationNotificationEvent;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.rest.RestParameters.AuthorisationFields;
import com.exrade.util.ContextHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorisationNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

	//private IUserSettingsManager userSettingManager = new UserSettingsManager();
	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/authorisations/";
	
	@Override
	public <T> void handle(NotificationEvent<T> event) {
		AuthorisationNotificationEvent notificationEvent = (AuthorisationNotificationEvent) event;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<Negotiator> receivers = new ArrayList<>();
		String language = null;
		
		if(NotificationType.AUTHORISATION_REQUESTED == notificationEvent.getNotificationType()){
			String targetName = "";
			
			try {
				if(notificationEvent.getAuthorisationRequest().getObjectType() == AuthorisationObjectType.INFORMATION_MODEL_TEMPLATE){
					IInformationModelManager informationModelManager = new InformationModelManager();
					InformationModelTemplate template = informationModelManager.readByUUID(notificationEvent.getAuthorisationRequest().getObjectID());
					targetName = template.getTitle();
					language = template.getLanguage();
				}
				else if(notificationEvent.getAuthorisationRequest().getObjectType() == AuthorisationObjectType.NEGOTIATION) {
					INegotiationManager negotiationManager = new NegotiationManager();
					NegotiationParameter negotiationParameter = negotiationManager.getNegotiationParameter(notificationEvent.getAuthorisationRequest().getObjectID(), new QueryFilters());
					targetName = negotiationParameter.title;
					language = negotiationParameter.language;
				}
				else if(notificationEvent.getAuthorisationRequest().getObjectType() == AuthorisationObjectType.NEGOTIATION_MESSAGE) {
					INegotiationManager negotiationManager = new NegotiationManager();
					Negotiation negotiation = negotiationManager.getNegotiation(notificationEvent.getAuthorisationRequest().getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID).toString());
					targetName = negotiation.getTitle();
					language = negotiation.getLanguage();
					dataMap.put("action_name", notificationEvent.getAuthorisationRequest().getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_ACTION).toString());
				}
				else if(notificationEvent.getAuthorisationRequest().getObjectType() == AuthorisationObjectType.NEGOTIATION_MESSAGE) {
					return;
				}
			}
			catch(Exception ex) {
				ExLogger.get().warn("Failed to genarate authorisation request target", ex);
				targetName = notificationEvent.getAuthorisationRequest().getObjectType().toString();
			}

			dataMap.put("actor_name", ContextHelper.getMembership().getUser().getFullName());
			dataMap.put("target_type", notificationEvent.getAuthorisationRequest().getObjectType());
			dataMap.put("target_name", targetName);
			dataMap.put("url", BASE_URL + notificationEvent.getAuthorisationRequest().getUuid());
			
			
			receivers.addAll(notificationEvent.getAuthorisationRequest().getReceivers());
		}
		else if(NotificationType.AUTHORISATION_PENDING_REMINDER == notificationEvent.getNotificationType()){
			if(notificationEvent.getAuthorisationRequest().getStatus() == AuthorisationStatus.PENDING) {
				String targetName = "";
				
				try {
					if(notificationEvent.getAuthorisationRequest().getObjectType() == AuthorisationObjectType.INFORMATION_MODEL_TEMPLATE){
						IInformationModelManager informationModelManager = new InformationModelManager();
						InformationModelTemplate template = informationModelManager.readByUUID(notificationEvent.getAuthorisationRequest().getObjectID());
						targetName = template.getTitle();
						language = template.getLanguage();
					}
					else if(notificationEvent.getAuthorisationRequest().getObjectType() == AuthorisationObjectType.NEGOTIATION) {
						INegotiationManager negotiationManager = new NegotiationManager();
						NegotiationParameter negotiationParameter = negotiationManager.getNegotiationParameter(notificationEvent.getAuthorisationRequest().getObjectID(), new QueryFilters());
						targetName = negotiationParameter.title;
						language = negotiationParameter.language;
					}
					else if(notificationEvent.getAuthorisationRequest().getObjectType() == AuthorisationObjectType.NEGOTIATION_MESSAGE) {
						INegotiationManager negotiationManager = new NegotiationManager();
						Negotiation negotiation = negotiationManager.getNegotiation(notificationEvent.getAuthorisationRequest().getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID).toString());
						targetName = negotiation.getTitle();
						language = negotiation.getLanguage();
						dataMap.put("action_name", notificationEvent.getAuthorisationRequest().getExtraContext().get(AuthorisationFields.EXTRA_CONTEXT_ACTION).toString());
					}
					else {
						return;
					}
				}
				catch(Exception ex) {
					ExLogger.get().warn("Failed to genarate authorisation request target", ex);
					targetName = notificationEvent.getAuthorisationRequest().getObjectType().toString();
				}
				
				dataMap.put("target_type", notificationEvent.getAuthorisationRequest().getObjectType());
				dataMap.put("target_name", targetName);
				dataMap.put("url", BASE_URL + notificationEvent.getAuthorisationRequest().getUuid());
				
				receivers.addAll(notificationEvent.getAuthorisationRequest().getAuthorisersWithPendingRequest());
			}
		}		
		
		sendWebHookNotification(notificationEvent, receivers);
		sendNotification(ContextHelper.getMembership(), receivers, notificationEvent.getNotificationType(), dataMap, language);
	}

}
