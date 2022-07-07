package com.exrade.runtime.notification;

import com.exrade.models.activity.Activity;
import com.exrade.models.notification.ChannelType;
import com.exrade.models.userprofile.Membership;
import com.exrade.runtime.notification.event.NotificationEvent;

import java.util.List;

public abstract class AbstractChannel {

	public abstract ChannelType getChannelType();
	
	public abstract <T> void dispatch(NotificationEvent<T> notificationEvent, Activity activity, List<Membership> toList);
	
}
