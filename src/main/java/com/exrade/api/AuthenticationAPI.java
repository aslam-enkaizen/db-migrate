package com.exrade.api;

import com.exrade.models.userprofile.ExAuthRequest;
import com.exrade.models.userprofile.ExAuthResponse;
import com.exrade.models.userprofile.TokenAction;
import com.exrade.runtime.userprofile.providers.password.ExUsernamePasswordAuthUser;

import java.util.Map;

public interface AuthenticationAPI {

	ExAuthResponse doAuthenticate(ExAuthRequest authRequest);
	
	void verify(String token);
	
	TokenAction getTokenAction(final String iTokenCode,Map<String, String> iFilters);
	
	String doSignup(ExUsernamePasswordAuthUser authUser);
	
	ExAuthResponse doSignupNoVerify(ExUsernamePasswordAuthUser authUser);
}
