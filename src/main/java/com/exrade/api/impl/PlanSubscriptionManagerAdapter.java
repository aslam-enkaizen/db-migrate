package com.exrade.api.impl;

import com.exrade.api.PlanSubscriptionAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.userprofile.Plan;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.PlanFields;
import com.exrade.runtime.userprofile.PlanManager;
import com.exrade.util.ContextHelper;

import java.util.List;
import java.util.Map;

public class PlanSubscriptionManagerAdapter implements PlanSubscriptionAPI {

	@Override
	public List<Plan> getPlans(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		return PlanManager.getInstance().getPlans(getQueryFilters(iFilters));
	}

	/*@Override
	public List<PlanLiveOffers> getPlansLiveOffers(ExRequestEnvelope request,
			Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		return PlanManager.getInstance().getPlansLiveOffers(getQueryFilters(iFilters));
	}*/
	
	private QueryFilters getQueryFilters(Map<String, String> iFilters){
		QueryFilters filters = QueryFilters.create(iFilters);

		filters.putIfNotNull(PlanFields.NAME, iFilters.get(PlanFields.NAME));
		filters.putIfNotNull(RestParameters.UUID, iFilters.get(RestParameters.UUID));
		filters.putIfNotNull(PlanFields.DEFAULT_PLAN, iFilters.get(PlanFields.DEFAULT_PLAN));
		filters.putIfNotNull(PlanFields.PERMISSIONS, iFilters.get(PlanFields.PERMISSIONS));
		filters.putIfNotNull(QueryParameters.SORT, iFilters.get(QueryParameters.SORT));
		
		return filters;
	}

	@Override
	public Plan createPlan(ExRequestEnvelope request, Plan iPlan) {
		ContextHelper.initContext(request);
		return PlanManager.getInstance().create(iPlan);
	}

	@Override
	public Plan updatePlan(ExRequestEnvelope request, Plan iPlan) {
		ContextHelper.initContext(request);
		return PlanManager.getInstance().update(iPlan);
	}

	@Override
	public void deletePlan(ExRequestEnvelope request, String iPlanUUID) {
		ContextHelper.initContext(request);
		PlanManager.getInstance().delete(iPlanUUID);
	}

}
