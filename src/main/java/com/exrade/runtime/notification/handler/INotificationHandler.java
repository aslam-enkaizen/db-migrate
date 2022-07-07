package com.exrade.runtime.notification.handler;

import com.exrade.runtime.notification.event.NotificationEvent;

public interface INotificationHandler {

	public <T> void handle(NotificationEvent<T> event);
	
}
