package com.exrade.models.userprofile.security;

import com.exrade.models.Permission;
import com.exrade.platform.security.Security.MemberPermissions;

import java.util.ArrayList;
import java.util.List;


public class MemberRole extends ExRole {

	public final static String OWNER = "profile.owner";
	public final static String ADMIN = "profile.admin";
	public final static String MEMBER = "profile.member";
	public final static String GUEST = "profile.guest";

	public MemberRole(){} 
	
	public MemberRole(String iName) {
		super(iName);
	}
	
	public static List<Permission> getPermissionsForRole(String role){
		List<Permission> permissions = new ArrayList<>();
		
		if (OWNER.equals(role)) {
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_ACTIVITIES_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_PRIVATE_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_JOIN ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_ARCHIVE ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.WORKGROUP_MANAGEMENT ));
			permissions.add(ExPermission.create(MemberPermissions.WORKGROUP_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.CONTACT_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.CONTACT_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.CONTACT_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.CONTACT_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.MEMBER_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.MEMBER_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.MEMBER_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.MEMBER_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.PERMISSION_ASSIGNMENT ));
			permissions.add(ExPermission.create(MemberPermissions.PERMISSION_ROLE_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.PERMISSION_ROLE_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.PERMISSION_ROLE_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.SUBSCRIPTION_MANAGEMENT ));
			permissions.add(ExPermission.create(MemberPermissions.PAYMENTMETHOD_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.PAYMENTMETHOD_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.PAYMENTMETHOD_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.PAYMENTMETHOD_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATIONTERMS_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATIONTERMS_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATIONTERMS_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATIONTERMS_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.PROFILE_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.CONTRACT_SIGN ));
			permissions.add(ExResourcePermission.create(MemberPermissions.NEGOTIATION_MAX_AMOUNT, Integer.MAX_VALUE));
		}
		
		if (ADMIN.equals(role)) {
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_ACTIVITIES_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_PRIVATE_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_JOIN ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_ARCHIVE ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.WORKGROUP_MANAGEMENT ));
			permissions.add(ExPermission.create(MemberPermissions.WORKGROUP_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.CONTACT_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.CONTACT_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.CONTACT_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.CONTACT_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.MEMBER_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.MEMBER_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.MEMBER_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.MEMBER_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.PERMISSION_ASSIGNMENT ));
			permissions.add(ExPermission.create(MemberPermissions.PERMISSION_ROLE_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.PERMISSION_ROLE_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.PERMISSION_ROLE_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.PAYMENTMETHOD_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.PAYMENTMETHOD_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.PAYMENTMETHOD_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.PAYMENTMETHOD_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATIONTERMS_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATIONTERMS_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATIONTERMS_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATIONTERMS_DELETE ));
			permissions.add(ExPermission.create(MemberPermissions.PROFILE_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.CONTRACT_SIGN ));
			permissions.add(ExResourcePermission.create(MemberPermissions.NEGOTIATION_MAX_AMOUNT, Integer.MAX_VALUE));
		}
		
		if (MEMBER.equals(role)) {
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATION_JOIN ));
			permissions.add(ExPermission.create(MemberPermissions.WORKGROUP_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.CONTACT_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.CONTACT_CREATE ));
			permissions.add(ExPermission.create(MemberPermissions.CONTACT_EDIT ));
			permissions.add(ExPermission.create(MemberPermissions.MEMBER_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.PAYMENTMETHOD_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.NEGOTIATIONTERMS_VIEW ));
			permissions.add(ExPermission.create(MemberPermissions.CONTRACT_SIGN ));
			permissions.add(ExResourcePermission.create(MemberPermissions.NEGOTIATION_MAX_AMOUNT, Integer.MAX_VALUE));
		}
		
		return permissions;
	}
}
