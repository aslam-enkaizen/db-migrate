package com.exrade.models.userprofile.security;

import com.exrade.models.Permission;
import com.exrade.platform.persistence.BaseEntity;

public class ExPermission extends BaseEntity implements Permission {
	 
	protected String value;

	public static Permission create(String iValue) {
		ExPermission permission = new ExPermission();
		permission.value = iValue;
		return permission;
	} 
	
	@Override
	public String getValue() {
		return value;
	}
	
}
