package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.notification.NotificationSetting;

import java.util.List;
import java.util.Map;

public interface NotificationSettingsAPI {

	NotificationSetting saveNotificationSetting(ExRequestEnvelope request, NotificationSetting iNotificationSetting);
	
	List<NotificationSetting> listNotificationSettings(ExRequestEnvelope request, Map<String, String> iFilters);
	
}
