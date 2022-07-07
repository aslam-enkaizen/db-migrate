package com.exrade.runtime.activity;

import com.exrade.models.activity.Activity;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IActivityManager {

	Activity readByUUID(String iActivityUUID);

	void delete(String iActivityUUID);

	List<Activity> listActivities(QueryFilters filters);

	void update(Activity activity);

	Activity store(Activity activity);
	
}
