package com.exrade.runtime.notification.handler;

import com.exrade.Messages;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.event.ProfileNotificationEvent;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;

import java.util.HashMap;
import java.util.Map;

public class ProfileNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/profile/";

	@Override
	public <T> void handle(NotificationEvent<T> event) {
		ProfileNotificationEvent notificationEvent = (ProfileNotificationEvent) event;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Membership to = null;
		String toEmail = null;
		String language = null;

		if(NotificationType.PROFILE_MEMBERSHIP_INVITATION_RECEIVED == notificationEvent.getNotificationType()){
			dataMap.put("actor_name", ContextHelper.getMembership().getUser().getFullName());
			dataMap.put("target_name", notificationEvent.getProfile().getName());
			dataMap.put("url", BASE_URL +  notificationEvent.getMemberInvitation().getInvitedProfileUUID() + "/invitations/" + notificationEvent.getMemberInvitation().getUuid());
			if(notificationEvent.getMemberInvitation().getInvitedUser() != null){
				dataMap.put("receiver_name", notificationEvent.getMemberInvitation().getInvitedUser().getFullName());
				toEmail = notificationEvent.getMemberInvitation().getInvitedUser().getEmail();
			}
			else {
				toEmail = notificationEvent.getMemberInvitation().getInvitedEmail();
				language = notificationEvent.getMemberInvitation().getInvitedBy().getLanguage();
			}
		}
		else if(NotificationType.PROFILE_MEMBERSHIP_INVITATION_UPDATED == notificationEvent.getNotificationType()){
			dataMap.put("actor_name", ContextHelper.getMembership().getUser().getFullName());
			dataMap.put("decision", Messages.get(notificationEvent.getMemberInvitation().getInvitationStatus().name()));
			dataMap.put("target_name", notificationEvent.getProfile().getName());
			dataMap.put("url", BASE_URL + "members");

			IMembershipManager membershipManager = new MembershipManager();
			to = membershipManager.getOwnerMembership(notificationEvent.getProfile().getUuid());
			dataMap.put("receiver_name", to.getFullName());
		}
		else if(NotificationType.PROFILE_TRIAL_FINISHING_REMINDER == notificationEvent.getNotificationType()){
			IMembershipManager membershipManager = new MembershipManager();
			to = membershipManager.getOwnerMembership(notificationEvent.getProfile().getUuid());

			if(to.getProfile().getPlanSubscription() == null)
				return;

			dataMap.put("receiver_name", to.getFullName());
			dataMap.put("plan_name", to.getPlan().getTitle());
			dataMap.put("expiration_date", to.getProfile().getPlanSubscription().getTrialEndDate());
			dataMap.put("url", BASE_URL + "subscribe/" + to.getPlan().getUuid());
		}

		if (language == null) {
			sendWebHookNotification(notificationEvent, to);
			sendNotification(to, toEmail, event.getNotificationType(), dataMap);
		} else {
			sendWebHookNotification(notificationEvent, to);
			sendNotification(to, toEmail, event.getNotificationType(), dataMap, language);
		}
	}
}
