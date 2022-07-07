package com.exrade.runtime.notification.handler;

import com.exrade.models.activity.ObjectType;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.upcomingevent.UpcomingLifecycleEventType;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.event.UpcomingLifeCycleEventNotificationEvent;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.util.DateUtil;

import java.util.*;

/**
 * @author Rhidoy
 * @created 04/11/2021
 * @package com.exrade.runtime.notification.handler
 * <p>
 * This class Sent Notification for upcoming life cycle events to specific user.
 */
public class UpcomingLifeCycleEventNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

    private static final String BASE_URL_CONTRACT = ExConfiguration
            .getStringProperty("site.url") + "/" +
            RestParameters.Resources.CONTRACTS + "/";

    @Override
    public <T> void handle(NotificationEvent<T> event) {
        UpcomingLifeCycleEventNotificationEvent ev = (UpcomingLifeCycleEventNotificationEvent) event;
        Map<String, Object> dataMap = new HashMap<>();
        List<Negotiator> receivers = new ArrayList<>();

        if (NotificationType.UPCOMING_LIFE_CYCLE_EVENTS
                == ev.getNotificationType()) {
            //when type is contract
            if (ev.getPayload().getObjectType() == ObjectType.CONTRACT) {
                if (ev.getEventType() == UpcomingLifecycleEventType.CONTRACT_EXPIRATION) {
                    dataMap.put("target_name", ev.getContract().getTitle());
                    dataMap.put("expiration_date", ev.getContract().getExpiryDate());
                    dataMap.put("url", BASE_URL_CONTRACT + ev.getContract().getUuid());

                    int terminationNoticePeriodInMills = (int) ev.getContract().getLifecycleSetting()
                            .getTerminationNoticePeriodInMilliseconds();
                    Date terminationNoticeExpirationDate = DateUtil.addWithCurrentDate(Calendar.MILLISECOND,
                            -terminationNoticePeriodInMills, false);
                    dataMap.put("termination_notice_expiration_date", terminationNoticeExpirationDate);
                    receivers = ev.getMembers();
                }
            }
        }

        sendWebHookNotification(ev, receivers);
        sendNotification(null, receivers, ev.getNotificationType(), dataMap);
    }
}
