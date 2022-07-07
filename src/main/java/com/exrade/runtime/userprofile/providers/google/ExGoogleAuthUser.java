package com.exrade.runtime.userprofile.providers.google;

import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthenticationException;
import com.exrade.runtime.userprofile.providers.ExOAuth2AuthUser;
import com.exrade.user.ProfiledIdentity;
import com.fasterxml.jackson.databind.JsonNode;

public class ExGoogleAuthUser extends ExOAuth2AuthUser implements ProfiledIdentity {


	private static final long serialVersionUID = 1L;
	

	
	public ExGoogleAuthUser(final JsonNode nodeInfo, ExGoogleAuthInfo info) {
		super(info.getAccessToken(), info, null);

		if (!nodeInfo.has(Constants.EMAIL)) {
			throw new ExAuthenticationException(ErrorKeys.AUTHENTICATOR_OAUTH2_EMPTYEMAIL);
		}
		
		this.email = nodeInfo.get(Constants.EMAIL).asText();
		
		if (nodeInfo.has(Constants.FIRST_NAME)) {
			this.firstName = nodeInfo.get(Constants.FIRST_NAME).asText();
		}
		if (nodeInfo.has(Constants.LAST_NAME)) {
			this.lastName = nodeInfo.get(Constants.LAST_NAME).asText();
		}
		if (nodeInfo.has(Constants.PROFILE_IMAGE_URL)) {
			this.picture = nodeInfo.get(Constants.PROFILE_IMAGE_URL).asText();
		}
		if (nodeInfo.has(Constants.PROFILE_LINK)) {
			this.profileLink = nodeInfo.get(Constants.PROFILE_LINK)
					.asText();
		}		
		if (nodeInfo.has(Constants.LOCALE)) {
			this.language = nodeInfo.get(Constants.LOCALE).asText();
		}
	}

	private static abstract class Constants {

		public static final String ID = "id";
		public static final String FIRST_NAME = "given_name";
		public static final String LAST_NAME = "family_name";
		public static final String EMAIL = "email";
		public static final String PROFILE_IMAGE_URL = "picture";
		public static final String PROFILE_LINK = "link";
		public static final String LOCALE = "locale";
		public static final String GENDER = "gender";
	}

	@Override
	public String getProvider() {
		return ExGoogleAuthProvider.PROVIDER_KEY;
	}

}
