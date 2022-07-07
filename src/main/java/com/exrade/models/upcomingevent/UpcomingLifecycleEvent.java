package com.exrade.models.upcomingevent;

import com.exrade.models.activity.ObjectType;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.BaseEntityUUID;

import java.util.Date;
import java.util.List;

/**
 * @author Rhidoy
 * @created 29/10/2021
 * @package com.exrade.models.upcomingevent
 */
public class UpcomingLifecycleEvent extends BaseEntityUUID {
    private String eventUUID;
    private ObjectType objectType;
    private UpcomingLifecycleEventType eventType;
    private Date date;
    private List<Negotiator> members;

    public void setEventUUID(String eventUUID) {
        this.eventUUID = eventUUID;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public void setEventType(UpcomingLifecycleEventType eventType) {
        this.eventType = eventType;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMembers(List<Negotiator> members) {
        this.members = members;
    }

    public UpcomingLifecycleEventType getEventType() {
        return eventType;
    }

    public Date getDate() {
        return date;
    }

    public String getEventUUID() {
        return eventUUID;
    }

    public List<Negotiator> getMembers() {
        return members;
    }
}
