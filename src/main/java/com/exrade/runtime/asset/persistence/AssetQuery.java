package com.exrade.runtime.asset.persistence;

import com.exrade.models.asset.Asset;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.AssetFields;

public class AssetQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + Asset.class.getSimpleName()+ " where 1 = 1 ";

		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}

		if (iFilters.isNotNull(AssetFields.OWNER)){
			query += andEq(AssetFields.OWNER + ".uuid", iFilters.get(AssetFields.OWNER));
		}

		if (iFilters.isNotNull(AssetFields.OWNER_PROFILE)){
			query += andEq(AssetFields.OWNER_PROFILE + ".uuid", iFilters.get(AssetFields.OWNER_PROFILE));
		}

		if (iFilters.isNotNull(AssetFields.ASSIGNED_TO)){
			query += andEq(AssetFields.ASSIGNED_TO + ".uuid", iFilters.get(AssetFields.ASSIGNED_TO));
		}

		if (iFilters.isNotNull(AssetFields.ASSIGNED_TO_PROFILE)){
			query += andEq(AssetFields.ASSIGNED_TO_PROFILE + ".uuid", iFilters.get(AssetFields.ASSIGNED_TO_PROFILE));
		}

		if (iFilters.isNotNull(AssetFields.NAME)){
			query += andEq(AssetFields.NAME, iFilters.get(AssetFields.NAME));
		}

		if (iFilters.isNotNull(AssetFields.SERIAL_NUMBER)){
			query += andEq(AssetFields.SERIAL_NUMBER, iFilters.get(AssetFields.SERIAL_NUMBER));
		}

		if (iFilters.isNotNull(AssetFields.TYPE)){
			query += andEq(AssetFields.TYPE, iFilters.get(AssetFields.TYPE));
		}

		if (iFilters.isNotNull(AssetFields.STATUS)){
			query += andEq(AssetFields.STATUS, iFilters.get(AssetFields.STATUS));
		}

		if (iFilters.isNotNull(AssetFields.EXTERNAL_ID)){
			query += andEq(AssetFields.EXTERNAL_ID, iFilters.get(AssetFields.EXTERNAL_ID));
		}

		if (iFilters.isNotNull(AssetFields.DATA_SOURCE)){
			query += andEq(AssetFields.DATA_SOURCE, iFilters.get(AssetFields.DATA_SOURCE));
		}

		if (iFilters.isNotNull(AssetFields.ASSET_SCHEMA_UUID)){
			query += andEq(AssetFields.ASSET_SCHEMA_UUID, iFilters.get(AssetFields.ASSET_SCHEMA_UUID));
		}

		if (!iFilters.isNullOrEmpty(RestParameters.KEYWORDS)){
			String keywordSearch = contains(AssetFields.NAME + ".toLowerCase()", iFilters.get(RestParameters.KEYWORDS).toString().toLowerCase());
			keywordSearch += or(contains(AssetFields.DESCRIPTION + ".toLowerCase()", iFilters.get(RestParameters.KEYWORDS).toString().toLowerCase()));
			keywordSearch += or(contains(AssetFields.SERIAL_NUMBER + ".toLowerCase()", iFilters.get(RestParameters.KEYWORDS).toString().toLowerCase()));
			query += and("(" + keywordSearch + ")");
		}

		if (iFilters.isNotNull(AssetFields.TAGS)){
			//query += and("tags traverse (value.toLowerCase() = '"+ iFilters.get(ContactQFilters.TAGS).toString().toLowerCase() + "')");
			query += andIn(AssetFields.TAGS, iFilters.get(AssetFields.TAGS).toString().toLowerCase());
		}

		return query;
	}

}
