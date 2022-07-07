package com.exrade.runtime.invitation.persistence;

import com.exrade.models.invitations.MemberInvitation;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.MemberInvitationFields;
import com.exrade.runtime.rest.RestParameters.MemberInvitationFilters;

public class MemberInvitationQuery extends InvitationQuery {

	@Override
	public String buildQuery(QueryFilters filters) {
		String query = "select from " + MemberInvitation.class.getSimpleName() + " where 1 = 1 ";

		query += super.buildQuery(filters);
		
		query += addEqFilter(filters,MemberInvitationFields.INVITED_USER);
		query += addEqFilter(filters,MemberInvitationFilters.INVITED_USER_UUID);
		query += addEqFilter(filters,MemberInvitationFields.INVITED_PROFILE);
		query += addEqFilter(filters,MemberInvitationFilters.INVITED_PROFILE_UUID);
		query += addEqFilter(filters,MemberInvitationFields.ROLENAME);
		
		if (filters.isNotNull(MemberInvitationFilters.INVITATION_INBOX) && getActor() != null){
			if (InvitationQuery.SENT.equals(filters.get(MemberInvitationFilters.INVITATION_INBOX))){
				query += andNotIn(MemberInvitationFields.INVITED_USER, getActor().getUser().getId());
			}
			else if (InvitationQuery.INCOMING.equals(filters.get(MemberInvitationFilters.INVITATION_INBOX))){
				query += andEq(MemberInvitationFilters.INVITED_USER_UUID, getActor().getUser().getUuid());
			}
		}

		return query;
	}
}
