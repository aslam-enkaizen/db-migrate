package com.exrade.runtime.notification.handler;

import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.event.WorkGroupNotificationEvent;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkGroupNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/workgroups/";
	
	@Override
	public <T> void handle(NotificationEvent<T> event) {
		WorkGroupNotificationEvent notificationEvent = (WorkGroupNotificationEvent) event;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<Negotiator> receivers = new ArrayList<>();
		Negotiator actor = ContextHelper.getMembership();
		
		if(actor != null)
			dataMap.put("actor_name", ((Membership)actor).getFullName());
		
		if(NotificationType.WORKGROUP_COMMENT_CREATED == notificationEvent.getNotificationType()){
			dataMap.put("target_name", notificationEvent.getPost().getTitle());
			dataMap.put("url", BASE_URL + notificationEvent.getWorkGroup().getUuid() + "/posts/" + notificationEvent.getPost().getUuid());
			receivers.add(notificationEvent.getWorkGroup().getOwner());
			receivers.addAll(notificationEvent.getWorkGroup().getMembers());
		}
		else if(NotificationType.WORKGROUP_MEMBER_ADDED == notificationEvent.getNotificationType()){
			dataMap.put("object_name", notificationEvent.getMember().getFullName());
			dataMap.put("target_name", notificationEvent.getWorkGroup().getName());
			dataMap.put("url", BASE_URL + notificationEvent.getWorkGroup().getUuid());
			receivers.add(notificationEvent.getMember());
		}
		else if(NotificationType.WORKGROUP_NEGOTIATION_ADDED == notificationEvent.getNotificationType()){
			dataMap.put("object_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("target_name", notificationEvent.getWorkGroup().getName());
			dataMap.put("url", BASE_URL + notificationEvent.getWorkGroup().getUuid());
			receivers.add(notificationEvent.getWorkGroup().getOwner());
			receivers.addAll(notificationEvent.getWorkGroup().getMembers());
		}
		else if(NotificationType.WORKGROUP_POST_CREATED == notificationEvent.getNotificationType()){
			dataMap.put("object_name", notificationEvent.getPost().getTitle());
			dataMap.put("target_name", notificationEvent.getWorkGroup().getName());
			dataMap.put("url", BASE_URL + notificationEvent.getWorkGroup().getUuid() + "/posts/" + notificationEvent.getPost().getUuid());
			receivers.add(notificationEvent.getWorkGroup().getOwner());
			receivers.addAll(notificationEvent.getWorkGroup().getMembers());
		}
		else if(NotificationType.WORKGROUP_NEGOTIATION_REMOVED == notificationEvent.getNotificationType()){
			dataMap.put("object_name", notificationEvent.getNegotiation().getTitle());
			dataMap.put("target_name", notificationEvent.getWorkGroup().getName());
			dataMap.put("url", BASE_URL + notificationEvent.getWorkGroup().getUuid());
			receivers.add(notificationEvent.getWorkGroup().getOwner());
			receivers.addAll(notificationEvent.getWorkGroup().getMembers());
		}
		else if(NotificationType.WORKGROUP_MEMBER_REMOVED == notificationEvent.getNotificationType()){
			dataMap.put("object_name", notificationEvent.getMember().getFullName());
			dataMap.put("target_name", notificationEvent.getWorkGroup().getName());
			dataMap.put("url", BASE_URL + notificationEvent.getWorkGroup().getUuid());
			receivers.add(notificationEvent.getMember());
		}
		else if(NotificationType.WORKGROUP_CREATED == notificationEvent.getNotificationType()){
			dataMap.put("object_name", notificationEvent.getWorkGroup().getName());
			dataMap.put("url", BASE_URL + notificationEvent.getWorkGroup().getUuid());
			
			IMembershipManager membershipManager = new MembershipManager();
			receivers.add(membershipManager.getOwnerMembership(notificationEvent.getWorkGroup().getOwner().getProfileUUID()));
		}
		
		sendWebHookNotification(notificationEvent, receivers);
		sendNotification(actor, receivers, notificationEvent.getNotificationType(), dataMap);
	}
}
