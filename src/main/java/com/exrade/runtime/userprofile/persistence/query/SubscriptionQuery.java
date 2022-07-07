package com.exrade.runtime.userprofile.persistence.query;

import com.exrade.models.userprofile.PlanSubscription;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.PlanSubscriptionFields;

public class SubscriptionQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from " + PlanSubscription.class.getSimpleName() +" where 1 = 1 ";

		nquery += addEqFilter(iFilters,QueryParameters.UUID);
		
		nquery += addEqFilter(iFilters, PlanSubscriptionFields.EXTERNAL_CLIENTID);
		
		nquery += addEqFilter(iFilters, PlanSubscriptionFields.EXTERNAL_SUBSCRIPTIONID);
		
		nquery += addEqFilter(iFilters, PlanSubscriptionFields.PROFILE_UUID);
		
		nquery += addEqFilter(iFilters, PlanSubscriptionFields.PLAN_UUID);

		return nquery;
	}

}
