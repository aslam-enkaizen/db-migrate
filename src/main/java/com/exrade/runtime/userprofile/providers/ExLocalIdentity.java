package com.exrade.runtime.userprofile.providers;

public interface ExLocalIdentity {
	String getTimezone();
	String getLanguage();
	String getPhone();
	String getCity();
	String getAddress();
	String getCountry();
	String getPlanName();
	String getProfileUUID();
	String getBusinessName();
	String getVat();
	String getPostcode();
}
