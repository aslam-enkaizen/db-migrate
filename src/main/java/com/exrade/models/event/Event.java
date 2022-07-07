package com.exrade.models.event;

import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.BaseEntityUUID;

public class Event extends BaseEntityUUID {

	protected Negotiator user;
	protected String objectUUID;
	protected Long time;
	protected LogEventType logEventType;
	protected String note;

	public Event() {
	}
	
	public Event(Negotiator negotiator, LogEventType logEventType,String objectUUID,Long time, String note) {
		this.user = negotiator;
		this.logEventType = logEventType;
		this.objectUUID = objectUUID;
		this.note = note;
		this.time = time != null ? time : System.currentTimeMillis();
	}
	
	public Event(Negotiator user, LogEventType logEventType,String objectUUID, String note) {
		this(user,logEventType,objectUUID,System.currentTimeMillis(), note);
	}

	public Negotiator getUser() {
		return user;
	}

	public Long getTime() {
		return time;
	}

	public LogEventType getLogEventType() {
		return logEventType;
	}
	
	public String getObjectUUID() {
		return objectUUID;
	}
	
	public void setObjectUUID(String objectUUID) {
		this.objectUUID = objectUUID;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}