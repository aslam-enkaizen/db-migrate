package com.exrade.runtime.reminder;

import com.exrade.models.calendar.Event;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.reminder.persistence.ReminderPersistentManager;
import com.exrade.util.ContextHelper;

import java.util.List;

public class ReminderManager implements IReminderManager {

	private ReminderPersistentManager reminderPersistentManager;

	public ReminderManager() {
		this(new ReminderPersistentManager());
	}

	public ReminderManager(ReminderPersistentManager iReminderPersistentManager) {
		this.reminderPersistentManager = iReminderPersistentManager;
	}
	
	@Override
	public List<Event> listReminders(QueryFilters filters) {
		return reminderPersistentManager.listReminders(filters,ContextHelper.getMembership());
	}

}
