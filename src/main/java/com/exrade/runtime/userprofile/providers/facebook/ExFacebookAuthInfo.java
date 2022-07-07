package com.exrade.runtime.userprofile.providers.facebook;

import com.exrade.models.userprofile.ExAuthRequest;
import com.exrade.providers.oauth2.OAuth2AuthInfo;

import java.util.Date;

public class ExFacebookAuthInfo extends OAuth2AuthInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	public ExFacebookAuthInfo(final JsonNode node) {
//		super(node.get(Constants.ACCESS_TOKEN) != null ? node.get(Constants.ACCESS_TOKEN).asText() : null,
//				node.get(Constants.EXPIRES_IN) != null ? new Date().getTime() + node.get(Constants.EXPIRES_IN).asLong() * 1000 : -1);
//	}
//
	public ExFacebookAuthInfo(final ExAuthRequest node) {
		super(node.getAccessToken(),
				node.getExpiresIn() != null ? new Date().getTime() + node.getExpiresIn() * 1000 : -1);
	}
}
