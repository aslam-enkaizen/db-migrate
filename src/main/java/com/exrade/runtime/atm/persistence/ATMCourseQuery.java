package com.exrade.runtime.atm.persistence;

import com.exrade.models.atm.ATMCourse;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class ATMCourseQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + ATMCourse.class.getSimpleName()+ " where 1 = 1 ";
		
		
		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		
		if (!iFilters.isNullOrEmpty("tutorId")){
			query += andEq("tutorId", iFilters.get("tutorId"));
		}
		
		if (!iFilters.isNullOrEmpty("assetId")){
			query += andEq("assetId", iFilters.get("assetId"));
		}
		
		if (!iFilters.isNullOrEmpty("partNumber")){
			query += andEq("partNumber", iFilters.get("partNumber"));
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
