package com.exrade.api.impl;

import com.exrade.api.ReminderAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.calendar.Event;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.reminder.IReminderManager;
import com.exrade.runtime.reminder.ReminderManager;
import com.exrade.runtime.reminder.persistence.ReminderPersistentManager.ReminderQFilters;
import com.exrade.util.ContextHelper;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReminderManagerAdapter implements ReminderAPI {

	private IReminderManager manager = new ReminderManager();
	
	@Override
	public List<Event> listReminders(ExRequestEnvelope request,
			Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();
		
		QueryFilters filters = QueryFilters.create(iFilters);
		
		if ( iFilters.get(ReminderQFilters.FROM) != null) {
			Date dateStarts = new Date(Long.parseLong(iFilters.get(ReminderQFilters.FROM)));
			filters.putIfNotNull(ReminderQFilters.FROM, dateStarts);
		}
		if (iFilters.get(ReminderQFilters.TO) != null) {
			Date dateEnds = new Date(Long.parseLong(iFilters.get(ReminderQFilters.TO)));
			filters.putIfNotNull(ReminderQFilters.TO, dateEnds);
		}
		
		return manager.listReminders(filters);
	}

}
