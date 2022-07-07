package com.exrade.runtime.message.persistence;

import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;

public class MessageQuery extends OrientSqlBuilder {
	
	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + NegotiationMessage.class.getSimpleName()+ " where 1 = 1 ";
		
		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += and(eq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID)));
		}
		
		return query;
	}
	
}
