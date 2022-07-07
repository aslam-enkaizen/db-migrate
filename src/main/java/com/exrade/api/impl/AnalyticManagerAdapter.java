package com.exrade.api.impl;

import com.exrade.api.AnalyticAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.analytic.AnalyticRequest;
import com.exrade.models.analytic.AnalyticResponse;
import com.exrade.models.analytic.Dimension;
import com.exrade.models.analytic.Metric;
import com.exrade.runtime.analytic.AnalyticManager;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.rest.RestParameters.AnalyticRequestFields;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Map;

public class AnalyticManagerAdapter implements AnalyticAPI {

	private AnalyticManager manager = new AnalyticManager();
	
	@Override
	public AnalyticResponse getResult(ExRequestEnvelope requestEnvelope, Map<String, String> filterParams) throws ParseException {
		ContextHelper.initContext(requestEnvelope);
		
		AnalyticRequest analyticRequest = new AnalyticRequest();
		analyticRequest.setDimensions(ExCollections.commaSeparatedToEnumList(filterParams.get(AnalyticRequestFields.DIMENSIONS), Dimension.class));
		analyticRequest.setMetrics(ExCollections.commaSeparatedToEnumList(filterParams.get(AnalyticRequestFields.METRICS), Metric.class));
		analyticRequest.setStartDate(DateUtils.parseDate(filterParams.get(AnalyticRequestFields.START_DATE), ExConfiguration.getPropertyAsStringArray("DATE_FORMATS")));
		analyticRequest.setEndDate(DateUtils.parseDate(filterParams.get(AnalyticRequestFields.END_DATE) + " 23:59:59.999", ExConfiguration.getPropertyAsStringArray("DATE_TIME_FORMATS")));
		analyticRequest.setObjectID(filterParams.get(AnalyticRequestFields.OBJECT_ID));
		return manager.getResult(analyticRequest);
	}

	
	
}
