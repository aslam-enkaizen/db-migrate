package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.userprofile.TokenAction;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.AccountStatus;

import java.util.List;
import java.util.Map;

public interface AccountAPI {

	User findByUUID(ExRequestEnvelope request, String iUUID);

	User findByUsername(ExRequestEnvelope request, String iUsername);

	List<User> find(ExRequestEnvelope request, Map<String, String> iFilters);
	
	User createAccount(ExRequestEnvelope request, User iUser);

	User updateAccount(ExRequestEnvelope request, User iUser);

	User updateRole(ExRequestEnvelope request, String userUUID, String iPlatformRole);

	void changePassword(ExRequestEnvelope request, String newPassword, String oldPassword);
	
	TokenAction changePasswordWithToken(String email);

	void resetPassword(ExRequestEnvelope request, String resetToken, String newPassword);

	void updateAccountStatus(ExRequestEnvelope request, String userAccountUUID,
			AccountStatus iAccountStatus);
	
	boolean existsAccount(ExRequestEnvelope request, Map<String, String> iFilters);

}