package com.exrade.runtime.atm.persistence;

import com.exrade.models.atm.ATMActivity;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.util.ExCollections;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class ATMActivityQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + ATMActivity.class.getSimpleName()+ " where 1 = 1 ";
		
		
		
		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		
		if (!iFilters.isNullOrEmpty("type")){
			query += and(in("type", ExCollections.commaSeparatedToList(iFilters.get("type").toString())));
		}
		
		if (!iFilters.isNullOrEmpty("tutorId")){
			query += and(in("tutorId", ExCollections.commaSeparatedToList(iFilters.get("tutorId").toString())));
		}
		
		if (!iFilters.isNullOrEmpty("maintainerId")){
			query += and(in("maintainerId", ExCollections.commaSeparatedToList(iFilters.get("maintainerId").toString())));
		}
		
		if (!iFilters.isNullOrEmpty("status")){
			query += and(in("status", ExCollections.commaSeparatedToList(iFilters.get("status").toString())));
		}
		
		if (!iFilters.isNullOrEmpty("assetId")){
			query += and(in("assetId", ExCollections.commaSeparatedToList(iFilters.get("assetId").toString())));
		}
		
		if (!iFilters.isNullOrEmpty("partNumber")){
			query += and(in("partNumber", ExCollections.commaSeparatedToList(iFilters.get("partNumber").toString())));
		}
		
		if (!iFilters.isNullOrEmpty("keywords")){
			List<String> keywords = Lists.newArrayList(Splitter.on(" ").trimResults()
				       .omitEmptyStrings().split((String) iFilters.get("keywords")));
			for (String keyword : keywords) {
				query += and("any().toLowerCase() like '%"+keyword.toLowerCase()+"%'");
			}
		}
		
		return query;
	}

}
