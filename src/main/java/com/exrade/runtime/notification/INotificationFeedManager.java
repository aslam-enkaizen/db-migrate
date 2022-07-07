package com.exrade.runtime.notification;

import com.exrade.models.activity.Activity;
import com.exrade.models.notification.NotificationFeed;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface INotificationFeedManager {

	NotificationFeed readByUUID(String iNotificationFeedUUID);

	void delete(String iNotificationFeedUUID);

	List<NotificationFeed> listNotificationFeeds(QueryFilters filters);

	List<NotificationFeed> listUserNotificationFeeds(String membershipUUID, QueryFilters filters);

	NotificationFeed update(NotificationFeed notificationFeed);

	NotificationFeed create(NotificationFeed notificationFeed);

	void create(Activity activity, List<Negotiator> toList);

	void markAsSeen(String iNotificationFeedUUID);

	void markAsRead(String iNotificationFeedUUID);

	void markAllAsSeen(String membershipUUID);

	void markAllAsRead(String membershipUUID);
}
