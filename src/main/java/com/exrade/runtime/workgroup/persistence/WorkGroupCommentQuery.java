package com.exrade.runtime.workgroup.persistence;

import com.exrade.models.workgroup.WorkGroupComment;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.PlainSql;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.WorkGroupCommentFields;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class WorkGroupCommentQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from " + WorkGroupComment.class.getSimpleName() + " where 1 = 1 ";
		
		if (!iFilters.isNullOrEmpty(RestParameters.KEYWORDS)){
			List<String> keywords = Lists.newArrayList(Splitter.on(" ").trimResults()
				       .omitEmptyStrings().split((String) iFilters.get(RestParameters.KEYWORDS)));
			for (String keyword : keywords) {
				nquery += and(contains(QueryKeywords.ANY + ".toLowerCase()", keyword.toLowerCase()));
			}
		}
		
		nquery += andEq(WorkGroupCommentFields.POST, PlainSql.get(iFilters.get(WorkGroupCommentFields.POST)));

		return nquery;
	}

}
