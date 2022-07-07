package com.exrade.runtime.notification.event;

import com.exrade.models.informationmodel.Clause;
import com.exrade.models.notification.NotificationType;

/**
 *
 * @author: Md. Aslam Hossain
 *
 */
public class ClauseNotificationEvent extends NotificationEvent<Clause> {

	public ClauseNotificationEvent(NotificationType notificationType, Clause payload) {
		super(notificationType, payload);
	}

	public Clause getClause() {
		return getPayload();
	}
}
