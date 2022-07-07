package com.exrade.runtime.clause.persistence.query;

import com.exrade.models.informationmodel.Clause;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.AssetFields;
import com.exrade.runtime.rest.RestParameters.ClauseFields;

/**
 *
 * @author: Md. Aslam Hossain
 *
 */
public class ClauseQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {

		String query = "select from " + Clause.class.getSimpleName()+ " where 1 = 1 ";
		
		if (!iFilters.containsKey(ClauseFields.ARCHIVED)){
			query += and(" (archived is null or archived == false) ");
		}

		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}

		if (iFilters.isNotNull(ClauseFields.TITLE)){
			query += andEq(ClauseFields.TITLE, iFilters.get(ClauseFields.TITLE));
		}

		if (iFilters.isNotNull(ClauseFields.DESCRIPTION)) {
			query += andEq(ClauseFields.DESCRIPTION, iFilters.get(ClauseFields.DESCRIPTION));
		}
		
		if (iFilters.isNotNull(ClauseFields.CATEGORY)) {
			query += andEq(ClauseFields.CATEGORY, iFilters.get(ClauseFields.CATEGORY));
		}
		
		if (iFilters.isNotNull(ClauseFields.TAGS)){
			query += andIn(ClauseFields.TAGS, iFilters.get(ClauseFields.TAGS).toString().toLowerCase());
		}
		
		if (iFilters.isNotNull(ClauseFields.STANDARD_TEMPLATE_UUID)) {
			query += andEq(ClauseFields.STANDARD_TEMPLATE_UUID, iFilters.get(ClauseFields.STANDARD_TEMPLATE_UUID));
		}

		if (iFilters.isNotNull(ClauseFields.ALTERNATIVE_TEMPLATES_UUIDS)) {
			query += andIn(ClauseFields.ALTERNATIVE_TEMPLATES_UUIDS, iFilters.get(ClauseFields.ALTERNATIVE_TEMPLATES_UUIDS));
		}
		
		if (iFilters.isNotNull(ClauseFields.CREATOR)) {
			query += andEq(ClauseFields.CREATOR + ".uuid", iFilters.get(ClauseFields.CREATOR));
		}
		
		if (iFilters.isNotNull(ClauseFields.PROFILE)) {
			query += andEq(ClauseFields.PROFILE + ".uuid", iFilters.get(ClauseFields.PROFILE));
		}

		if (iFilters.isNotNull(ClauseFields.PUBLICATION_STATUS)) {
			query += andEq(ClauseFields.PUBLICATION_STATUS, iFilters.get(ClauseFields.PUBLICATION_STATUS));
		}
		
		if (!iFilters.isNullOrEmpty(RestParameters.KEYWORDS)){
			String keywordSearch = contains(ClauseFields.TITLE + ".toLowerCase()", iFilters.get(RestParameters.KEYWORDS).toString().toLowerCase());
			keywordSearch += or(contains(AssetFields.DESCRIPTION + ".toLowerCase()", iFilters.get(RestParameters.KEYWORDS).toString().toLowerCase()));
			query += and("(" + keywordSearch + ")");
		}


		return query;
	
	}

}
