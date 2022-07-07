package com.exrade.runtime.notification;

import com.exrade.models.notification.ChannelType;
import com.exrade.models.notification.Frequency;
import com.exrade.models.notification.NotificationSetting;
import com.exrade.models.notification.NotificationType;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.notification.persistence.NotificationSettingQuery;
import com.exrade.runtime.rest.RestParameters.NotificationSettingFields;

import java.util.List;

public class NotificationSettingsManager implements INotificationSettingsManager {

	private PersistentManager persistenceManager = new PersistentManager();
	
	@Override
	public NotificationSetting getNotificationSetting(String iNotificationSettingUUID) {
		return persistenceManager.readObjectByUUID(NotificationSetting.class, iNotificationSettingUUID);
	}

	@Override
	public NotificationSetting saveNotificationSetting(NotificationSetting iNotificationSetting) {
		persistenceManager.update(iNotificationSetting);
		return this.getNotificationSetting(iNotificationSetting.getUuid());
	}

	@Override
	public List<NotificationSetting> listNotificationSettings(QueryFilters iFilters) {
		if(iFilters == null)
			iFilters = new QueryFilters();
		if(iFilters.get(QueryParameters.PER_PAGE) == null)
			iFilters.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
		return persistenceManager.listObjects(new NotificationSettingQuery(), iFilters);
	}
	
	@Override
	public List<NotificationSetting> getNotificationSettings() {
		QueryFilters filters = new QueryFilters();
		return listNotificationSettings(filters);
	}

	@Override
	public List<NotificationSetting> getEnabledNotificationSettings() {
		QueryFilters filters = QueryFilters.create(NotificationSettingFields.ACTIVE, true);
		
		return listNotificationSettings(filters);
	}

	@Override
	public List<NotificationSetting> getNotificationSettingsForChannel(ChannelType iChannelType) {
		QueryFilters filters = QueryFilters.create(NotificationSettingFields.CHANNEL_TYPE, iChannelType);
		
		return listNotificationSettings(filters);
	}

	@Override
	public List<NotificationSetting> getNotificationSettingsForType(NotificationType iNotificationType) {
		QueryFilters filters = QueryFilters.create(NotificationSettingFields.NOTIFICATION_TYPE, iNotificationType);
		
		return listNotificationSettings(filters);
	}

	@Override
	public NotificationSetting getNotificationSetting(ChannelType iChannelType, NotificationType iNotificationType) {
		QueryFilters filters = QueryFilters.create(NotificationSettingFields.NOTIFICATION_TYPE, iNotificationType);
		filters.put(NotificationSettingFields.CHANNEL_TYPE, iChannelType);
		
		return persistenceManager.readObject(new NotificationSettingQuery(), filters);
	}

	@Override
	public boolean isEnabled(ChannelType iChannelType, NotificationType iNotificationType) {
		NotificationSetting notificationSetting = getNotificationSetting(iChannelType, iNotificationType);
		
		if(notificationSetting != null && notificationSetting.isActive())
			return true;
		
		return false;
	}

	@Override
	public List<Frequency> getAllowedFrequencies(ChannelType iChannelType, NotificationType iNotificationType) {
		NotificationSetting notificationSetting = getNotificationSetting(iChannelType, iNotificationType);
		return notificationSetting.getAllowedFrequencies();
	}

	@Override
	public boolean isExists(ChannelType iChannelType, NotificationType iNotificationType) {
		NotificationSetting notificationSetting = getNotificationSetting(iChannelType, iNotificationType);
		
		if(notificationSetting != null)
			return true;
		
		return false;
	}
}
