package com.exrade.runtime.notification.handler;

import com.exrade.models.contract.ContractUserMember;
import com.exrade.models.contract.IContractMember;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.ContractNotificationEvent;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.timer.TrakSmartContractScheduler;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.DateUtil;

import java.util.*;

public class ContractNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/contracts/";

	@Override
	public <T> void handle(NotificationEvent<T> event) {
		ContractNotificationEvent notificationEvent = (ContractNotificationEvent) event;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<Negotiator> receivers = new ArrayList<>();
		Negotiator actor = ContextHelper.getMembership();

		if (actor != null)
			dataMap.put("actor_name", ((Membership) actor).getFullName());

		if (NotificationType.CONTRACT_EXPIRING_REMINDER == notificationEvent.getNotificationType()) {
			dataMap.put("target_name", notificationEvent.getPayload().getTitle());
			dataMap.put("expiration_date", notificationEvent.getPayload().getExpiryDate());
			dataMap.put("url", BASE_URL + notificationEvent.getPayload().getUuid());

			for (IContractMember member : notificationEvent.getPayload().getContractMembers()) {
				if (member.getMemberObjectType().equals(ContractUserMember.MEMBER_OBJECT_TYPE)) {
					receivers.add(((ContractUserMember) member).getNegotiator());
				}
			}
		} else if (NotificationType.CONTRACT_NEXT_RENEWAL_REMINDER == notificationEvent.getNotificationType()) {
			dataMap.put("target_name", notificationEvent.getPayload().getTitle());
			dataMap.put("expiration_date", notificationEvent.getPayload().getExpiryDate());
			dataMap.put("url", BASE_URL + notificationEvent.getPayload().getUuid());

			for (IContractMember member : notificationEvent.getPayload().getContractMembers()) {
				if (member.getMemberObjectType().equals(ContractUserMember.MEMBER_OBJECT_TYPE)) {
					receivers.add(((ContractUserMember) member).getNegotiator());
				}
			}
		} else if (NotificationType.CONTRACT_TERMINATION_NOTICE_DEADLINE_REMINDER == notificationEvent
				.getNotificationType()) {
			dataMap.put("target_name", notificationEvent.getPayload().getTitle());
			dataMap.put("expiration_date", notificationEvent.getPayload().getExpiryDate());
			dataMap.put("url", BASE_URL + notificationEvent.getPayload().getUuid());

			int terminationNoticePeriodInMills = (int) notificationEvent.getPayload().getLifecycleSetting()
					.getTerminationNoticePeriodInMilliseconds();
			Date terminationNoticeExpirationDate = DateUtil.addWithCurrentDate(Calendar.MILLISECOND,
					-terminationNoticePeriodInMills, false);
			dataMap.put("termination_notice_expiration_date", terminationNoticeExpirationDate);

			for (IContractMember member : notificationEvent.getPayload().getContractMembers()) {
				if (member.getMemberObjectType().equals(ContractUserMember.MEMBER_OBJECT_TYPE)) {
					receivers.add(((ContractUserMember) member).getNegotiator());
				}
			}
		} else if (NotificationType.CONTRACT_MEMBER_ADDED == notificationEvent.getNotificationType()) {
			dataMap.put("object_name", notificationEvent.getMember().getFullName());
			dataMap.put("target_name", notificationEvent.getPayload().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getPayload().getUuid());
			receivers.add(notificationEvent.getMember());
		} else if (NotificationType.CONTRACT_MEMBER_REMOVED == notificationEvent.getNotificationType()) {
			dataMap.put("object_name", notificationEvent.getMember().getFullName());
			dataMap.put("target_name", notificationEvent.getPayload().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getPayload().getUuid());
			receivers.add(notificationEvent.getMember());
		} else if (NotificationType.CONTRACT_CREATED == notificationEvent.getNotificationType()) {
			dataMap.put("object_name", notificationEvent.getPayload().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getPayload().getUuid());

			IMembershipManager membershipManager = new MembershipManager();
			receivers.add(membershipManager.getOwnerMembership(notificationEvent.getPayload().getOwnerProfileUUID()));
			
			TrakSmartContractScheduler.schedule(event);
		}

		sendWebHookNotification(notificationEvent, receivers);
		sendNotification(actor, receivers, notificationEvent.getNotificationType(), dataMap);
	}

}
