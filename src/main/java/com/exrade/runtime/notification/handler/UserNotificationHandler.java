package com.exrade.runtime.notification.handler;

import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.User;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.event.UserNotificationEvent;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

public class UserNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url");

	@Override
	public <T> void handle(NotificationEvent<T> event) {
		UserNotificationEvent notificationEvent = (UserNotificationEvent) event;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		String toEmail = notificationEvent.getUser().getEmail();
		String language = notificationEvent.getUser().getLanguage();

		if(NotificationType.USER_PASSWORD_CHANGED == notificationEvent.getNotificationType()){
			dataMap.put("user_name", notificationEvent.getUser().getFullName());
		}
		else if(NotificationType.USER_PASSWORD_RESET_REQUESTED == notificationEvent.getNotificationType()){
			if(!notificationEvent.getUser().isActive())
				return;
			dataMap.put("user_name", notificationEvent.getUser().getFullName());
			dataMap.put("url", String.format("%s/password-reset/%s/", BASE_URL, notificationEvent.getTokenAction().getToken()));
		}
		else if(NotificationType.USER_SIGNUP_CONFIRMATION_REQUIRED == notificationEvent.getNotificationType()){
			dataMap.put("user_name", notificationEvent.getUser().getFullName());
			if(notificationEvent.getAuthUser() != null && !Strings.isNullOrEmpty(notificationEvent.getAuthUser().getRedirectUrl()))
				dataMap.put("url", String.format("%s/activate/%s/?next=%s", BASE_URL, notificationEvent.getTokenAction().getToken(), notificationEvent.getAuthUser().getRedirectUrl()));
			else
				dataMap.put("url", String.format("%s/activate/%s/", BASE_URL, notificationEvent.getTokenAction().getToken()));
		}
		else if(NotificationType.USER_WELCOME_NOTIFICATION == notificationEvent.getNotificationType()){
			dataMap.put("user_name", notificationEvent.getUser().getFullName());
			dataMap.put("url", String.format("%s/negotiation/", BASE_URL));
		}

		if (notificationEvent != null) {
			User user = notificationEvent.getUser();
			if (user != null) {
				Membership membership = user.getCurrentMembership();
				sendWebHookNotification(notificationEvent, membership);
			}
		}
		sendNotification(null, toEmail, event.getNotificationType(), dataMap, language);
	}

}
