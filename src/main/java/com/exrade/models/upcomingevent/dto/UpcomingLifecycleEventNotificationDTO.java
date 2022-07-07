package com.exrade.models.upcomingevent.dto;

import com.exrade.models.activity.ObjectType;
import com.exrade.models.upcomingevent.UpcomingLifecycleEventType;
import com.exrade.models.userprofile.Negotiator;

import java.util.List;

/**
 * @author Rhidoy
 * @created 04/11/2021
 * @package com.exrade.models.upcomingevent
 * <p>
 * This is mutable class for sending Upcoming Lifecycle Events from Alert Manager
 */
public class UpcomingLifecycleEventNotificationDTO {
    private final ObjectType objectType;
    private final UpcomingLifecycleEventType eventType;
    private final Object event;
    private final List<Negotiator> member;

    public UpcomingLifecycleEventNotificationDTO(ObjectType objectType, UpcomingLifecycleEventType eventType, Object event, List<Negotiator> members) {
        this.objectType = objectType;
        this.eventType = eventType;
        this.event = event;
        this.member = members;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public UpcomingLifecycleEventType getEventType() {
        return eventType;
    }

    public Object getEvent() {
        return event;
    }

    public List<Negotiator> getMember() {
        return member;
    }
}
