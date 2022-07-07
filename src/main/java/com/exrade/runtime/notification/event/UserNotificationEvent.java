package com.exrade.runtime.notification.event;

import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.TokenAction;
import com.exrade.models.userprofile.User;
import com.exrade.runtime.userprofile.providers.password.ExUsernamePasswordAuthUser;

public class UserNotificationEvent extends NotificationEvent<User> {
	private TokenAction tokenAction = null;
	private ExUsernamePasswordAuthUser authUser = null;

	public UserNotificationEvent(NotificationType source, User payload) {
		super(source, payload);
	}

	public UserNotificationEvent(NotificationType source, User payload, TokenAction tokenAction) {
		super(source, payload);
		this.tokenAction = tokenAction;
	}

	public UserNotificationEvent(NotificationType source, User payload, TokenAction tokenAction, ExUsernamePasswordAuthUser authUser) {
		super(source, payload);
		this.tokenAction = tokenAction;
		this.authUser = authUser;
	}

	public User getUser(){
		return getPayload();
	}

	public TokenAction getTokenAction() {
		return this.tokenAction;
	}

	public ExUsernamePasswordAuthUser getAuthUser() {
		return authUser;
	}
}
