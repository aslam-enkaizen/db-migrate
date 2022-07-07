package com.exrade.runtime.userprofile.providers;

import com.exrade.models.userprofile.*;
import com.exrade.models.userprofile.security.AccountStatus;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExParamException;
import com.exrade.providers.oauth2.OAuth2AuthInfo;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.userprofile.AccountManager;
import com.exrade.runtime.userprofile.IAccountManager;
import com.exrade.runtime.userprofile.TraktiJwtManager;
import com.exrade.util.RESTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;

import java.util.Map;

public abstract class ExOAuthAuthenticator<U extends ExOAuth2AuthUser,I extends OAuth2AuthInfo> {
	protected IAccountManager accountManager = new AccountManager();

	protected User getUser(U auth2User){
		return accountManager.findByUsername(auth2User.getEmail());
	}

	protected User createUser(U auth2User){
		return accountManager.create(auth2User,AccountStatus.ACTIVE);
	}

	protected User convertFromGuest(U auth2User){
		return accountManager.convertFromGuest(auth2User,AccountStatus.ACTIVE);
	}

	public abstract String getKey();

	public String getConfiguration(String keyConf){
		return ExConfiguration.getStringProperty("play-authenticate."+getKey()+"."+keyConf);
	}

	public ExAuthResponse authenticate(ExAuthRequest authRequest){
		final I info = buildInfo(authRequest);

		final U auth2User = transform(info);

		if (!Strings.isNullOrEmpty(authRequest.getTimeZone())){
			auth2User.setTimezone(authRequest.getTimeZone());
		}

		User user = getUser(auth2User);

		if (user==null){ // A new user is created
			user = createUser(auth2User);
		}
		else if(user.isGuest()) {
			user = convertFromGuest(auth2User);
    	}
		else if (user.isDisabled()){//
			throw new ExParamException(ErrorKeys.USER_NOT_ACTIVE);
		}

		boolean providerMatched = false;
		for (LinkedAccount linkedAccount  : user.getLinkedAccounts()) {
			if (linkedAccount.getProviderKey().equals(authRequest.getAuthProvider().name().toLowerCase())){
				providerMatched = true;
				linkedAccount.setProviderUserId(authRequest.getAccessToken());
				user.setLastLogin(TimeProvider.now());
				accountManager.updateAccount(user);

				break;
			}
		}

		if (!providerMatched)
			throw new ExParamException(ErrorKeys.USER_NAME_EXISTS, "email");

		Membership membership = user.getCurrentMembership();
		ExAuthResponse authResponse = new ExAuthResponse();
		authResponse.setMembership(membership);
        authResponse.setAccessToken(TraktiJwtManager.getInstance().generateToken(membership.getUuid()));
		return authResponse;
	}

	public JsonNode doRestGET(final String url, Map<String,String> queryParameters) {
		return RESTUtil.doRestGET(url, null, queryParameters);
	}

	protected abstract I buildInfo(final ExAuthRequest authRequest);

	protected abstract U transform(I info);
}
