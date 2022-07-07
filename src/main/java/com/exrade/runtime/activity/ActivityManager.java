package com.exrade.runtime.activity;

import com.exrade.models.activity.Activity;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.activity.persistence.ActivityQuery;
import com.exrade.runtime.rest.RestParameters.ActivityFields;

import java.util.List;

public class ActivityManager implements IActivityManager {

	private PersistentManager persistenceManager = new PersistentManager();
	
	@Override
	public Activity readByUUID(String iActivityUUID) {
		return persistenceManager.readObjectByUUID(Activity.class, iActivityUUID);
	}

	@Override
	public void delete(String iActivityUUID) {
		persistenceManager.delete(readByUUID(iActivityUUID));
	}

	@Override
	public List<Activity> listActivities(QueryFilters filters) {
		filters.putIfNotEmpty(QueryParameters.SORT, OrientSqlBuilder.DESC_SORT+ActivityFields.PUBLISHED);
		return persistenceManager.listObjects(new ActivityQuery(), filters);
	}

	@Override
	public void update(Activity activity) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public Activity store(Activity activity) {
		persistenceManager.create(activity);
		return readByUUID(activity.getUuid());
	}

}
