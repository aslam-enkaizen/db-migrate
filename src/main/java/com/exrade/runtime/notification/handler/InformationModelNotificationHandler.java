package com.exrade.runtime.notification.handler;

import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.team.TeamObjectType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.InformationModelNotificationEvent;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.userprofile.TraktiJwtManager;
import com.exrade.util.ContextHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InformationModelNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/negotiation-templates/";
	private static final String TEAM_COLLABORATION_BASE_URL = ExConfiguration.getStringProperty("site.url") + "/collaboration/";

	@Override
	public <T> void handle(NotificationEvent<T> event) {
		InformationModelNotificationEvent notificationEvent = (InformationModelNotificationEvent) event;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<Negotiator> receivers = new ArrayList<>();

		Negotiator actor = ContextHelper.getMembership();

		if (actor != null)
			dataMap.put("actor_name", ((Membership) actor).getFullName());

		if (NotificationType.INFORMATION_MODEL_MEMBER_ADDED == notificationEvent.getNotificationType()) {
			InformationModelTemplate template = (InformationModelTemplate) notificationEvent.getInformationModel();
			dataMap.put("object_name", notificationEvent.getMember().getFullName());
			dataMap.put("target_name", template.getTitle());

			String url = TEAM_COLLABORATION_BASE_URL + "?objectType=" + TeamObjectType.INFORMATION_MODEL_TEMPLATE.toString() + "&objectID=" + template.getUuid();

			if (notificationEvent.getMember().isGuest()) {
				dataMap.put("url", url + "&token="
						+ TraktiJwtManager.getInstance().generateToken(notificationEvent.getMember().getIdentifier()));
				sendNotification(notificationEvent.getMember(), notificationEvent.getMember().getUser().getEmail(),
						notificationEvent.getNotificationType(), dataMap);
			} else {
				dataMap.put("url", url);
				receivers.add(notificationEvent.getMember());
			}
		}

		sendWebHookNotification(notificationEvent, receivers);
		sendNotification(actor, receivers, notificationEvent.getNotificationType(), dataMap);
	}

}
