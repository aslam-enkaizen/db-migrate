package com.exrade.runtime.integration.persistence;

import com.exrade.models.integration.IntegrationSetting;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.IntegrationSettingFields;
import com.exrade.runtime.rest.RestParameters.IntegrationSettingFilters;

public class IntegrationSettingQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + IntegrationSetting.class.getSimpleName() + " where 1 = 1 ";
		
		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		
		if (iFilters.isNotNull(IntegrationSettingFields.INTEGRATION_SERVICE_TYPE)){
			query += andEq(IntegrationSettingFields.INTEGRATION_SERVICE_TYPE, iFilters.get(IntegrationSettingFields.INTEGRATION_SERVICE_TYPE));
		}
		
		if (iFilters.isNotNull(IntegrationSettingFilters.PROFILE_UUID)){
			query += andEq("profile.uuid", iFilters.get(IntegrationSettingFilters.PROFILE_UUID));
		}
		
		return query;
	}

}
