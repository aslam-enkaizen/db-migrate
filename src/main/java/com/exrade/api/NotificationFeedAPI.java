package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.notification.NotificationFeed;

import java.util.List;
import java.util.Map;

public interface NotificationFeedAPI {

	NotificationFeed getNotificationFeed(ExRequestEnvelope request, String iNotificationFeedUUID);

	void deleteNotificationFeed(ExRequestEnvelope request, String iNotificationFeedUUID);

	List<NotificationFeed> listNotificationFeeds(ExRequestEnvelope request, Map<String, String> iFilters);

	void markNotificationFeed(ExRequestEnvelope request, String iNotificationFeedUUID, boolean seen, boolean read);

	void markAllNotificationFeed(ExRequestEnvelope request, boolean seen, boolean read);
}
