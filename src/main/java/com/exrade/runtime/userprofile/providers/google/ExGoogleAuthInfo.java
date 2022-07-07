package com.exrade.runtime.userprofile.providers.google;

import com.exrade.models.userprofile.ExAuthRequest;
import com.exrade.providers.oauth2.OAuth2AuthInfo;

import java.util.Date;

public class ExGoogleAuthInfo extends OAuth2AuthInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	public ExGoogleAuthInfo(final JsonNode node) {
//		super(node.get(Constants.ACCESS_TOKEN) != null ? node.get(Constants.ACCESS_TOKEN).asText() : null,
//				node.get(Constants.EXPIRES_IN) != null ? new Date().getTime() + node.get(Constants.EXPIRES_IN).asLong() * 1000 : -1);
//	}
	
	public ExGoogleAuthInfo(final ExAuthRequest node) {
		super(node.getAccessToken(),
				node.getExpiresIn() != null ? new Date().getTime() + node.getExpiresIn() * 1000 : -1);
	}
}
