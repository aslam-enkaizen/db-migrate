package com.exrade.runtime.template.persistence;

import com.exrade.models.template.Template;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.template.persistence.TemplatePersistenceManager.TemplateQFilters;

public class TemplateQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + Template.class.getSimpleName()+ " where 1 = 1 ";
		
		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		
		if (iFilters.isNotNull(TemplateQFilters.OWNER_PROFILE)){
			query += andEq("ownerMembership.profile.uuid", iFilters.get(TemplateQFilters.OWNER_PROFILE));
		}
		
		if (iFilters.isNotNull(TemplateQFilters.OWNER_MEMBERSHIP)){
			query += andEq("ownerMembership.uuid", iFilters.get(TemplateQFilters.OWNER_MEMBERSHIP));
		}
		
		if (iFilters.isNotNull(TemplateQFilters.TEMPLATE_TYPE)){
			query += andEq(TemplateQFilters.TEMPLATE_TYPE, iFilters.get(TemplateQFilters.TEMPLATE_TYPE));
		}
		
		return query;
	}
}
