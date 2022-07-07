package com.exrade.runtime.notification.handler;

import com.exrade.models.notification.ChannelType;
import com.exrade.models.notification.Frequency;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.IProfile;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.webhook.WebHook;
import com.exrade.models.webhook.WebHookEvent;
import com.exrade.platform.persistence.IPersistenceUUID;
import com.exrade.runtime.mail.EmailSender;
import com.exrade.runtime.notification.IUserSettingsManager;
import com.exrade.runtime.notification.UserSettingsManager;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.runtime.webhook.IWebHookManager;
import com.exrade.runtime.webhook.WebHookManager;
import com.exrade.util.ExCollections;
import com.exrade.util.RESTUtil;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseNotificationHandler {
	private IUserSettingsManager userSettingManager = new UserSettingsManager();
	private IMembershipManager membershipManager = new MembershipManager();
	private IWebHookManager webHookManager = new WebHookManager();
	private String notificationTemplateDir = "";

	public String getNotificationTemplateDir() {
		return notificationTemplateDir;
	}

	public void setNotificationTemplateDir(String notificationTemplateDir) {
		this.notificationTemplateDir = notificationTemplateDir;
	}

	public void sendNotification(Negotiator actor, List<? extends Negotiator> receivers,
			NotificationType notificationType, Map<String, Object> dataMap) {
		sendNotification(actor, receivers, notificationType, dataMap, null);
	}

	public void sendNotification(Negotiator actor, List<? extends Negotiator> receivers,
			NotificationType notificationType, Map<String, Object> dataMap, String language) {
		if (ExCollections.isNotEmpty(receivers)) {
			if (actor != null) {
				for (int i = 0; i < receivers.size(); i++) {
					if (receivers.get(i).getIdentifier().equals(actor.getIdentifier())) {
						receivers.remove(i);
						break;
					}
				}
			}

			for (Negotiator receiver : getMembersWithActiveNotification(receivers, notificationType)) {
				dataMap.put("receiver_name", receiver.getUser().getFullName());
				sendNotification(notificationType,
						!Strings.isNullOrEmpty(language) ? language : receiver.getUser().getLanguage(), dataMap,
						new String[] { receiver.getUser().getEmail() });
			}
		}
	}

	public void sendNotification(Negotiator to, String toEmail, NotificationType notificationType,
			Map<String, Object> dataMap) {
		sendNotification(to, toEmail, notificationType, dataMap, null);
	}

	public void sendNotification(Negotiator to, String toEmail, NotificationType notificationType,
			Map<String, Object> dataMap, String language) {
		if (to != null && isNotificationActive(to.getIdentifier(), notificationType) && to.isProfileActive()
				&& to.isActive()) {
			dataMap.put("receiver_name", to.getUser().getFullName());
			sendNotification(notificationType, !Strings.isNullOrEmpty(language) ? language : to.getUser().getLanguage(),
					dataMap, new String[] { to.getUser().getEmail() });
		} else if (toEmail != null) {
			dataMap.put("receiver_name", "");
			sendNotification(notificationType, language, dataMap, new String[] { toEmail });
		}
	}

	public void sendNotification(IProfile toProfile, NotificationType notificationType, Map<String, Object> dataMap) {
		List<? extends Negotiator> receivers = membershipManager.getProfileMembers(toProfile.getUuid());
		sendNotification(null, receivers, notificationType, dataMap);
	}

	public void sendNotification(NotificationType notificationType, String language, Map<String, Object> dataMap,
			String[] iRecipients) {
		EmailSender emailSender = new EmailSender();
		emailSender.send(getNotificationTemplateDir() + notificationType.toString(), language, dataMap, iRecipients);
	}

	private List<Negotiator> getMembersWithActiveNotification(List<? extends Negotiator> iMembers,
			NotificationType notificationType) {
		List<Negotiator> activeMembers = new ArrayList<>();
		for (Negotiator member : iMembers) {
			if (isNotificationActive(member.getIdentifier(), notificationType) && member.isProfileActive()
					&& member.isActive())
				activeMembers.add(member);
		}
		return activeMembers;
	}

	private boolean isNotificationActive(String membershipIdentitifer, NotificationType notificationType) {
		try {
			return userSettingManager.isNotificationActive(membershipIdentitifer, ChannelType.EMAIL, notificationType,
					Frequency.IMMEDIATELY);
		} catch (Exception ex) {
			return true;
		}
	}

	public <P> void sendWebHookNotification(NotificationEvent<P> notificationEvent, List<Negotiator> receivers) {
		if (notificationEvent != null && !receivers.isEmpty() && receivers.get(0) != null && receivers.get(0).getProfile() != null) {
			WebHook webHook = webHookManager.findByProfileUUID(receivers.get(0).getProfile().getUuid());
			if (webHook != null && webHook.getEnabled()
					&& webHook.getEvents().contains(notificationEvent.getNotificationType().name())) {
				RESTUtil.doRestPOST(webHook.getUrl(), null,
						ControllerUtil.toJsonResponseNoPersistence(createWebHookEvent(notificationEvent)));
			}
		}
	}

	public <P> void sendWebHookNotification(NotificationEvent<P> notificationEvent, Membership membership) {
		if (notificationEvent != null && membership != null) {
			Profile profile = membership.getProfile();
			if (profile != null) {
				WebHook webHook = webHookManager.findByProfileUUID(profile.getUuid());
				if (webHook != null && webHook.getEnabled()
						&& webHook.getEvents().contains(notificationEvent.getNotificationType().name())) {
					RESTUtil.doRestPOST(webHook.getUrl(), null,
							ControllerUtil.toJsonResponseNoPersistence(createWebHookEvent(notificationEvent)));
				}
			}
		}
	}
	
	private <P> WebHookEvent createWebHookEvent(NotificationEvent<P> notificationEvent) {
		WebHookEvent webHookEvent = new WebHookEvent();
		webHookEvent.setEventType(notificationEvent.getNotificationType());
		webHookEvent.setObjectId(((IPersistenceUUID)notificationEvent.getPayload()).getUuid());
		return webHookEvent;
	}
}
