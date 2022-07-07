package com.exrade.models.calendar;

import com.google.common.base.MoreObjects;

import java.util.Date;

public class Event implements Comparable<Event> {

	private String name;
	private String description;
	private String negotiationUUID;
	private Date time;
	private boolean ownedNegotiation;

	public Event(Date iTime,String iName,String iDescription,
			String iNegotiationUUID,boolean iOwnedNegotiation) {
		time = iTime;
		name = iName;
		description = iDescription;
		negotiationUUID = iNegotiationUUID;
		ownedNegotiation = iOwnedNegotiation;
	}

	public Event() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getNegotiationUUID() {
		return negotiationUUID;
	}

	public Date getTime() {
		return time;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass().getSimpleName()).add("name", getName()).add("time",getTime())
		.add("negotiationUUID", getNegotiationUUID()).toString();
	}

	public boolean isOwnedNegotiation() {
		return ownedNegotiation;
	}

	@Override
	public int compareTo(Event o) {
		Date external = (o == null || o.getTime() == null) ? new Date(0) : o.getTime();
		Date local = getTime() == null ? new Date(0) : getTime();
		return local.compareTo(external);
	}

}
