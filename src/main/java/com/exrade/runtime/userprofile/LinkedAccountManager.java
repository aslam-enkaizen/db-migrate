package com.exrade.runtime.userprofile;

import com.exrade.models.userprofile.LinkedAccount;
import com.exrade.models.userprofile.User;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.IQuery;
import com.exrade.platform.persistence.query.OrientSqlBuilder.Operator;
import com.exrade.platform.persistence.query.PlainSql;
import com.exrade.platform.persistence.query.SimpleOQuery;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.UserFields;

import java.util.List;

public class LinkedAccountManager {

	
	private PersistentManager persistentManager;

	public LinkedAccountManager() {
		this(new PersistentManager());
	}
	
	public LinkedAccountManager(PersistentManager iPersistentManager) {
		persistentManager = iPersistentManager;
	}
	
	public LinkedAccount getAccountByProvider(final User user, String iProvider){
		IQuery query = new SimpleOQuery<>(User.class).select("flatten(linkedAccounts)").
				eq(RestParameters.UUID,user.getUuid()).
				filter(UserFields.LINKED_ACCOUNTS,PlainSql.get("(providerKey = 'password')"),Operator.CONTAINS).getQuery();
		
		return persistentManager.readObject(query);
	}
	
	public static LinkedAccount getLinkedAccount(List<LinkedAccount> linkedAccounts,String iProviderKey){
		if (iProviderKey == null) return null;
		LinkedAccount accountToSelect = null;
		for (LinkedAccount linkedAccount : linkedAccounts) {
			if (iProviderKey.equals(linkedAccount.getProviderKey())){
				accountToSelect = linkedAccount;
			}
		}
		return accountToSelect;
	}
}
