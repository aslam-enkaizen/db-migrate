package com.exrade.runtime.notification.handler;

import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.ImportNotificationEvent;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.rest.RestParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rhidoy
 * @created 22/04/2022
 * @package com.exrade.runtime.notification.handler
 * <p>
 * This class Sent Notification when a Import Task finished.
 */
public class ImportNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

    private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/profile/" + RestParameters.Resources.IMPORTS+"/";


    @Override
    public <T> void handle(NotificationEvent<T> event) {
        ImportNotificationEvent ev = (ImportNotificationEvent) event;
        Map<String, Object> dataMap = new HashMap<>();
        Membership receiver = ev.getPayload().getRequestor();

        if (NotificationType.IMPORT_FINISHED == ev.getNotificationType()) {
            // adding actor
            dataMap.put("actor_name", ev.getPayload().getRequestor().getFullName());
            dataMap.put("object_name", ev.getPayload().getObjectType());
            dataMap.put("url", BASE_URL+ev.getPayload().getUuid());
        }

        sendWebHookNotification(ev, receiver);
        sendNotification(receiver, receiver.getUser().getEmail(), ev.getNotificationType(), dataMap);
    }
}
