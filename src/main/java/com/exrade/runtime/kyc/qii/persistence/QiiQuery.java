package com.exrade.runtime.kyc.qii.persistence;

import com.exrade.models.kyc.qii.QiiData;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.QiiDataFields;
import com.exrade.runtime.rest.RestParameters.QiiDataFilters;

public class QiiQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + QiiData.class.getSimpleName()+ " where 1 = 1 ";

		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}

		if (iFilters.isNotNull(QiiDataFields.REQUEST_MANAGEMENT_LINK_NAME)){
			query += andEq(QiiDataFields.REQUEST_MANAGEMENT_LINK_NAME, iFilters.get(QiiDataFields.REQUEST_MANAGEMENT_LINK_NAME));
		}

		if (iFilters.isNotNull(QiiDataFields.REQUEST_MANAGEMENT_ID)){
			query += andEq(QiiDataFields.REQUEST_MANAGEMENT_ID, iFilters.get(QiiDataFields.REQUEST_MANAGEMENT_ID));
		}

		if (iFilters.isNotNull(QiiDataFields.REQUEST_MANAGEMENT_NAME)){
			query += andEq(QiiDataFields.REQUEST_MANAGEMENT_NAME, iFilters.get(QiiDataFields.REQUEST_MANAGEMENT_NAME));
		}

		if (iFilters.isNotNull(QiiDataFilters.MEMBERSHIP_UUID)){
			query += andEq(String.format("%s.%s", QiiDataFields.MEMBERSHIP, RestParameters.UUID), iFilters.get(QiiDataFilters.MEMBERSHIP_UUID));
		}

		if (iFilters.isNotNull(QiiDataFilters.NEGOTIATION_UUID)){
			query += andEq(String.format("%s.%s", QiiDataFields.NEGOTIATION, RestParameters.UUID), iFilters.get(QiiDataFilters.NEGOTIATION_UUID));
		}

		return query;
	}

}
