package com.exrade.runtime.activity.persistence;

import com.exrade.models.activity.Activity;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.Map;
import java.util.Map.Entry;


public class ActivityQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from " + Activity.class.getSimpleName() + " where 1 = 1 ";

		if (iFilters.isNotNull(QueryParameters.UUID)){
			nquery += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}

		if (!iFilters.isNullOrEmpty("type") && !iFilters.isNullOrEmpty("objectID")){
			nquery += and("( (object.type='" + iFilters.get("type") + "' and object.objectID='" + iFilters.get("objectID")
						+ "') or (target.type='" + iFilters.get("type") + "' and target.objectID = '" + iFilters.get("objectID") + "')"
						+ " or (extraContext.objectID = '" + iFilters.get("objectID") + "')" + ")");
		}

		if (!iFilters.isNullOrEmpty("verb")){
			nquery += andEq("verb", iFilters.get("verb"));
		}
		else if (!iFilters.isNullOrEmpty(ActivityQFilters.INCLIDE_VERBS)){
			nquery += andIn("verb", iFilters.get(ActivityQFilters.INCLIDE_VERBS));
		}
		else if (!iFilters.isNullOrEmpty(ActivityQFilters.EXCLUDE_VERBS)){
			nquery += andNotIn("verb", iFilters.get(ActivityQFilters.EXCLUDE_VERBS));
		}

		if (iFilters.isNotNull(ActivityQFilters.PUBLISHED_FROM_DATETIME)){
			nquery += and(condition("published", iFilters.get(ActivityQFilters.PUBLISHED_FROM_DATETIME),Operator.GTEQ));
		}

		if (iFilters.isNotNull(ActivityQFilters.PUBLISHED_TO_DATETIME)){
			nquery += and(condition("published", iFilters.get(ActivityQFilters.PUBLISHED_TO_DATETIME),Operator.LTEQ));
		}

		if (!iFilters.isNullOrEmpty(ActivityQFilters.EXTRA_CONTEXT)){
			@SuppressWarnings("unchecked")
			Map<String, String> extraContext = (Map<String, String>)iFilters.get(ActivityQFilters.EXTRA_CONTEXT);

			if(extraContext != null) {
				for(Entry<String, String> extraContextItem : extraContext.entrySet()) {
					nquery += andEq(ActivityQFilters.EXTRA_CONTEXT + "." + extraContextItem.getKey(), extraContextItem.getValue());
				}
			}

		}

		return nquery;
	}

	public static final class ActivityQFilters {
		public static final String PUBLISHED_FROM_DATETIME = "publishedFromDateTime";
		public static final String PUBLISHED_TO_DATETIME = "publishedToDateTime";
		public static final String EXTRA_CONTEXT = "extraContext";
		public static final String INCLIDE_VERBS = "includeVerbs";
		public static final String EXCLUDE_VERBS = "excludeVerbs";
	}

}
