package com.exrade.runtime.workgroup.persistence;

import com.exrade.core.ExLogger;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.workgroup.WorkGroup;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.PlainSql;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.WorkGroupFields;
import com.exrade.runtime.workgroup.persistence.WorkGroupPersistenceManager.WorkGroupQFilters;
import com.exrade.util.ContextHelper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class WorkGroupQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		Negotiator negotiator = ContextHelper.getMembership();
		ExLogger.get().debug("Negotiator: " + negotiator.getId());
		String nquery = "select from " + WorkGroup.class.getSimpleName() + " where 1 = 1 ";
		
		//nquery += addEqFilter(iFilters, WorkGroupQFilters.NEGOTIATION_UUID);
		if (!iFilters.isNullOrEmpty(WorkGroupFields.NEGOTIATION_UUID))
			nquery += and("negotiations" + " contains ("+eq(RestParameters.UUID, iFilters.get(WorkGroupFields.NEGOTIATION_UUID))+")");
		
		nquery += and(getInvolvedStatement(negotiator));
		
		if (!iFilters.isNullOrEmpty(RestParameters.KEYWORDS)){
			List<String> keywords = Lists.newArrayList(Splitter.on(" ").trimResults()
				       .omitEmptyStrings().split((String) iFilters.get(RestParameters.KEYWORDS)));
			for (String keyword : keywords) {
				nquery += and(contains(QueryKeywords.ANY + ".toLowerCase()", keyword.toLowerCase()));
			}
		}
		
		if (!iFilters.isNullOrEmpty(WorkGroupQFilters.TAGS)){
			nquery += andIn(WorkGroupQFilters.TAGS, iFilters.get(WorkGroupQFilters.TAGS).toString().toLowerCase());
		}

		return nquery;
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
