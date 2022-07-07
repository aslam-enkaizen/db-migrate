package com.exrade.runtime.invitation.persistence;

import com.exrade.platform.persistence.query.OrientSqlBuilder;

public class MemberInvitationPersistence extends InvitationPersistence {

	public final static OrientSqlBuilder INVITATION_QUERY = new MemberInvitationQuery();

	@Override
	public OrientSqlBuilder getQuery() {
		return INVITATION_QUERY;
	}

}
