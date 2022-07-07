package com.exrade.models.team;

import com.exrade.models.Role;

import java.util.List;

public interface ITeamMember {

	String getUuid();

	String getMemberObjectType();

	String getMemberObjectID();

	String getFullName();

	String getAvatar();

	String getEmail();

	String getOrganisation();

	String getOrganisationUUID();

	String getOrganisationLogo();

	String getPhone();

	String getAddress();

	String getCity();

	String getCountry();

	List<Role> getRoles();

	boolean hasRole(String roleName);

}
