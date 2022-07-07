package com.exrade.runtime.notification.event;

import com.exrade.models.contract.Contract;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.upcomingevent.UpcomingLifecycleEventType;
import com.exrade.models.upcomingevent.dto.UpcomingLifecycleEventNotificationDTO;
import com.exrade.models.userprofile.Negotiator;

import java.util.List;

public class UpcomingLifeCycleEventNotificationEvent extends NotificationEvent<UpcomingLifecycleEventNotificationDTO> {

    public UpcomingLifeCycleEventNotificationEvent(NotificationType source, UpcomingLifecycleEventNotificationDTO payload) {
        super(source, payload);
    }

    public Contract getContract() {
        return (Contract) payload.getEvent();
    }

    public List<Negotiator> getMembers() {
        return payload.getMember();
    }

    public UpcomingLifecycleEventType getEventType() {
        return payload.getEventType();
    }
}
