package com.exrade.models.contract;

import com.exrade.models.Role;
import com.exrade.models.userprofile.Negotiator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class ContractUserMember extends ContractMemberBase  {
	public static final String MEMBER_OBJECT_TYPE = "Membership";

	private Negotiator negotiator;
	
	private List<Role> roles = new ArrayList<>();
	
	public String getUuid() {
		return getNegotiator().getIdentifier();
	}

	@Override
	public String getMemberObjectType() {
		return MEMBER_OBJECT_TYPE;
	}

	@Override
	public String getFullName() {
		return getNegotiator().getUser().getFullName();
	}

	@Override
	public String getEmail() {
		return getNegotiator().getUser().getEmail();
	}

	@Override
	public String getOrganisation() {
		if(getNegotiator().getProfile().isBusinessProfile())
			return getNegotiator().getName();
		else
			return null;
	}
	
	@Override
	public String getOrganisationUUID() {
		if(getNegotiator().getProfile().isBusinessProfile())
			return getNegotiator().getProfile().getUuid();
		else
			return null;
	}

	@Override
	public String getPhone() {
		return getNegotiator().getProfile().getPhone();
	}

	@Override
	public String getAddress() {
		return getNegotiator().getProfile().getAddress();
	}

	@Override
	public String getCity() {
		return getNegotiator().getProfile().getCity();
	}

	@Override
	public String getCountry() {
		return getNegotiator().getProfile().getCountry();
	}

	@JsonIgnore
	public Negotiator getNegotiator() {
		return negotiator;
	}

	public void setNegotiator(Negotiator negotiator) {
		this.negotiator = negotiator;
	}

	@Override
	public List<Role> getRoles() {
		return roles;
	}

	@Override
	public String getAvatar() {
		return getNegotiator().getUser().getAvatar();
	}

	@Override
	public String getOrganisationLogo() {
		return getNegotiator().getProfile().getLogo();
	}
	
}
