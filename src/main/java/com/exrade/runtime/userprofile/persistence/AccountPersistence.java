package com.exrade.runtime.userprofile.persistence;

import com.exrade.models.userprofile.User;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.IQuery;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.UserFields;
import com.exrade.runtime.userprofile.persistence.query.AccountQuery;


/**
 * Persistent manager for User Profile entities
 * @author carlopolisini
 *
 */
public class AccountPersistence extends PersistentManager {

	/**
	 * Search an Actor entity by username
	 * @param username
	 * @return Actor, null otherwise
	 */
	public User findByUsername(String iUsername) {
		QueryFilters filters = QueryFilters.create(UserFields.USER_NAME,iUsername);
		IQuery nquery = new AccountQuery().createQuery(filters);
		User user = readObject(nquery);
		return user;
	}
	
	/**
	 * Search an Actor entity by UUID
	 * @param UUID
	 * @return Actor, null otherwise
	 */
	public User findByUUID(String iUUID) {
		QueryFilters filters = QueryFilters.create(QueryParameters.UUID,iUUID);
		IQuery nquery = new AccountQuery().createQuery(filters);
		User user = readObject(nquery);
		return user;
	}

}
