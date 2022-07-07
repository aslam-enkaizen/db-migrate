package com.exrade.runtime.userprofile.providers.facebook;

import com.exrade.models.userprofile.ExAuthRequest;
import com.exrade.platform.exception.ExAuthenticationException;
import com.exrade.runtime.userprofile.providers.ExOAuthAuthenticator;
import com.exrade.util.Logger;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;



public class ExFacebookAuthProvider extends ExOAuthAuthenticator<ExFacebookAuthUser,ExFacebookAuthInfo> {

	static final String PROVIDER_KEY = "facebook";
	private static final String FIELDS = "fields";
	
	public static abstract class Constants {
		private static final String USER_INFO_URL_SETTING_KEY = "userInfoUrl";
		private static final String USER_FRIENDS_URL_SETTING_KEY = "userFriendsUrl";
		private static final String USER_PICTURE_URL_SETTING_KEY = "userPictureUrl";
		private static final String OAUTH2_ACCESS_TOKEN = "oauth2_access_token";
		private static final String ERROR_CODE = "errorCode";
		private static final String ERROR_MESSAGE = "message";
		private static final String USER_INFO_FIELDS_SETTING_KEY = "userInfoFields";
	}
	
	@Override
	protected ExFacebookAuthUser transform(ExFacebookAuthInfo info) {
		final String urlUserInfo = getConfiguration(Constants.USER_INFO_URL_SETTING_KEY);
		final String urlFriends = getConfiguration(Constants.USER_FRIENDS_URL_SETTING_KEY);
		final String fields = getConfiguration(Constants.USER_INFO_FIELDS_SETTING_KEY);

		Map<String,String> queryParameters = new HashMap<>();
//		queryParameters.put(OAuth2AuthProvider.Constants.ACCESS_TOKEN,
//				info.getAccessToken());
		queryParameters.put(FIELDS, fields);
		
		final JsonNode userInfo = doRestGET(urlUserInfo,queryParameters);
		
		if (userInfo.get(Constants.ERROR_CODE) != null) {
			throw new ExAuthenticationException(userInfo.get(
					Constants.ERROR_MESSAGE).asText());
		}
		
	
		Logger.debug("Data from facebook: {}",userInfo.toString());
		return new ExFacebookAuthUser(userInfo, info);
	}

	@Override
	public String getKey() {
		return PROVIDER_KEY;
	}

	@Override
	protected ExFacebookAuthInfo buildInfo(ExAuthRequest authRequest) {
		return new ExFacebookAuthInfo(authRequest);
	}

}
