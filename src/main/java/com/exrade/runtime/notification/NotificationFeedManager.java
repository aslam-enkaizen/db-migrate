package com.exrade.runtime.notification;

import com.exrade.models.activity.Activity;
import com.exrade.models.activity.Verb;
import com.exrade.models.notification.NotificationFeed;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.notification.persistence.NotificationFeedPersistenceManager;
import com.exrade.runtime.notification.persistence.NotificationFeedQuery;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.NotificationFeedFields;
import com.exrade.util.ExCollections;

import java.util.List;

public class NotificationFeedManager implements INotificationFeedManager {

	private NotificationFeedPersistenceManager persistenceManager = new NotificationFeedPersistenceManager();

	@Override
	public NotificationFeed readByUUID(String iNotificationFeedUUID) {
		return persistenceManager.readObjectByUUID(NotificationFeed.class, iNotificationFeedUUID);
	}

	@Override
	public void delete(String iNotificationFeedUUID) {
		persistenceManager.delete(readByUUID(iNotificationFeedUUID));
	}

	@Override
	public List<NotificationFeed> listNotificationFeeds(QueryFilters filters) {

		filters.put(QueryParameters.SORT, OrientSqlBuilder.DESC_SORT+RestParameters.CREATION_DATE);
		return persistenceManager.listObjects(new NotificationFeedQuery(), filters);
	}

	@Override
	public NotificationFeed update(NotificationFeed notificationFeed) {
		persistenceManager.update(notificationFeed);
		return readByUUID(notificationFeed.getUuid());
	}

	@Override
	public NotificationFeed create(NotificationFeed notificationFeed) {
		persistenceManager.create(notificationFeed);
		return readByUUID(notificationFeed.getUuid());
	}

	@Override
	public void markAsSeen(String iNotificationFeedUUID) {
		NotificationFeed feed = readByUUID(iNotificationFeedUUID);
		feed.setSeen(true);
		update(feed);
	}

	@Override
	public void markAsRead(String iNotificationFeedUUID) {
		NotificationFeed feed = readByUUID(iNotificationFeedUUID);
		feed.setRead(true);
		update(feed);
	}

	@Override
	public List<NotificationFeed> listUserNotificationFeeds(String membershipUUID, QueryFilters filters) {
		filters.put(NotificationFeedFields.MEMBERSHIP, membershipUUID);
		return listNotificationFeeds(filters);
	}

	@Override
	public void create(Activity activity, List<Negotiator> toList) {
		if(ExCollections.isNotEmpty(toList)){
			for(Negotiator to : toList){
				NotificationFeed feed = NotificationFeed.create(activity, (Membership)to);
				if(feed.getActivity().getVerb() == Verb.LOGIN)
					feed.setVisible(false);
				create(feed);
			}
		}
	}

	@Override
	public void markAllAsSeen(String membershipUUID) {
		persistenceManager.markFieldTrue(membershipUUID, NotificationFeedFields.SEEN);
	}

	@Override
	public void markAllAsRead(String membershipUUID) {
		persistenceManager.markFieldTrue(membershipUUID, NotificationFeedFields.READ);
	}

}
