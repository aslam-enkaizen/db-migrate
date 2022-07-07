package com.exrade.models.upcomingevent.dto;

import com.exrade.models.activity.ObjectType;
import com.exrade.models.upcomingevent.UpcomingLifecycleEventType;

import java.util.Date;
import java.util.List;

/**
 * @author Rhidoy
 * @created 03/11/2021
 * @package com.exrade.models.upcomingevent
 */
public class UpcomingLifecycleEventCreateDTO {
    private final String eventUUID;
    private final ObjectType objectType;
    private final UpcomingLifecycleEventType eventType;
    private final Date date;
    private final List<String> members;

    public UpcomingLifecycleEventCreateDTO(String eventUUID, ObjectType objectType, UpcomingLifecycleEventType eventType, Date date, List<String> members) {
        this.eventUUID = eventUUID;
        this.objectType = objectType;
        this.eventType = eventType;
        this.date = date;
        this.members = members;
    }

    public String getEventUUID() {
        return eventUUID;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public UpcomingLifecycleEventType getEventType() {
        return eventType;
    }

    public Date getDate() {
        return date;
    }

    public List<String> getMembers() {
        return members;
    }
}
