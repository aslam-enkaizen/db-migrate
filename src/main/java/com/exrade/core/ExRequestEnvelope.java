package com.exrade.core;

import com.exrade.models.i18n.ExLang;
import com.google.common.base.Strings;


public class ExRequestEnvelope {

	private String identifier;
	private String accessToken;
	private String language = ExLang.ENGLISH.getCode();
	
	public ExRequestEnvelope(){		
	}
	
	public ExRequestEnvelope(String identifier, String accessToken, String language){
		setIdentifier(identifier);
		setAccessToken(accessToken);
		setLanguage(language);
	}
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String token) {
		this.accessToken = token;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		if(!Strings.isNullOrEmpty(language))
			this.language = language;
	}
}
