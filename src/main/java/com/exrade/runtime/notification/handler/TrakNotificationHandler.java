package com.exrade.runtime.notification.handler;

import com.exrade.models.notification.NotificationType;
import com.exrade.models.trak.ApprovalResponseType;
import com.exrade.models.trak.TrakStatus;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.event.TrakNotificationEvent;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.timer.TrakSmartContractScheduler;
import com.exrade.util.ContextHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rhidoy
 * @created 19/10/2021
 * @package com.exrade.runtime.notification.handler
 *          <p>
 *          This class Sent Notification when a Trak or Trak Response or Trak
 *          Response Approval create or modified.
 */
public class TrakNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/"
			+ RestParameters.Resources.TRAKS + "/";

	@Override
	public <T> void handle(NotificationEvent<T> event) {
		TrakNotificationEvent ev = (TrakNotificationEvent) event;
		Map<String, Object> dataMap = new HashMap<>();
		List<Negotiator> receivers = new ArrayList<>();
		Negotiator actor = ContextHelper.getMembership();

		if (actor != null)
			dataMap.put("actor_name", ((Membership) actor).getFullName());

		if (NotificationType.TRAK_CREATED == ev.getNotificationType()) {
			// adding actor
			dataMap.put("actor_name", ((Membership) ev.getPayload().getCreator()).getFullName());
			dataMap.put("object_name", ev.getPayload().getTitle());
			dataMap.put("url", BASE_URL + ev.getPayload().getUuid());
			
			if (ev.getPayload().getAssignee() != null)
				receivers.add(ev.getPayload().getAssignee());
			
			if (ev.getPayload().getApprover() != null)
				receivers.add(ev.getPayload().getApprover());
			
		} else if (NotificationType.TRAK_UPDATED == ev.getNotificationType()) {
			dataMap.put("object_name", ev.getPayload().getTitle());
			dataMap.put("url", BASE_URL + ev.getPayload().getUuid());
			
			if (ev.getPayload().getAssignee() != null)
				receivers.add(ev.getPayload().getAssignee());
			
			if (ev.getPayload().getApprover() != null)
				receivers.add(ev.getPayload().getApprover());
			
		} else if (NotificationType.TRAK_RESPONSE_CREATED == ev.getNotificationType()) {
			dataMap.put("object_name", ev.getPayload().getTitle());
			dataMap.put("url", BASE_URL + ev.getPayload().getUuid() + RestParameters.Resources.TRAK_RESPONSES + "/"
					+ ev.getTrakResponse().getUuid());
			
			if (ev.getPayload().getCreator() != null)
				receivers.add(ev.getPayload().getCreator());
			
			if (ev.getPayload().getApprover() != null)
				receivers.add(ev.getPayload().getApprover());
			
			if(!ev.getPayload().isApprovalRequired() && ev.getPayload().getStatus() == TrakStatus.COMPLETED)
				TrakSmartContractScheduler.schedule(event);
			
		} else if (NotificationType.TRAK_RESPONSE_UPDATED == ev.getNotificationType()) {
			dataMap.put("object_name", ev.getPayload().getTitle());
			dataMap.put("url", BASE_URL + ev.getPayload().getUuid() + RestParameters.Resources.TRAK_RESPONSES + "/"
					+ ev.getTrakResponse().getUuid());
			
			if (ev.getPayload().getCreator() != null)
				receivers.add(ev.getPayload().getCreator());
			
			if (ev.getPayload().getApprover() != null)
				receivers.add(ev.getPayload().getApprover());
			
			if(!ev.getPayload().isApprovalRequired() && ev.getPayload().getStatus() == TrakStatus.COMPLETED)
				TrakSmartContractScheduler.schedule(event);
			
		} else if (NotificationType.TRAK_APPROVAL_CREATED == ev.getNotificationType()) {
			dataMap.put("object_name", ev.getPayload().getTitle());
			dataMap.put("url",
					BASE_URL + ev.getPayload().getUuid() + RestParameters.Resources.TRAK_RESPONSES + "/"
							+ ev.getTrakResponse().getUuid() + "/" + RestParameters.Resources.TRAK_APPROVALS + "/"
							+ ev.getTrakApproval().getUuid());
			
			if (ev.getPayload().getCreator() != null)
				receivers.add(ev.getPayload().getCreator());
			
			if (ev.getPayload().getAssignee() != null)
				receivers.add(ev.getPayload().getAssignee());

			if (ev.getTrakApproval().getApprovalResponseType() == ApprovalResponseType.ACCEPTED)
				TrakSmartContractScheduler.schedule(event);
			
		} else if (NotificationType.TRAK_APPROVAL_UPDATED == ev.getNotificationType()) {
			dataMap.put("object_name", ev.getPayload().getTitle());
			dataMap.put("url",
					BASE_URL + ev.getPayload().getUuid() + RestParameters.Resources.TRAK_RESPONSES + "/"
							+ ev.getTrakResponse().getUuid() + "/" + RestParameters.Resources.TRAK_APPROVALS + "/"
							+ ev.getTrakApproval().getUuid());
			
			if (ev.getPayload().getCreator() != null)
				receivers.add(ev.getPayload().getCreator());
			
			if (ev.getPayload().getAssignee() != null)
				receivers.add(ev.getPayload().getAssignee());

			if (ev.getTrakApproval().getApprovalResponseType() == ApprovalResponseType.ACCEPTED)
				TrakSmartContractScheduler.schedule(event);
		}

		sendWebHookNotification(ev, receivers);
		sendNotification(actor, receivers, ev.getNotificationType(), dataMap);

		// TrakSmartContractScheduler
	}
}
