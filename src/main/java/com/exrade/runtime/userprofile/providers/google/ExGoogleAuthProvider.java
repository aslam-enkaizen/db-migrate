package com.exrade.runtime.userprofile.providers.google;

import com.exrade.models.userprofile.ExAuthRequest;
import com.exrade.platform.exception.ExAuthenticationException;
import com.exrade.runtime.userprofile.providers.ExOAuthAuthenticator;
import com.exrade.util.Logger;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;



public class ExGoogleAuthProvider extends ExOAuthAuthenticator<ExGoogleAuthUser,ExGoogleAuthInfo> {

	static final String PROVIDER_KEY = "google";
	
	public static abstract class Constants {
		private static final String USER_INFO_URL_SETTING_KEY = "userInfoUrl";
		private static final String ERROR = "error";
		private static final String ERROR_MESSAGE = "message";
	}
	
	@Override
	protected ExGoogleAuthUser transform(ExGoogleAuthInfo info) {
		final String urlUserInfo = getConfiguration(Constants.USER_INFO_URL_SETTING_KEY);

		Map<String,String> queryParameters = new HashMap<>();
//		queryParameters.put(OAuth2AuthProvider.Constants.ACCESS_TOKEN,
//				info.getAccessToken());
		
		final JsonNode userInfo = doRestGET(urlUserInfo,queryParameters);
		
		if (userInfo.get(Constants.ERROR) != null) {
			Logger.error("Data from google: {}", userInfo.toString());
			throw new ExAuthenticationException(userInfo.get(
					Constants.ERROR).get(Constants.ERROR_MESSAGE).asText());
		}
		
	
		Logger.debug("Data from google: {}",userInfo.toString());
		return new ExGoogleAuthUser(userInfo, info);
	}

	@Override
	public String getKey() {
		return PROVIDER_KEY;
	}

	@Override
	protected ExGoogleAuthInfo buildInfo(ExAuthRequest authRequest) {
		return new ExGoogleAuthInfo(authRequest);
	}

}
