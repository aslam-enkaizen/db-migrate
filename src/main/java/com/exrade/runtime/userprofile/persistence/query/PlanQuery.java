package com.exrade.runtime.userprofile.persistence.query;

import com.exrade.models.userprofile.Plan;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.PermissionsFields;
import com.exrade.runtime.rest.RestParameters.PlanFields;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class PlanQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from " + Plan.class.getSimpleName() +" where 1 = 1 and active=true ";

		nquery += addEqFilter(iFilters,QueryParameters.UUID);
		nquery += addEqFilter(iFilters,PlanFields.NAME);
		nquery += addEqFilter(iFilters,PlanFields.DEFAULT_PLAN);
		if (!iFilters.isNullOrEmpty(PlanFields.PERMISSIONS)){
			Object value = iFilters.get(PlanFields.PERMISSIONS);
			List<String> permissionValues = Lists.newArrayList(Splitter.on(',').trimResults().split((String) value)
					.iterator());
			for (String permissionValue : permissionValues) {
				nquery += andCollectionCondition(PlanFields.PERMISSIONS,PermissionsFields.VALUE,permissionValue,Operator.EQ);
			}

		}

		return nquery;
	}

}
