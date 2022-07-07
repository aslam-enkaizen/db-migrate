package com.exrade.runtime.notification.handler;

import com.exrade.Messages;
import com.exrade.models.messaging.AdmissionRequest;
import com.exrade.models.messaging.Information;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.team.TeamObjectType;
import com.exrade.models.userprofile.Membership;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.NegotiationNotificationEvent;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

public class NegotiationNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/negotiation/";
	private static final String TEAM_COLLABORATION_BASE_URL = ExConfiguration.getStringProperty("site.url") + "/collaboration/";
	@Override
	public <T> void handle(NotificationEvent<T> event) {
		NegotiationNotificationEvent notificationEvent = (NegotiationNotificationEvent) event;

		handleNegotiationEvent(notificationEvent);
	}

	public void handleNegotiationEvent(NegotiationNotificationEvent notificationEvent) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Membership to = null;
		String toEmail = null;
//		Lang lang = Lang.get(notificationEvent.getNegotiation().getLanguage()).get();
		//todo commented
		String lang = notificationEvent.getNegotiation().getLanguage();

		if (NotificationType.NEGOTIATION_USER_JOINED == notificationEvent.getNotificationType()) {
			dataMap.put("actor_name", notificationEvent.getNegotiationMessage().getSender().getUser().getFullName());
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid());
			to = (Membership) notificationEvent.getNegotiation().getOwner();
		} else if (NotificationType.NEGOTIATION_INFO_MESSAGE_RECEIVED == notificationEvent.getNotificationType()) {
			dataMap.put("actor_name", notificationEvent.getNegotiationMessage().getSender().getUser().getFullName());
			dataMap.put("object_name", ((Information) notificationEvent.getNegotiationMessage()).getContent());
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid() + "/messages/");
			to = (Membership) notificationEvent.getNegotiationMessage().getReceiver();
		} else if (NotificationType.NEGOTIATION_INVITATION_RECEIVED == notificationEvent.getNotificationType()) {
			dataMap.put("actor_name", notificationEvent.getNegotiation().getOwner().getUser().getFullName());
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());

			if ("Sales Quote".equals(notificationEvent.getNegotiation().getInformationModelDocument().getTitle())
					|| "Preventivo".equals(notificationEvent.getNegotiation().getInformationModelDocument().getTitle()))
				dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid() + "/quote/"
						+ notificationEvent.getNegotiationInvitation().getUuid());
			else
				dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid() + "/description/"
						+ notificationEvent.getNegotiationInvitation().getUuid());

			if (notificationEvent.getNegotiationInvitation().getInvitedMembership() != null)
				to = (Membership) notificationEvent.getNegotiationInvitation().getInvitedMembership();
			else
				toEmail = notificationEvent.getNegotiationInvitation().getInvitedEmail();

			String businessLogo = ((Membership) notificationEvent.getNegotiation().getOwner()).getBusinessLogo();
			if (!Strings.isNullOrEmpty(businessLogo)) {
				dataMap.put("logo", ExConfiguration.getStringProperty("site.url") + "/files/" + businessLogo);
			}
			if (notificationEvent.getNegotiation().getOwner().getProfile().isBusinessProfile()) {
				dataMap.put("actor_company", notificationEvent.getNegotiation().getOwner().getName());
			}
		} else if (NotificationType.NEGOTIATION_INVITATION_UPDATED == notificationEvent.getNotificationType()) {

			dataMap.put("actor_name",
					notificationEvent.getNegotiationInvitation().getInvitedMembership().getUser().getFullName());
			dataMap.put("decision",
					Messages.get(lang, notificationEvent.getNegotiationInvitation().getInvitationStatus().name()));
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid());
			to = (Membership) notificationEvent.getNegotiation().getOwner();
		} else if (NotificationType.NEGOTIATION_JOIN_REQUEST_UPDATED == notificationEvent.getNotificationType()) {

			dataMap.put("decision", Messages.get(lang,
					((AdmissionRequest) notificationEvent.getNegotiationMessage()).getStatus().name()));
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid());
			to = (Membership) notificationEvent.getNegotiationMessage().getSender();
		} else if (NotificationType.NEGOTIATION_JOIN_REQUESTED == notificationEvent.getNotificationType()) {

			dataMap.put("actor_name", notificationEvent.getNegotiationMessage().getSender().getUser().getFullName());
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid() + "/participants/");
			to = (Membership) notificationEvent.getNegotiation().getOwner();
		} else if (NotificationType.NEGOTIATION_MESSAGE_RECEIVED == notificationEvent.getNotificationType()) {

			dataMap.put("actor_name", notificationEvent.getNegotiationMessage().getSender().getUser().getFullName());
			dataMap.put("object_name", notificationEvent.getNegotiationMessage().getMessageType());
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid());
			to = (Membership) notificationEvent.getNegotiationMessage().getReceiver();
		} else if (NotificationType.NEGOTIATION_CREATED == notificationEvent.getNotificationType()) {
			dataMap.put("actor_name", notificationEvent.getNegotiation().getOwner().getUser().getFullName());
			dataMap.put("object_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid());

			IMembershipManager membershipManager = new MembershipManager();
			to = membershipManager
					.getOwnerMembership(notificationEvent.getNegotiation().getOwner().getProfile().getUuid());

			if (to.getUuid().equals(notificationEvent.getNegotiation().getOwner().getIdentifier())) // don't notify if
																									// deal and profile
																									// owner are same
				to = null;
		} else if (NotificationType.NEGOTIATION_PENDING_INVITATION_REMINDER == notificationEvent
				.getNotificationType()) {

			dataMap.put("actor_name", notificationEvent.getNegotiation().getOwner().getUser().getFullName());
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());

			if ("Sales Quote".equals(notificationEvent.getNegotiation().getInformationModelDocument().getTitle())
					|| "Preventivo".equals(notificationEvent.getNegotiation().getInformationModelDocument().getTitle()))
				dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid() + "/quote/"
						+ notificationEvent.getNegotiationInvitation().getUuid());
			else
				dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid() + "/description/"
						+ notificationEvent.getNegotiationInvitation().getUuid());

			if (notificationEvent.getNegotiationInvitation().getInvitedMembership() != null)
				to = (Membership) notificationEvent.getNegotiationInvitation().getInvitedMembership();
			else
				toEmail = notificationEvent.getNegotiationInvitation().getInvitedEmail();

			String businessLogo = ((Membership) notificationEvent.getNegotiation().getOwner()).getBusinessLogo();

			if (!Strings.isNullOrEmpty(businessLogo)) {
				dataMap.put("logo", ExConfiguration.getStringProperty("site.url") + "/files/" + businessLogo);
			}

			if (notificationEvent.getNegotiation().getOwner().getProfile().isBusinessProfile()) {
				dataMap.put("actor_company", notificationEvent.getNegotiation().getOwner().getName());
			}
		} else if (NotificationType.NEGOTIATION_AGREED == notificationEvent.getNotificationType()) {
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid());
			to = (Membership) notificationEvent.getStateMachine().getUser();
		} else if (NotificationType.NEGOTIATION_NOT_AGREED == notificationEvent.getNotificationType()) {
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid());
			to = (Membership) notificationEvent.getStateMachine().getUser();
		} else if (NotificationType.NEGOTIATION_QII_DATA_RECEIVED == notificationEvent.getNotificationType()) {
			dataMap.put("object_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid() + "/description/"
					+ notificationEvent.getNegotiationInvitation().getUuid());
			if (notificationEvent.getNegotiationInvitation().getInvitedMembership() != null)
				to = (Membership) notificationEvent.getNegotiationInvitation().getInvitedMembership();
			else
				toEmail = notificationEvent.getNegotiationInvitation().getInvitedEmail();
		} else if (NotificationType.NEGOTIATION_MEMBER_ADDED == notificationEvent.getNotificationType()) {
			dataMap.put("actor_name", notificationEvent.getMember().getUser().getFullName());
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());
			String url = TEAM_COLLABORATION_BASE_URL + "?objectType=" + TeamObjectType.NEGOTIATION.toString() + "&objectID=" + notificationEvent.getNegotiation().getUuid();
			dataMap.put("url", url);
			to = (Membership) notificationEvent.getMember();
		}

		sendWebHookNotification(notificationEvent, to);
		sendNotification(to, toEmail, notificationEvent.getNotificationType(), dataMap,
				notificationEvent.getNegotiation().getLanguage());
	}
}
