package com.exrade.runtime.informationmodel.persistence.query;

import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;

import java.text.MessageFormat;

public class InformationTemplateSearchSummaryQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String resourceName = InformationModelTemplate.class.getSimpleName();
		String profileUUID = iFilters.getOrDefault("profile.uuid", "").toString();

		if(!iFilters.isNullOrEmpty(QueryParameters.FIELD)){
			if(iFilters.get(QueryParameters.FIELD).equals("tags")) {
				return MessageFormat.format("select value, count(*) from (select expand(tags) from {0} where "
						+ "authorMembership is not null and authorMembership.profile.uuid = ''{1}'') where value <> '''' group by value order by count desc", resourceName, profileUUID);
			}
			else if(iFilters.get(QueryParameters.FIELD).equals("category")) {
				return MessageFormat.format("select category as value, count(*) from {0} where "
						+ "authorMembership is not null and authorMembership.profile.uuid = ''{1}'' group by category order by count desc", resourceName, profileUUID);
			}

		}

		return null;
	}
}
