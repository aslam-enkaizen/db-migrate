package com.exrade.runtime.security;

import com.exrade.models.Role;
import com.exrade.models.userprofile.security.ExRole;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.IQuery;
import com.exrade.platform.persistence.query.OrientSqlBuilder.Operator;
import com.exrade.platform.persistence.query.SimpleOQuery;

public class RoleManager {

	private PersistentManager persistentManager;
	
	public RoleManager() {
		this(new PersistentManager());
	}
	
	public RoleManager(PersistentManager iPersistentManager) {
		persistentManager = iPersistentManager;
	}
	
	public void create(ExRole role) {
		persistentManager.create(role);
	}
	
	public Role findByName(String iName){
		IQuery query = new SimpleOQuery<>(ExRole.class).filter("name", iName, Operator.LIKE).getQuery();
		return persistentManager.readObject(query);
	}
	
}
