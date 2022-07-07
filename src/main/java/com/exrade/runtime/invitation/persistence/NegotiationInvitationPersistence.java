package com.exrade.runtime.invitation.persistence;

import com.exrade.platform.persistence.query.OrientSqlBuilder;

public class NegotiationInvitationPersistence extends InvitationPersistence {

	public final static OrientSqlBuilder INVITATION_QUERY = new NegotiationInvitationQuery();

	@Override
	public OrientSqlBuilder getQuery() {
		return INVITATION_QUERY;
	}  
	
}
