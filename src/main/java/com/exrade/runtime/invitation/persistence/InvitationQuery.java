package com.exrade.runtime.invitation.persistence;

import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.InvitationFields;

public abstract class InvitationQuery extends OrientSqlBuilder {

	public static final String SENT = "sent";
	public static final String INCOMING = "incoming";

	@Override
	public String buildQuery(QueryFilters filters) {

		String query = "";

		if (filters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, filters.get(QueryParameters.UUID));
		}

		if (filters.isNotNull(InvitationFields.INVITED_EMAIL)){
			query += andEq(InvitationFields.INVITED_EMAIL, filters.get(InvitationFields.INVITED_EMAIL));
		}

		if (filters.isNotNull(InvitationFields.INVITATION_STATUS)){
			query += andEq(InvitationFields.INVITATION_STATUS, filters.get(InvitationFields.INVITATION_STATUS));
		}

		if (filters.isNotNull(InvitationQFilters.CREATED_AFTER_INCLUSIVE)){
			query += and(condition(RestParameters.CREATION_DATE, filters.get(InvitationQFilters.CREATED_AFTER_INCLUSIVE),Operator.GTEQ));
		}

		return query;
	}

	public static final class InvitationQFilters {
		public static final String CREATED_AFTER_INCLUSIVE = "createdAfterInclusive";
	}
}
