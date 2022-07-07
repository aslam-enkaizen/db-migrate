package com.exrade.runtime.notification.persistence;

import com.exrade.models.notification.NotificationFeed;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.NotificationFeedFields;


public class NotificationFeedQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from " + NotificationFeed.class.getSimpleName() + " where 1 = 1 ";

		if (iFilters.isNotNull(QueryParameters.UUID)){
			nquery += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		else {
			if (iFilters.isNotNull(NotificationFeedFields.VISIBLE)){
				nquery += andEq(NotificationFeedFields.VISIBLE, iFilters.get(NotificationFeedFields.VISIBLE));
			}
			else {
				nquery += andEq(NotificationFeedFields.VISIBLE, true);
			}
		}

		if (iFilters.isNotNull(NotificationFeedFields.MEMBERSHIP)){
			nquery += andEq(NotificationFeedFields.MEMBERSHIP + "." + QueryParameters.UUID, iFilters.get(NotificationFeedFields.MEMBERSHIP));
		}

		if (iFilters.isNotNull(NotificationFeedFields.SEEN)){
			nquery += andEq(NotificationFeedFields.SEEN, iFilters.get(NotificationFeedFields.SEEN));
		}

		if (iFilters.isNotNull(NotificationFeedFields.READ)){
			nquery += andEq(NotificationFeedFields.READ, iFilters.get(NotificationFeedFields.READ));
		}

		if (!iFilters.isNullOrEmpty("type") && !iFilters.isNullOrEmpty("objectID")){
			nquery += and("( (activity.object.type='" + iFilters.get("type") + "' and activity.object.objectID='" + iFilters.get("objectID")
						+ "') or (activity.target.type='" + iFilters.get("type") + "' and activity.target.objectID = '" + iFilters.get("objectID") + "')"
						+ "or (activity.extraContext.objectID = '" + iFilters.get("objectID") + "')" + ")");
		}

		return nquery;
	}

}
