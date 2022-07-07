package com.exrade.runtime.notification.event;

import com.exrade.models.notification.NotificationType;

public class NotificationEvent<P> {
	  protected P  payload;

	  protected NotificationType notificationType;

	  public NotificationEvent(NotificationType notificationType, P payload) {
	    this.payload = payload;
	    this.notificationType = notificationType;
	  }


	  public P getPayload() {
	    return payload;
	  }

	  public NotificationType getNotificationType() {
	    return notificationType;
	  }
}
