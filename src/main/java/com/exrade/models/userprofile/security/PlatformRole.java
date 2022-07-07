package com.exrade.models.userprofile.security;

public class PlatformRole extends ExRole {

	public final static String SUPERADMIN = "platform.superadmin";
	public final static String MODERATOR = "platform.moderator";
	public final static String MEMBER = "platform.member";
	
	public PlatformRole(){
		
	}
	
	public PlatformRole(String iName) {
		super(iName);
	}

}
