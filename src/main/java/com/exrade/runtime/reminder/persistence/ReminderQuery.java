package com.exrade.runtime.reminder.persistence;

import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.userprofile.security.NegotiationRole;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.negotiation.persistence.NegotiationQuery.NegotiationQFilters;
import com.exrade.runtime.reminder.persistence.ReminderPersistentManager.ReminderQFilters;
import com.exrade.runtime.rest.RestParameters.NegotiationFields;

import java.util.Date;

public class ReminderQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {

		Date lowerDate = (Date) iFilters.get(ReminderQFilters.FROM);
		Date upperDate = (Date) iFilters.get(ReminderQFilters.TO);

		String fields = "owner,negotiators,uuid,title,startDate,endDate,publicationDate,ownerTimeEvents,participantTimeEvents";

		String query = "select " + fields + " from "
				+ Negotiation.class.getSimpleName() + " where 1 = 1 ";

		if (lowerDate != null) {
			query += and(condition(NegotiationFields.END_DATE, lowerDate,
					Operator.GTEQ));
		}

		if (upperDate != null) {
			query += and(condition(NegotiationFields.PUBLICATION_DATE,
					upperDate, Operator.LT));
		}

		query += and("("+ getNegotiatorRoleQuery(getActor().getIdentifier(), NegotiationRole.PARTICIPANT)
					+ or(getNegotiatorRoleQuery(getActor().getIdentifier(), NegotiationRole.OWNER)) + ")");

		return query;
	}

	
	protected String getNegotiatorRoleQuery(String negotiatorUUID, String negotiatorRole){
		String roleQuery = "";
		if(NegotiationRole.OWNER.equals(negotiatorRole)){
			roleQuery = " ( " +  eq(NegotiationQFilters.OWNER_UUID, negotiatorUUID) +
					or( " negotiators contains (" + eq("membership.uuid", negotiatorUUID) + and(" exRoles contains (") + eq("name", negotiatorRole) + "))") + " ) ";
		}
		else if(NegotiationRole.PARTICIPANT.equals(negotiatorRole)){
			roleQuery = " ( " +  eq(NegotiationFields.PARTICIPANTS+"[uuid]", negotiatorUUID) +
					or( " negotiators contains (" + eq("membership.uuid", negotiatorUUID) + and(" exRoles contains (") + eq("name", negotiatorRole) + "))") + " ) ";
		}
		return roleQuery;
	}
}
