package com.exrade.runtime.userprofile.providers.facebook;

import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthenticationException;
import com.exrade.runtime.userprofile.providers.ExOAuth2AuthUser;
import com.exrade.user.ProfiledIdentity;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

public class ExFacebookAuthUser extends ExOAuth2AuthUser implements ProfiledIdentity {


	private static final long serialVersionUID = 1L;



	public ExFacebookAuthUser(final JsonNode nodeInfo, ExFacebookAuthInfo info) {
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
			this.picture = nodeInfo.get(Constants.PROFILE_IMAGE_URL).get("data").get("url").asText();
		}
		if (nodeInfo.has(Constants.TIMEZONE)) {
			this.setTimezone(nodeInfo.get(Constants.TIMEZONE).asText());
		}

		if (nodeInfo.has(Constants.PROFILE_LINK)) {
			this.profileLink = nodeInfo.get(Constants.PROFILE_LINK)
					.asText();
		}
		if (nodeInfo.has(Constants.TIMEZONE)) {
			this.setTimezone(nodeInfo.get(Constants.TIMEZONE).asText());
		}

		if (nodeInfo.has(Constants.LOCALE)) {
			this.country = nodeInfo.get(Constants.LOCALE).asText().substring(0,2);
		}

		if (nodeInfo.has(Constants.ABOUT)) {
			this.description = nodeInfo.get(Constants.ABOUT).asText();
		}

		if (nodeInfo.has(Constants.WEBSITE)) {
			this.website = nodeInfo.get(Constants.WEBSITE).asText();
		}

		//TODO put a for cycle since this is an array

		if (nodeInfo.has(Constants.INTERESTS)) {
			JsonNode interestData = nodeInfo.get(Constants.INTERESTS).get("data");
			List<String> interestList = new ArrayList<>();
			for (JsonNode interestJson : interestData) {
				interestList.add(interestJson.get("name").asText());
			}
			this.interests = Joiner.on(",").join(interestList);
		}

		if (nodeInfo.has(Constants.LOCATION)) {
			this.city = nodeInfo.get(Constants.LOCATION).get("name").asText();
		}

	}

	private static abstract class Constants {

		public static final String ID = "id";
		public static final String FIRST_NAME = "first_name";
		public static final String LAST_NAME = "last_name";
		public static final String EMAIL = "email";
		public static final String PROFILE_IMAGE_URL = "picture";
		public static final String PROFILE_LINK = "link";
		public static final String TIMEZONE = "timezone";
		public static final String ABOUT = "about";
		public static final String BIRTHDAY = "birthday";
		public static final String CURRENCY = "currency";
		public static final String HOMETOWN = "hometown";
		public static final String WEBSITE = "website";
		public static final String INTERESTS = "interests";
		public static final String LOCATION = "location";
		public static final String LOCALE = "locale";
		public static final String AGE_RANGE = "age_range";
		public static final String LANGUAGES = "languages";

	}

	@Override
	public String getProvider() {
		return ExFacebookAuthProvider.PROVIDER_KEY;
	}

}
