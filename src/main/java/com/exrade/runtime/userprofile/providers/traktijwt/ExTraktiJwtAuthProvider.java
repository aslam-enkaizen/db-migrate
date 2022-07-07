package com.exrade.runtime.userprofile.providers.traktijwt;

import com.exrade.models.userprofile.ExAuthRequest;
import com.exrade.models.userprofile.ExAuthResponse;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExParamException;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.runtime.userprofile.TraktiJwtManager;
import com.exrade.util.ContextHelper;
import com.google.common.base.Strings;

public class ExTraktiJwtAuthProvider {
	public static final String PROVIDER_KEY = "trakti_jwt";
	private IMembershipManager membershipManager = new MembershipManager();

	public ExAuthResponse authenticate(ExAuthRequest authRequest) {
		String membershipUUID = TraktiJwtManager.getInstance().decodeSubject(authRequest.getAccessToken());
		if(!Strings.isNullOrEmpty(membershipUUID)) {
			Membership membership = membershipManager.findByUUID(membershipUUID, true);
			ContextHelper.setUserProfile(membership);
            //user.setLastLogin(TimeProvider.now()); //TODO: update last login
            //accountManager.updateAccount(user);
	        ExAuthResponse authResponse = new ExAuthResponse();
	        authResponse.setMembership(membership);
	        authResponse.setAccessToken(TraktiJwtManager.getInstance().generateToken(membership.getUuid()));
	        return authResponse;
		}
		else {
			throw new ExParamException(ErrorKeys.PARAM_INVALID, "accessToken");
		}

	}

}
