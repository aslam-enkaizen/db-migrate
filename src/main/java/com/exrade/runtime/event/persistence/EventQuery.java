package com.exrade.runtime.event.persistence;

import com.exrade.models.event.Event;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;

public class EventQuery  extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from " + Event.class.getSimpleName() +" where 1 = 1 ";

		nquery += addEqFilter(iFilters,QueryParameters.UUID);
		nquery += addEqFilter(iFilters,RestParameters.EventFields.LOG_EVENT_TYPE);
		nquery += addEqFilter(iFilters,RestParameters.EventFilters.USER_UUID);
		nquery += addEqFilter(iFilters,RestParameters.EventFields.OBJECT_UUID);

		return nquery;
	}

}
