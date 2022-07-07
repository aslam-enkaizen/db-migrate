package com.exrade.models.calendar;

import com.exrade.models.notification.NotificationType;
import com.exrade.platform.persistence.BaseEntityUUID;

public class Reminder extends BaseEntityUUID {
	private NotificationType notificationType;
	private int minutes;

	public Reminder() {}

	public Reminder(NotificationType notificationType, int minutes) {
		setNotificationType(notificationType);
		setMinutes(minutes);
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
}
