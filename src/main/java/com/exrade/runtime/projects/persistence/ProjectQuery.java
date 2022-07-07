package com.exrade.runtime.projects.persistence;

import com.exrade.models.projects.Project;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.PlainSql;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.ProjectFields;

public class ProjectQuery extends OrientSqlBuilder {
	
	public ProjectQuery() {
	}
	
	@Override
	public String buildQuery(QueryFilters filters) {
		String query = "select from " + Project.class.getSimpleName() + " where 1 = 1 ";

		if (filters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, filters.get(QueryParameters.UUID));
		}

		if (getActor()!= null){
			query += andEq(ProjectFields.OWNER,PlainSql.get(getActor().getId()));
		}
		
		if (filters.isNotNull(ProjectFields.NAME)){
			query += and(ProjectFields.NAME +" like "+filters.get(ProjectFields.NAME)+"%");
		}
		
		if (filters.isNotNull(ProjectFields.PRIORITY)){
			query += andEq(ProjectFields.PRIORITY,filters.get(ProjectFields.PRIORITY));
		}
		
		if (filters.isNotNull(ProjectFields.NEGOTIATIONS)){
			query += andIn(ProjectFields.NEGOTIATIONS, filters.get(ProjectFields.NEGOTIATIONS));
		}

		return query;
	}
}
