package com.exrade.models.notification;

import com.exrade.models.activity.Activity;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

public class NotificationFeed extends BaseEntityUUIDTimeStampable {

	private Activity activity;
	private Membership membership;
	private boolean seen;
	private boolean read;
	private boolean visible = true;
	
	public static NotificationFeed create(Activity activity, Membership membership){
		NotificationFeed feed = new NotificationFeed();
		feed.setActivity(activity);
		feed.setMembership(membership);
		feed.setRead(false);
		feed.setSeen(false);
		return feed;
	}
	
	public Activity getActivity() {
		return activity;
	}
	
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	public Membership getMembership() {
		return membership;
	}
	
	public void setMembership(Membership membership) {
		this.membership = membership;
	}
	
	public boolean isSeen() {
		return seen;
	}
	
	public void setSeen(boolean seen) {
		this.seen = seen;
	}
	
	public boolean isRead() {
		return read;
	}
	
	public void setRead(boolean read) {
		this.read = read;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
}
