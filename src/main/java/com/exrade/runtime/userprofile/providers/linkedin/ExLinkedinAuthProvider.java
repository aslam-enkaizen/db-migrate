package com.exrade.runtime.userprofile.providers.linkedin;

import com.exrade.core.ExLogger;
import com.exrade.models.userprofile.ExAuthRequest;
import com.exrade.platform.exception.ExAuthenticationException;
import com.exrade.runtime.userprofile.providers.ExOAuthAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;



public class ExLinkedinAuthProvider extends ExOAuthAuthenticator<ExLinkedinAuthUser,ExLinkedinAuthInfo> {

	static final String PROVIDER_KEY = "linkedin";
	
	public static abstract class Constants {
		private static final String USER_INFO_URL_SETTING_KEY = "userInfoUrl";
		private static final String USER_EMAIL_URL_KEY = "userEmailUrl";
		private static final String OAUTH2_ACCESS_TOKEN = "oauth2_access_token";
		private static final String ERROR_CODE = "serviceErrorCode";
		private static final String ERROR_MESSAGE = "message";
	}
	
	@Override
	protected ExLinkedinAuthUser transform(ExLinkedinAuthInfo info) {
		final String profileApiurl = getConfiguration(Constants.USER_INFO_URL_SETTING_KEY);
		final String emailApiUrl = getConfiguration(Constants.USER_EMAIL_URL_KEY);
		
		Map<String,String> queryParameters = new HashMap<>();
		queryParameters.put(Constants.OAUTH2_ACCESS_TOKEN,
				info.getAccessToken());
		
		final JsonNode profileResult = doRestGET(profileApiurl,queryParameters);
		if (profileResult.get(Constants.ERROR_CODE) != null) {
			throw new ExAuthenticationException(profileResult.get(
					Constants.ERROR_MESSAGE).asText());
		}
		
		final JsonNode emailResult = doRestGET(emailApiUrl,queryParameters);
		if (emailResult.get(Constants.ERROR_CODE) != null) {
			throw new ExAuthenticationException(emailResult.get(
					Constants.ERROR_MESSAGE).asText());
		}
		
		ExLogger.get().debug("Data from Linkedin: {}, {}",profileResult.toString(), emailResult.toString());
		return new ExLinkedinAuthUser(profileResult, emailResult, info);
	}

	@Override
	public String getKey() {
		return PROVIDER_KEY;
	}

	@Override
	protected ExLinkedinAuthInfo buildInfo(ExAuthRequest authRequest) {
		return new ExLinkedinAuthInfo(authRequest);
	}

}
