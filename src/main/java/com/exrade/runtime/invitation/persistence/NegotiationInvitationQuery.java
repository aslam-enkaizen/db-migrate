package com.exrade.runtime.invitation.persistence;

import com.exrade.models.invitations.NegotiationInvitation;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.NegotiationInvitationFields;
import com.exrade.runtime.rest.RestParameters.NegotiationInvitationFilters;

public class NegotiationInvitationQuery extends InvitationQuery {

	@Override
	public String buildQuery(QueryFilters filters) {
		String query = "select from " + NegotiationInvitation.class.getSimpleName() + " where 1 = 1 ";

		query += super.buildQuery(filters);
		
		query += addEqFilter(filters,NegotiationInvitationFields.INVITED_MEMBERSHIP);
		query += addEqFilter(filters,NegotiationInvitationFilters.INVITED_MEMBERSHIP_UUID);
		query += addEqFilter(filters,NegotiationInvitationFields.INVITED_NEGOTIATION);
		query += addEqFilter(filters,NegotiationInvitationFilters.INVITED_NEGOTIATION_UUID);

		if (filters.isNotNull(NegotiationInvitationFilters.INVITATION_INBOX) && getActor() != null){
			if (InvitationQuery.SENT.equals(filters.get(NegotiationInvitationFilters.INVITATION_INBOX))){
				query += andNotIn(NegotiationInvitationFields.INVITED_MEMBERSHIP, getActor().getId());
			}
			else if (InvitationQuery.INCOMING.equals(filters.get(NegotiationInvitationFilters.INVITATION_INBOX))){
				query += andEq(NegotiationInvitationFilters.INVITED_MEMBERSHIP_UUID, getActor().getIdentifier());
			}
		}
		
		return query;
	}
}
