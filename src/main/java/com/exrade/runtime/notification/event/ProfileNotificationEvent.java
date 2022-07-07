package com.exrade.runtime.notification.event;

import com.exrade.models.invitations.MemberInvitation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Profile;

public class ProfileNotificationEvent extends NotificationEvent<Profile> {
	
	private MemberInvitation memberInvitation;
	
	public ProfileNotificationEvent(NotificationType source, Profile payload) {
		super(source, payload);
	}
	
	public ProfileNotificationEvent(NotificationType source, Profile payload, MemberInvitation invitation) {
		super(source, payload);
		this.memberInvitation = invitation;
	}
	
	public Profile getProfile(){
		return getPayload();
	}
	
	public MemberInvitation getMemberInvitation(){
		return memberInvitation;
	}
}
