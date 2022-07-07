package com.exrade.runtime.workgroup.persistence;

import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.workgroup.WorkGroup;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.PlainSql;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.contact.persistence.ContactPersistenceManager.ContactQFilters;
import com.exrade.runtime.rest.RestParameters.WorkGroupFields;
import com.exrade.util.ContextHelper;

public class WorkGroupSearchSummaryQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		Negotiator negotiator = ContextHelper.getMembership();
		
		if(!iFilters.isNullOrEmpty(QueryParameters.FIELD) && iFilters.get(QueryParameters.FIELD).equals(ContactQFilters.TAGS)){
			String innerQueryquery = "select expand(tags) from " + WorkGroup.class.getSimpleName()+ " where 1 = 1 and tags is not null";
			
			innerQueryquery += and(getInvolvedStatement(negotiator));
			
			String query = "select value, count(*) from (" + innerQueryquery + ") group by value order by count desc";
			return query;
		}
		
		return null;
	}

	protected String getInvolvedStatement(Negotiator negotiator){
		return "(" + getParticipatedStatement(negotiator)
				+ or(getOwnedStatement(negotiator))
				+ ")";
	}
	
	protected String getOwnedStatement(Negotiator negotiator){
		String eqOwner = eq(WorkGroupFields.OWNER, PlainSql.get(negotiator.getId()));
		
		return eqOwner;
	}
	
	protected String getParticipatedStatement(Negotiator negotiator){
		String eqParticipated = in(WorkGroupFields.MEMBERS, PlainSql.get(negotiator.getId()));
		
		return eqParticipated;
	}
}
