package com.exrade.runtime.kyc.persistence;

import com.exrade.models.kyc.Kyc;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.KycFields;
import com.exrade.runtime.rest.RestParameters.KycFilters;

public class KycQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + Kyc.class.getSimpleName() + " where 1 = 1 ";
		
		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		
		if (iFilters.isNotNull(KycFields.SERVICE_CALL_REFERENCE)){
			query += andEq(KycFields.SERVICE_CALL_REFERENCE, iFilters.get(KycFields.SERVICE_CALL_REFERENCE));
		}
		
		if (iFilters.isNotNull(KycFilters.OFFER_UUID)){
			query += andEq("offer.uuid", iFilters.get(KycFilters.OFFER_UUID));
		}
		
		return query;
	}

}
