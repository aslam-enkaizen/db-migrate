package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.activity.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityFeedAPI {
	
	Activity getActivity(ExRequestEnvelope request, String iActivityUUID);

	List<Activity> listActivities(ExRequestEnvelope request, Map<String, String> iFilters);
}
