package com.exrade.runtime.workgroup.persistence;

import com.exrade.models.workgroup.Post;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.PlainSql;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.PostFields;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class PostQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from " + Post.class.getSimpleName() + " where 1 = 1 ";
		
		if (!iFilters.isNullOrEmpty(RestParameters.KEYWORDS)){
			List<String> keywords = Lists.newArrayList(Splitter.on(" ").trimResults()
				       .omitEmptyStrings().split((String) iFilters.get(RestParameters.KEYWORDS)));
			for (String keyword : keywords) {
				nquery += and(contains(QueryKeywords.ANY + ".toLowerCase()", keyword.toLowerCase()));
			}
		}
		
		nquery += andEq(PostFields.WORKGROUP, PlainSql.get(iFilters.get(PostFields.WORKGROUP)));

		return nquery;
	}

}
