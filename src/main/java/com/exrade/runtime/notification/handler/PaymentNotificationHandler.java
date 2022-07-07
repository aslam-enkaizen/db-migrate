package com.exrade.runtime.notification.handler;

import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.event.PaymentNotificationEvent;
import com.exrade.runtime.rest.RestParameters;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rhidoy
 * @created 07/06/2022
 * <p>
 * This class Sent Notification when payment completed or payment is pending or when paypal subscription done or cancel.
 */
public class PaymentNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

    private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/"
            + RestParameters.Resources.NEGOTIATIONS + "/";

    @Override
    public <T> void handle(NotificationEvent<T> event) {
        PaymentNotificationEvent ev = (PaymentNotificationEvent) event;
        Map<String, Object> dataMap = new HashMap<>();
        List<Negotiator> receivers = ev.getPayload().getReceiver();
        Negotiator actor = ev.getPayload().getActor();

        dataMap.put("actor_name", ((Membership) actor).getFullName());
        // adding actor
        dataMap.put("object_name", ev.getPayload().getNegotiationTitle());
        dataMap.put("url", BASE_URL + ev.getPayload().getNegotiationUuid());

        if (ev.getNotificationType().equals(NotificationType.PAYMENT_PENDING)) {
            //send notification only to actor
            receivers = Collections.singletonList(actor);
        }

        for (Negotiator receiver : receivers) {
//            if (((Membership) receiver).getFullName().equals(dataMap.get("actor_name")))
//                dataMap.put("actor_name", "You");
            sendNotification(actor, receiver.getUser().getEmail(), ev.getNotificationType(), dataMap);
        }

        sendWebHookNotification(ev, receivers);

    }
}
