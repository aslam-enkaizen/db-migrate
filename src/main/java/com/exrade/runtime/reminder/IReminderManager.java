package com.exrade.runtime.reminder;

import com.exrade.models.calendar.Event;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IReminderManager {
 
	public	List<Event> listReminders(QueryFilters filters);
 
}
