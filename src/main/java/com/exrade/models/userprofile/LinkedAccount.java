package com.exrade.models.userprofile;

import com.exrade.platform.persistence.BaseEntity;
import com.exrade.user.AuthUser;

public class LinkedAccount extends BaseEntity {

	private String providerUserId;
	private String providerKey;

	public LinkedAccount(){

	}

	public LinkedAccount(String provider, String userId) {
		this.providerKey = provider;
		this.providerUserId = userId;
	}

	public static LinkedAccount create(final AuthUser authUser) {
		final LinkedAccount linkedAccount = new LinkedAccount();
		linkedAccount.providerKey = authUser.getProvider();
		linkedAccount.providerUserId = authUser.getId();
		return linkedAccount;
	}

	public String getProviderUserId() {
		return providerUserId;
	}

	public void setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
	}

	public String getProviderKey() {
		return providerKey;
	}
}
