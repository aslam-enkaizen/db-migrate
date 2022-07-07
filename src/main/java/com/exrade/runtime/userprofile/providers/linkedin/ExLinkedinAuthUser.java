package com.exrade.runtime.userprofile.providers.linkedin;

import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthenticationException;
import com.exrade.runtime.userprofile.providers.ExOAuth2AuthUser;
import com.exrade.user.ProfiledIdentity;
import com.fasterxml.jackson.databind.JsonNode;

public class ExLinkedinAuthUser extends ExOAuth2AuthUser implements
		ProfiledIdentity {


	private static final long serialVersionUID = 1L;
	
	private String industry;
	
	public ExLinkedinAuthUser(final JsonNode nodeInfo, final JsonNode nodeEmail, ExLinkedinAuthInfo info) {
		super(info.getAccessToken(), info, null);

		if (nodeEmail.findPath(Constants.EMAIL).isMissingNode()) {
			throw new ExAuthenticationException(ErrorKeys.AUTHENTICATOR_OAUTH2_EMPTYEMAIL);
		}
		
		this.email = nodeEmail.findPath(Constants.EMAIL).asText();
		
		if (nodeInfo.has(Constants.FIRST_NAME)) {
			this.firstName = nodeInfo.get(Constants.FIRST_NAME).asText();
		}
		if (nodeInfo.has(Constants.LAST_NAME)) {
			this.lastName = nodeInfo.get(Constants.LAST_NAME).asText();
		}
		
	}

	private static abstract class Constants {

		public static final String FIRST_NAME = "localizedFirstName";
		public static final String LAST_NAME = "localizedLastName";
		public static final String EMAIL = "emailAddress";
	}

	@Override
	public String getProvider() {
		return ExLinkedinAuthProvider.PROVIDER_KEY;
	}

	public String getIndustry() {
		return industry;
	}

}
