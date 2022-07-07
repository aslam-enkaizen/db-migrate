package com.exrade.runtime.userprofile;

import com.exrade.models.userprofile.TokenAction;
import com.exrade.models.userprofile.TokenAction.Type;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.AccountStatus;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.IQuery;
import com.exrade.platform.persistence.query.SimpleOQuery;
import com.exrade.runtime.rest.RestParameters.TokenActionFields;

public class TokenActionManager {

	private PersistentManager persistentManager;

	public TokenActionManager() {
		this(new PersistentManager());
	}
	
	public TokenActionManager(PersistentManager iPersistentManager) {
		persistentManager = iPersistentManager;
	}
	
	/**
	 * Verify this 	
	 * @param userUnverified
	 */
	public void verify(final User userUnverified) {
		// TODO wrap this into a transaction
		deleteByUser(userUnverified, Type.EMAIL_VERIFICATION);
		userUnverified.setAccountStatus(AccountStatus.ACTIVE);
		persistentManager.update(userUnverified);
	}
	
	public void deleteByUser(final User u, final Type type) {
		IQuery query = new SimpleOQuery<>(TokenAction.class).eq(TokenActionFields.TARGET_USER+".uuid",u.getUuid()).
				eq(TokenActionFields.TYPE,type).getQuery();
		TokenAction tokenAction = persistentManager.readObject(query);
		persistentManager.delete(tokenAction);
	}
	
	public void create(final Type type, final String token,final User targetUser) {
		TokenAction ta = TokenAction.create(type, token, targetUser);
		persistentManager.create(ta);
	}

	public TokenAction findByToken(String iToken,String iType) {
		TokenAction tokenAction = null;
		if (iToken != null){
			IQuery query = new SimpleOQuery<>(TokenAction.class).eq(TokenActionFields.TOKEN,iToken).eq(TokenActionFields.TYPE,iType).getQuery();
			tokenAction = persistentManager.readObject(query);
		}
		return tokenAction;
	}

}
