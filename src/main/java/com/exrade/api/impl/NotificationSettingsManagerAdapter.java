package com.exrade.api.impl;

import com.exrade.api.NotificationSettingsAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.notification.NotificationSetting;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.notification.INotificationSettingsManager;
import com.exrade.runtime.notification.NotificationSettingsManager;
import com.exrade.util.ContextHelper;

import java.util.List;
import java.util.Map;

public class NotificationSettingsManagerAdapter implements NotificationSettingsAPI {

	private INotificationSettingsManager manager = new NotificationSettingsManager();
	
	@Override
	public NotificationSetting saveNotificationSetting(ExRequestEnvelope request,
			NotificationSetting iNotificationSetting) {
		ContextHelper.initContext(request);
		return manager.saveNotificationSetting(iNotificationSetting);
	}

	@Override
	public List<NotificationSetting> listNotificationSettings(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		return manager.listNotificationSettings(QueryFilters.create(iFilters));
	}

}
