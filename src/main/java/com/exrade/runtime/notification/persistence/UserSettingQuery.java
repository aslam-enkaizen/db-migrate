package com.exrade.runtime.notification.persistence;

import com.exrade.models.notification.UserSetting;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.UserSettingFields;
import com.exrade.runtime.rest.RestParameters.UserSettingFilters;


public class UserSettingQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from " + UserSetting.class.getSimpleName() + " where 1 = 1 ";
		
		if (iFilters.isNotNull(QueryParameters.UUID)){
			nquery += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		
		if (iFilters.isNotNull(UserSettingFields.MEMBERSHIP)){
			nquery += andEq(UserSettingFields.MEMBERSHIP + "." + QueryParameters.UUID, iFilters.get(UserSettingFields.MEMBERSHIP));
		}
		
		if (iFilters.isNotNull(UserSettingFilters.NOTIFICATION_SETTING)){
			//nquery += andEq(UserSettingFields.MEMBERSHIP + "." + QueryParameters.UUID, iFilters.get(UserSettingFields.MEMBERSHIP));
		}
		
		return nquery;
	}

}
