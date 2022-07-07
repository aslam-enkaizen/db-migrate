package com.exrade.api.impl;

import com.exrade.api.AccountAPI;
import com.exrade.core.ExLogger;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.LinkedAccount;
import com.exrade.models.userprofile.TokenAction;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.AccountStatus;
import com.exrade.models.userprofile.security.PlatformRole;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.exception.ExParamException;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.UserNotificationEvent;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.UserFields;
import com.exrade.runtime.rest.RestParameters.UserFilters;
import com.exrade.runtime.userprofile.AccountManager;
import com.exrade.runtime.userprofile.IAccountManager;
import com.exrade.runtime.userprofile.LinkedAccountManager;
import com.exrade.runtime.userprofile.providers.password.ExUsernamePasswordAuthProvider;
import com.exrade.util.ContextHelper;
import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AccountManagerAdapter implements AccountAPI {

	private IAccountManager manager = new AccountManager();
	private NotificationManager notificationManager = new NotificationManager();

	@Override
	public User findByUUID(ExRequestEnvelope request, String iUUID) {
		ContextHelper.initContext(request);
		return manager.findByUUID(iUUID);
	}

	@Override
	public User findByUsername(ExRequestEnvelope request, String iUsername) {
		ContextHelper.initContext(request);
		return manager.findByUsername(iUsername);
	}

	@Override
	public List<User> find(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();

		QueryFilters filters = QueryFilters.create(iFilters);

		filters.putIfNotNull(UserFields.FIRST_NAME, iFilters.get(UserFields.FIRST_NAME));
		filters.putIfNotNull(UserFields.LAST_NAME, iFilters.get(UserFields.LAST_NAME));
		filters.putIfNotNull(UserFields.PHONE, iFilters.get(UserFields.PHONE));
		filters.putIfNotNull(UserFields.ACCOUNT_STATUS, iFilters.get(UserFields.ACCOUNT_STATUS));
		
		if (!Strings.isNullOrEmpty(iFilters.get(UserFields.EMAIL)))
			filters.putIfNotNull(UserFields.EMAIL, iFilters.get(UserFields.EMAIL).toLowerCase());
		
		String keywords = iFilters.get(RestParameters.KEYWORDS);
		if (!Strings.isNullOrEmpty(keywords)){
			filters.putIfNotNull(RestParameters.KEYWORDS, keywords);
		}

		if(!(filters.containsKey(QueryParameters.UUID)
				|| filters.containsKey(UserFields.EMAIL)
				|| Security.isProfileAdministrator())) {
			throw new ExParamException(ErrorKeys.BADREQUEST_MISSING_PARAMETER);
		}

		if(filters.size() == 0)
			throw new ExParamException(ErrorKeys.BADREQUEST_MISSING_PARAMETER);

		return manager.find(filters);
	}

	@Override
	public User updateAccount(ExRequestEnvelope request, User iUser) {
		ContextHelper.initContext(request);
		return manager.updateAccount(iUser);
	}

	@Override
	public User updateRole(ExRequestEnvelope request, String userUUID,
			String iPlatformRole) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();
		return manager.updateRole(userUUID, iPlatformRole);
	}

	@Override
	public void changePassword(ExRequestEnvelope request,
			String newPassword, String oldPassword) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();
		manager.changePassword((User)ContextHelper.getMembership().getUser(), newPassword, oldPassword, true);
	}

	@Override
	public void resetPassword(ExRequestEnvelope request, String resetToken, String newPassword) {
		ContextHelper.initContext(request);
		// Pass true for the second parameter if you want to
		// automatically create a password and the exception never to
		// happen
		manager.resetPassword(resetToken, newPassword);
	}

	@Override
	public void updateAccountStatus(ExRequestEnvelope request,
			String userAccountUUID, AccountStatus iAccountStatus) {
		ContextHelper.initContext(request);
		manager.updateAccountStatus(userAccountUUID, iAccountStatus);
	}

	@Override
	public TokenAction changePasswordWithToken(String email) {
		// The email address given *BY AN UNKNWON PERSON* to the form - we
		// should find out if we actually have a user with this email
		// address and whether password login is enabled for him/her. Also
		// only send if the email address of the user has been verified.

		final User user = manager.findByUsername(email);

		if (user == null) {
			throw new ExParamException(ErrorKeys.PARAM_INVALID, "email");
		}

		LinkedAccount passwordAccount = LinkedAccountManager.getLinkedAccount(
				user.getLinkedAccounts(), ExUsernamePasswordAuthProvider.PROVIDER_KEY);

		if (passwordAccount != null){
			TokenAction tokenAction = null;
			if (user.isToValidate()){
				// User still not verified
				String verificationRecord = ExUsernamePasswordAuthProvider.generateVerificationRecord(user);
				tokenAction = TokenAction.create(TokenAction.Type.EMAIL_VERIFICATION, verificationRecord, user);
				notificationManager.process(new UserNotificationEvent(
						NotificationType.USER_SIGNUP_CONFIRMATION_REQUIRED, user, tokenAction));
			}
			else {
				// User verified
				String resetRecord = ExUsernamePasswordAuthProvider.generatePasswordResetRecord(user);
				tokenAction = TokenAction.create(TokenAction.Type.PASSWORD_RESET, resetRecord, user);
				notificationManager.process(new UserNotificationEvent(
						NotificationType.USER_PASSWORD_RESET_REQUESTED, user, tokenAction));
			}
			return tokenAction;
		}
		else {
			ExLogger.get().warn("No Username/Password authentication method is set for this email");;
			throw new ExNotFoundException(email);
		}
	}

	@Override
	public boolean existsAccount(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);

		filters.putIfNotNull(UserFields.EMAIL, iFilters.get(UserFields.EMAIL));
		filters.put(UserFilters.NOT_ACCOUNT_STATUS, AccountStatus.TO_VALIDATE);

		if(filters.size() == 0)
			throw new ExParamException(ErrorKeys.BADREQUEST_MISSING_PARAMETER);

		return manager.existsAccount(filters);
	}

	@Override
	public User createAccount(ExRequestEnvelope request, User iUser) {
		ContextHelper.initContext(request);
		Security.checkRole(ContextHelper.getMembership(),Arrays.asList(PlatformRole.SUPERADMIN,PlatformRole.MODERATOR));
		return manager.create(iUser);
	}

}
