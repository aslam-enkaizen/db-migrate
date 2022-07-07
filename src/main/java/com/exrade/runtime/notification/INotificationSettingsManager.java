package com.exrade.runtime.notification;

import com.exrade.models.notification.ChannelType;
import com.exrade.models.notification.Frequency;
import com.exrade.models.notification.NotificationSetting;
import com.exrade.models.notification.NotificationType;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface INotificationSettingsManager {

	NotificationSetting getNotificationSetting(String iNotificationSettingUUID);
	
	NotificationSetting getNotificationSetting(ChannelType iChannelType, NotificationType iNotificationType);
	
	NotificationSetting saveNotificationSetting(NotificationSetting iNotificationSetting);
	
	List<NotificationSetting> listNotificationSettings(QueryFilters iFilters);
	
	List<NotificationSetting> getNotificationSettings();
	
	List<NotificationSetting> getEnabledNotificationSettings();
	
	List<NotificationSetting> getNotificationSettingsForChannel(ChannelType iChannelType);
	
	List<NotificationSetting> getNotificationSettingsForType(NotificationType iNotificationType);
	
	boolean isEnabled(ChannelType iChannelType, NotificationType iNotificationType);
	
	boolean isExists(ChannelType iChannelType, NotificationType iNotificationType);
	
	List<Frequency> getAllowedFrequencies(ChannelType iChannelType, NotificationType iNotificationType);
}
