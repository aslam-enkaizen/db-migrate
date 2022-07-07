package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.calendar.Event;

import java.util.List;
import java.util.Map;

public interface ReminderAPI {
 
	public	List<Event> listReminders(ExRequestEnvelope request, Map<String, String> iFilters);
 
}
