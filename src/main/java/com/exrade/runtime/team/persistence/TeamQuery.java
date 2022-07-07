package com.exrade.runtime.team.persistence;

import com.exrade.models.team.Team;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.TeamFields;

public class TeamQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + Team.class.getSimpleName()+ " where 1 = 1 ";

		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}

		if (iFilters.isNotNull(TeamFields.OBJECT_TYPE)){
			query += andEq(TeamFields.OBJECT_TYPE, iFilters.get(TeamFields.OBJECT_TYPE));
		}

		if (iFilters.isNotNull(TeamFields.OBJECT_ID)){
			query += andEq(TeamFields.OBJECT_ID, iFilters.get(TeamFields.OBJECT_ID));
		}

		if (!iFilters.isNullOrEmpty(TeamFields.PROFILE_UUID)){
			query += andEq("profile.uuid", iFilters.get(TeamFields.PROFILE_UUID));
		}

		return query;
	}
}
