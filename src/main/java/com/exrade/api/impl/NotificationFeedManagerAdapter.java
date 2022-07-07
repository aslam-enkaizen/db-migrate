package com.exrade.api.impl;

import com.exrade.api.NotificationFeedAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.notification.NotificationFeed;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.notification.INotificationFeedManager;
import com.exrade.runtime.notification.NotificationFeedManager;
import com.exrade.runtime.rest.RestParameters.NotificationFeedFields;
import com.exrade.util.ContextHelper;

import java.util.List;
import java.util.Map;

public class NotificationFeedManagerAdapter implements NotificationFeedAPI {

	private INotificationFeedManager manager = new NotificationFeedManager();

	@Override
	public NotificationFeed getNotificationFeed(ExRequestEnvelope request, String iNotificationFeedUUID) {
		ContextHelper.initContext(request);
		return manager.readByUUID(iNotificationFeedUUID);
	}

	@Override
	public void deleteNotificationFeed(ExRequestEnvelope request, String iNotificationFeedUUID) {
		ContextHelper.initContext(request);
		manager.delete(iNotificationFeedUUID);
	}

	@Override
	public List<NotificationFeed> listNotificationFeeds(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putIfNotEmpty("type",iFilters.get("type"));
		filters.putIfNotEmpty("objectID",iFilters.get("objectID"));

		if(iFilters.containsKey(NotificationFeedFields.READ))
			filters.putIfNotNull(NotificationFeedFields.READ, Boolean.parseBoolean(iFilters.get(NotificationFeedFields.READ)));

		if(iFilters.containsKey(NotificationFeedFields.SEEN))
			filters.putIfNotNull(NotificationFeedFields.SEEN, Boolean.parseBoolean(iFilters.get(NotificationFeedFields.SEEN)));

		if(iFilters.containsKey(NotificationFeedFields.VISIBLE))
			filters.putIfNotNull(NotificationFeedFields.VISIBLE, Boolean.parseBoolean(iFilters.get(NotificationFeedFields.VISIBLE)));

		return manager.listUserNotificationFeeds(ContextHelper.getMembershipUUID(), filters);
	}

	@Override
	public void markNotificationFeed(ExRequestEnvelope request, String iNotificationFeedUUID, boolean seen, boolean read) {
		ContextHelper.initContext(request);
		if(seen)
			manager.markAsSeen(iNotificationFeedUUID);
		if(read)
			manager.markAsRead(iNotificationFeedUUID);
	}

	@Override
	public void markAllNotificationFeed(ExRequestEnvelope request, boolean seen, boolean read) {
		ContextHelper.initContext(request);
		if(seen)
			manager.markAllAsSeen(ContextHelper.getMembershipUUID());
		if(read)
			manager.markAllAsRead(ContextHelper.getMembershipUUID());
	}

}
