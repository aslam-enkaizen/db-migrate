package com.exrade.models.contract;

import com.exrade.models.Role;
import com.exrade.models.contact.Contact;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class ContractContactMember extends ContractMemberBase  {
	public static final String MEMBER_OBJECT_TYPE = "Contact";

	private Contact contact;
	
	private List<Role> roles = new ArrayList<>();

	@JsonIgnore
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getUuid() {
		return getContact().getUuid();
	}

	@Override
	public String getMemberObjectType() {
		return MEMBER_OBJECT_TYPE;
	}

	@Override
	public String getFullName() {
		return getContact().getName();
	}

	@Override
	public String getEmail() {
		return getContact().getEmail();
	}

	@Override
	public String getOrganisation() {
		return getContact().getOrganization();
	}
	
	@Override
	public String getOrganisationUUID() {
		return null;
	}

	@Override
	public String getPhone() {
		return getContact().getPhone();
	}

	@Override
	public String getAddress() {
		return getContact().getAddress();
	}

	@Override
	public String getCity() {
		return getContact().getCity();
	}

	@Override
	public String getCountry() {
		return getContact().getCountry();
	}

	@Override
	public List<Role> getRoles() {
		return roles;
	}

	@Override
	public String getAvatar() {
		return null;
	}

	@Override
	public String getOrganisationLogo() {
		return null;
	}
}
