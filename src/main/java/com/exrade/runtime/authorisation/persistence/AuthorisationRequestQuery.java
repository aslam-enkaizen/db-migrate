package com.exrade.runtime.authorisation.persistence;

import com.exrade.models.authorisation.AuthorisationObjectType;
import com.exrade.models.authorisation.AuthorisationRequest;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.PlainSql;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.AuthorisationFields;
import com.exrade.runtime.rest.RestParameters.AuthorisationFilters;
import com.exrade.util.ContextHelper;

public class AuthorisationRequestQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + AuthorisationRequest.class.getSimpleName()+ " where 1 = 1 ";

		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}

		if (iFilters.isNotNull(AuthorisationFields.STATUS)){
			query += andEq(AuthorisationFields.STATUS, iFilters.get(AuthorisationFields.STATUS));
		}

		if (iFilters.isNotNull(AuthorisationFields.OBJECTTYPE)){
			query += andEq(AuthorisationFields.OBJECTTYPE, iFilters.get(AuthorisationFields.OBJECTTYPE));
		}

		if (iFilters.isNotNull(AuthorisationFields.OBJECTID)){
			query += andEq(AuthorisationFields.OBJECTID, iFilters.get(AuthorisationFields.OBJECTID));
		}

		if (iFilters.isNotNull(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID)){
			String objectTypeNegotiationQuery = eq(AuthorisationFields.OBJECTTYPE, AuthorisationObjectType.NEGOTIATION) + andEq(AuthorisationFields.OBJECTID, iFilters.get(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID));
			String extraContextNegotiationQuery = eq(AuthorisationFields.EXTRA_CONTEXT + "." + AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID, iFilters.get(AuthorisationFields.EXTRA_CONTEXT_NEGOTIAION_UUID));

			query += and(" ( (" + objectTypeNegotiationQuery + " ) "  + or(extraContextNegotiationQuery) + " ) ");
		}

		if (iFilters.isNotNull(AuthorisationFilters.REQUEST_TYPE)){
			if(AuthorisationFilters.SENT.equals(iFilters.get(AuthorisationFilters.REQUEST_TYPE))){
				if (iFilters.isNotNull(AuthorisationFilters.SENDER_UUID)) {
					if(iFilters.get(AuthorisationFilters.SENDER_UUID).equals(ContextHelper.getMembershipUUID()))
						query += andEq(AuthorisationFields.SENDER + ".uuid", iFilters.get(AuthorisationFilters.SENDER_UUID));
					else
						query += and( " (" + eq(AuthorisationFields.SENDER + ".uuid", iFilters.get(AuthorisationFilters.SENDER_UUID)) + " and (" + AuthorisationFields.RECEIVERS + " contains (uuid = '" + ContextHelper.getMembershipUUID() + "')))");
				}
				else
					query += andEq(AuthorisationFields.SENDER, PlainSql.get(ContextHelper.getMembership().getId()));
			}
			else if(AuthorisationFilters.RECEIVED.equals(iFilters.get(AuthorisationFilters.REQUEST_TYPE))){
				query += and(" (" + AuthorisationFields.RECEIVERS + " contains (uuid = '" + ContextHelper.getMembershipUUID() + "'))"); //andTraverse(AuthorisationFields.RECEIVERS, "uuid", ContextHelper.getMembershipUUID());
			}
			else{
				query += and( " (" + eq(AuthorisationFields.SENDER, PlainSql.get(ContextHelper.getMembership().getId())) + " or (" + AuthorisationFields.RECEIVERS + " contains (uuid = '" + ContextHelper.getMembershipUUID() + "')))");
			}
		}

		if (iFilters.isNotNull(AuthorisationFilters.CREATED_AFTER_INCLUSIVE)){
			query += and(condition(RestParameters.CREATION_DATE, iFilters.get(AuthorisationFilters.CREATED_AFTER_INCLUSIVE),Operator.GTEQ));
		}

		return query;
	}
}
