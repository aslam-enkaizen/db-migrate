package com.exrade.runtime.notification.event;

import com.exrade.models.authorisation.AuthorisationRequest;
import com.exrade.models.notification.NotificationType;

public class AuthorisationNotificationEvent extends NotificationEvent<AuthorisationRequest> {

	
	public AuthorisationNotificationEvent(NotificationType source, AuthorisationRequest payload) {
		super(source, payload);
	}
	
	public AuthorisationRequest getAuthorisationRequest(){
		return getPayload();
	}
	
}
