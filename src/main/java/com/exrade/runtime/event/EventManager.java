package com.exrade.runtime.event;

import com.exrade.models.event.Event;
import com.exrade.models.event.LogEventType;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.event.persistence.EventQuery;
import com.exrade.runtime.rest.RestParameters.EventFields;

import java.util.List;

public class EventManager {

	private PersistentManager persistentManager;

	private static final EventManager INSTANCE = new EventManager();

	private EventManager() {
		this(new PersistentManager());
	}
	
	public EventManager(PersistentManager iPersistentManager) {
		persistentManager = iPersistentManager;
	}

	public static EventManager getInstance() {
		return INSTANCE;
	}
	
	public List<Event> getEvents(QueryFilters iFilters){
		if (iFilters.isNull(QueryParameters.SORT)){
			iFilters.put(QueryParameters.SORT,EventFields.TIME);
		}
		return persistentManager.listObjects(new EventQuery(), iFilters);
	}
	
	public String create(Event event) {
		persistentManager.create(event);
		return event.getUuid();
	}
	
	public String create(Negotiator negotiator, LogEventType logEventType,String objectUUID,String content) {
		Event event = new Event(negotiator,logEventType,objectUUID,content);
		persistentManager.create(event);
		return event.getUuid();
	} 

	public Event findByUUID(String uuid){
		return persistentManager.readObjectByUUID(Event.class, uuid);
	}

}