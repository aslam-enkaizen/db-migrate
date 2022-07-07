package com.exrade.runtime.notification.persistence;

import com.exrade.models.notification.NotificationSetting;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.NotificationSettingFields;


public class NotificationSettingQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from " + NotificationSetting.class.getSimpleName() + " where 1 = 1 ";
		
		if (iFilters.isNotNull(QueryParameters.UUID)){
			nquery += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		
		if (iFilters.isNotNull(NotificationSettingFields.ACTIVE)){
			nquery += andEq(NotificationSettingFields.ACTIVE, iFilters.get(NotificationSettingFields.ACTIVE));
		}
		
		if (iFilters.isNotNull(NotificationSettingFields.CHANNEL_TYPE)){
			nquery += andEq(NotificationSettingFields.CHANNEL_TYPE, iFilters.get(NotificationSettingFields.CHANNEL_TYPE));
		}
		
		if (iFilters.isNotNull(NotificationSettingFields.NOTIFICATION_TYPE)){
			nquery += andEq(NotificationSettingFields.NOTIFICATION_TYPE, iFilters.get(NotificationSettingFields.NOTIFICATION_TYPE));
		}
		
		return nquery;
	}

}
