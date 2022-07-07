package com.exrade.runtime.asset.persistence;

import com.exrade.models.asset.AssetSchema;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.AssetSchemaFields;
import com.exrade.runtime.rest.RestParameters.AssetSchemaFilters;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class AssetSchemaQuery  extends OrientSqlBuilder{

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from "+ AssetSchema.class.getSimpleName() + " where 1 = 1 ";

		if (iFilters.isNotNull(AssetSchemaFields.NAME)){
			nquery += andEq(AssetSchemaFields.NAME, iFilters.get(AssetSchemaFields.NAME));
		}

		if (iFilters.isNotNull(QueryParameters.UUID)){
			nquery += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}

		if (iFilters.containsKey(AssetSchemaFields.TAGS) && iFilters.get(AssetSchemaFields.TAGS) != null){
			nquery += andIn(AssetSchemaFields.TAGS, iFilters.get(AssetSchemaFields.TAGS).toString().toLowerCase());
		}

		if (iFilters.isNotNull(AssetSchemaFields.AUTHOR_MEMBERSHIP_UUID)){
			nquery += andEq("authorMembership.uuid", iFilters.get(AssetSchemaFields.AUTHOR_MEMBERSHIP_UUID));
		}

		if (iFilters.isNotNull(AssetSchemaFilters.PROFILE)){
			nquery += andEq("authorMembership.profile.uuid", iFilters.get(AssetSchemaFilters.PROFILE));
		}

		if (!iFilters.containsKey(QueryParameters.UUID)){
			if (!iFilters.containsKey(AssetSchemaFilters.INCLUDE_INACTIVE) || !iFilters.isTrue(AssetSchemaFilters.INCLUDE_INACTIVE)){
				nquery += andEq(AssetSchemaFields.ACTIVE, true);
			}
		}

		if (!iFilters.isNullOrEmpty(RestParameters.KEYWORDS)){
			List<String> keywords = Lists.newArrayList(Splitter.on(" ").trimResults()
				       .omitEmptyStrings().split((String) iFilters.get(RestParameters.KEYWORDS)));
			for (String keyword : keywords) {
				nquery += and(contains(QueryKeywords.ANY + ".toLowerCase()", keyword.toLowerCase()));
			}
		}
		return nquery;
	}


}
