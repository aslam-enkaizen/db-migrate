package com.exrade.runtime.invitation.persistence;

import com.exrade.models.invitations.AbstractInvitation;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public abstract class InvitationPersistence extends PersistentManager {

	public abstract OrientSqlBuilder getQuery();
	
	public <T extends AbstractInvitation> List<T> list(QueryFilters iQueryFilters) {
		return listObjects(getQuery(), iQueryFilters);
	}

	public <T extends AbstractInvitation> T read(QueryFilters iQueryFilters) {
		return readObject(getQuery(), iQueryFilters);
	}
	
}
