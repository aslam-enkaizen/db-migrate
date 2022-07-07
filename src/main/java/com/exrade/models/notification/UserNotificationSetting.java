package com.exrade.models.notification;

import com.exrade.platform.persistence.BaseEntity;
import org.springframework.util.Assert;

public class UserNotificationSetting extends BaseEntity {


	private NotificationSetting notificationSetting;
	
	private Frequency frequency;
	
	public static UserNotificationSetting create(NotificationSetting iNotificationSetting){
		return create(iNotificationSetting, Frequency.NONE);
	}
	
	public static UserNotificationSetting create(NotificationSetting iNotificationSetting, Frequency iFrequency){
		Assert.notNull(iNotificationSetting, "iNotificationSetting");
		
		UserNotificationSetting userNotificationSetting = new UserNotificationSetting();
		userNotificationSetting.setNotificationSetting(iNotificationSetting);
		userNotificationSetting.setFrequency(iFrequency);
		
		return userNotificationSetting;
	}
	
	public Frequency getFrequency() {
		return frequency;
	}

	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	public NotificationSetting getNotificationSetting() {
		return notificationSetting;
	}

	public void setNotificationSetting(NotificationSetting notificationSetting) {
		this.notificationSetting = notificationSetting;
	}
	
}
