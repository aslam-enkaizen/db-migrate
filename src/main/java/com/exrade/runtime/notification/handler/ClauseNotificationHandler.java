package com.exrade.runtime.notification.handler;

import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.ClauseNotificationEvent;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.util.ContextHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author: Md. Aslam Hossain
 *
 */
public class ClauseNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/" + RestParameters.Resources.CLAUSE + "/";

	@Override
	public <T> void handle(NotificationEvent<T> event) {
		ClauseNotificationEvent clauseNotificationEvent = (ClauseNotificationEvent) event;
		Map<String, Object> dataMap = new HashMap<>();
		List<Negotiator> receivers = new ArrayList<>();
		Negotiator actor = ContextHelper.getMembership();
		
		if (actor != null)
			dataMap.put("actor_name", ((Membership) actor).getFullName());
		
		if (NotificationType.CLAUSE_STATUS_UPDATED == clauseNotificationEvent.getNotificationType()) {
			dataMap.put("object_name", clauseNotificationEvent.getPayload().getTitle());
			dataMap.put("url", BASE_URL + clauseNotificationEvent.getPayload().getUuid());
			receivers.add(clauseNotificationEvent.getPayload().getCreator());
		}
		
		sendWebHookNotification(clauseNotificationEvent, receivers);
		sendNotification(actor, receivers, clauseNotificationEvent.getNotificationType(), dataMap);
		
	}
}
