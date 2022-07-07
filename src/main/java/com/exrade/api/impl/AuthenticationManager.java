package com.exrade.api.impl;

import com.exrade.api.AuthenticationAPI;
import com.exrade.models.activity.Verb;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.*;
import com.exrade.models.userprofile.TokenAction.Type;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExParamException;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.UserNotificationEvent;
import com.exrade.runtime.rest.RestParameters.TokenActionFields;
import com.exrade.runtime.userprofile.AccountManager;
import com.exrade.runtime.userprofile.IAccountManager;
import com.exrade.runtime.userprofile.TokenActionManager;
import com.exrade.runtime.userprofile.TraktiJwtManager;
import com.exrade.runtime.userprofile.providers.facebook.ExFacebookAuthProvider;
import com.exrade.runtime.userprofile.providers.google.ExGoogleAuthProvider;
import com.exrade.runtime.userprofile.providers.linkedin.ExLinkedinAuthProvider;
import com.exrade.runtime.userprofile.providers.password.ExLoginUsernamePasswordAuthUser;
import com.exrade.runtime.userprofile.providers.password.ExUsernamePasswordAuthProvider;
import com.exrade.runtime.userprofile.providers.password.ExUsernamePasswordAuthUser;
import com.exrade.runtime.userprofile.providers.traktijwt.ExTraktiJwtAuthProvider;
import com.exrade.util.ContextHelper;

import java.util.Arrays;
import java.util.Map;

public class AuthenticationManager implements AuthenticationAPI {
	private NotificationManager notificationManager = new NotificationManager();
	private IAccountManager accountManager = new AccountManager();

	@Override
	public ExAuthResponse doAuthenticate(ExAuthRequest authRequest) {
		ExAuthResponse exAuthResponse = null;
		if (authRequest.getAuthProvider() == AuthProvider.PASSWORD) {
			ExLoginUsernamePasswordAuthUser usernamePassAuthUser = new ExLoginUsernamePasswordAuthUser(
					authRequest.getPassword(), authRequest.getUserIdentifier());
			exAuthResponse = ExUsernamePasswordAuthProvider.login(usernamePassAuthUser);
		}

		else if (authRequest.getAuthProvider() == AuthProvider.LINKEDIN) {
			exAuthResponse = new ExLinkedinAuthProvider().authenticate(authRequest);
		}

		else if (authRequest.getAuthProvider() == AuthProvider.FACEBOOK) {
			exAuthResponse = new ExFacebookAuthProvider().authenticate(authRequest);
		}

		else if (authRequest.getAuthProvider() == AuthProvider.GOOGLE) {
			exAuthResponse = new ExGoogleAuthProvider().authenticate(authRequest);
		}
		
		else if(authRequest.getAuthProvider() == AuthProvider.TRAKTI_JWT) {
			exAuthResponse = new ExTraktiJwtAuthProvider().authenticate(authRequest);
		}

		if (exAuthResponse != null) {
			ActivityLogger.log(ContextHelper.getMembership(), Verb.LOGIN, exAuthResponse.getMembership().getUser(), Arrays.asList(exAuthResponse.getMembership()));
			return exAuthResponse;
		}
		throw new ExParamException(ErrorKeys.PARAM_INVALID, "provider");
	}

	@Override
	public void verify(String token) {
		final TokenAction ta = tokenIsValid(token, Type.EMAIL_VERIFICATION);
		if (ta == null) {
			throw new ExParamException(ErrorKeys.AUTHENTICATOR_TOKEN_INVALID, "token");
		}

		new TokenActionManager().verify(ta.getTargetUser());

		notificationManager.process(new UserNotificationEvent(
				NotificationType.USER_WELCOME_NOTIFICATION, ta.getTargetUser()));
	}

	@Override
	public String doSignup(ExUsernamePasswordAuthUser authUser) {
		String verificationCode = ExUsernamePasswordAuthProvider.signup(authUser);
		User user = accountManager.findByUsernamePasswordIdentity(authUser);

		TokenAction tokenAction = TokenAction.create(Type.EMAIL_VERIFICATION, verificationCode, user);
        notificationManager.process(new UserNotificationEvent(
				NotificationType.USER_SIGNUP_CONFIRMATION_REQUIRED, user, tokenAction, authUser));

		return verificationCode;
	}

	@Override
	public ExAuthResponse doSignupNoVerify(ExUsernamePasswordAuthUser authUser) {
		String verificationCode = ExUsernamePasswordAuthProvider.signup(authUser);
		verify(verificationCode);

		IAccountManager accountManager = new AccountManager();
		User user = accountManager.findByAuthUserIdentity(authUser);

		ExAuthResponse authResponse = new ExAuthResponse();
        authResponse.setMembership(user.getCurrentMembership());
        authResponse.setAccessToken(TraktiJwtManager.getInstance().generateToken(user.getCurrentMembership().getUuid()));

		return authResponse;
	}

	private static TokenAction tokenIsValid(final String iTokenCode, final Type type) {
		TokenAction token = null;
		if (iTokenCode != null && !iTokenCode.trim().isEmpty()) {
			final TokenAction ta = new TokenActionManager().findByToken(iTokenCode, type.name());
			if (ta != null && ta.isValid()) {
				token = ta;
			}
		}

		return token;
	}

	@Override
	public TokenAction getTokenAction(final String iTokenCode, Map<String, String> iFilters) {
		TokenAction token = null;
		String type = Type.EMAIL_VERIFICATION.name();
		if (iFilters.get(TokenActionFields.TYPE) != null){
			type = iFilters.get(TokenActionFields.TYPE);
		}
		if (iTokenCode != null && !iTokenCode.trim().isEmpty()) {
			token = new TokenActionManager().findByToken(iTokenCode, type);
		}
		return token;
	}
}
