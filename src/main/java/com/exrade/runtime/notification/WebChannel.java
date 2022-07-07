package com.exrade.runtime.notification;

import com.exrade.models.activity.Activity;
import com.exrade.models.notification.ChannelType;
import com.exrade.models.notification.NotificationFeed;
import com.exrade.models.userprofile.Membership;
import com.exrade.runtime.notification.event.NotificationEvent;

import java.util.List;

public class WebChannel extends AbstractChannel {

	private INotificationFeedManager feedManager = new NotificationFeedManager();
	
	@Override
	public ChannelType getChannelType() {
		return ChannelType.WEB;
	}

	@Override
	public <T> void dispatch(NotificationEvent<T> notificationEvent, Activity activity, List<Membership> toList) {
		for(Membership to : toList){
			NotificationFeed feed = NotificationFeed.create(activity, to);
			feedManager.create(feed);
		}
	}

}
