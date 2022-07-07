package com.exrade.api.impl;

import com.exrade.api.ActivityFeedAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.activity.Activity;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExParamException;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.activity.ActivityManager;
import com.exrade.runtime.activity.IActivityManager;
import com.exrade.runtime.activity.persistence.ActivityQuery.ActivityQFilters;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class ActivityFeedManagerAdapter implements ActivityFeedAPI {
	
	private IActivityManager manager = new ActivityManager();

	@Override
	public Activity getActivity(ExRequestEnvelope request, String iActivityUUID) {
		ContextHelper.initContext(request);
		return manager.readByUUID(iActivityUUID);
	}

	@Override
	public List<Activity> listActivities(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putIfNotEmpty("type", iFilters.get("type"));
		filters.putIfNotEmpty("objectID", iFilters.get("objectID"));
		
		List<String> verbsToInclude = ExCollections.commaSeparatedToList(iFilters.get(ActivityQFilters.INCLIDE_VERBS));
		filters.putIfNotEmpty(ActivityQFilters.INCLIDE_VERBS, verbsToInclude);
		
		List<String> verbsToExclude = ExCollections.commaSeparatedToList(iFilters.get(ActivityQFilters.EXCLUDE_VERBS));
		filters.putIfNotEmpty(ActivityQFilters.EXCLUDE_VERBS, verbsToExclude);
		
		if (iFilters.containsKey(RestParameters.START_DATE)){
			try {
				filters.put(ActivityQFilters.PUBLISHED_FROM_DATETIME, DateUtils.parseDate(iFilters.get(RestParameters.START_DATE), ExConfiguration.getPropertyAsStringArray("DATE_FORMATS")));
			} catch (ParseException e) {
				throw new ExParamException(ErrorKeys.PARAM_INVALID, RestParameters.START_DATE);
			}
		}
		if (iFilters.containsKey(RestParameters.END_DATE)){
			try {
				filters.put(ActivityQFilters.PUBLISHED_TO_DATETIME, DateUtils.parseDate(iFilters.get(RestParameters.END_DATE) + " 23:59:59.999", ExConfiguration.getPropertyAsStringArray("DATE_TIME_FORMATS")));
			} catch (ParseException e) {
				throw new ExParamException(ErrorKeys.PARAM_INVALID, RestParameters.END_DATE);
			}
		}
		
		return manager.listActivities(filters);
	}

}
