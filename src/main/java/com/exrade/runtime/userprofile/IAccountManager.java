package com.exrade.runtime.userprofile;

import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.AccountStatus;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.providers.password.UsernamePasswordAuthUser;
import com.exrade.user.AuthUser;
import com.exrade.user.AuthUserIdentity;

import java.util.List;

public interface IAccountManager {

	User findByUUID(String iUUID);

	User findByAuthUserIdentity(AuthUserIdentity identity);

	User findByUsernamePasswordIdentity(UsernamePasswordAuthUser identity);

	User findByUsername(String iUsername);

	List<User> find(QueryFilters iFilters);

	User create(User user);
	
	User create(AuthUser authUser);

	User create(AuthUser authUser, AccountStatus status);

	User updateAccount(User iUser);

	User updateRole(String userUUID, String iPlatformRole);

	void changePassword(User user, String newPassword, String oldPassword, boolean create);

	void resetPassword(String resetToken, String newPassword);

	void updateAccountStatus(String userAccountUUID,
			AccountStatus iAccountStatus);
	
	boolean existsAccount(QueryFilters iFilters);

	User convertFromGuest(AuthUser authUser, AccountStatus status);

}
