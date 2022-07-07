package com.exrade.models.contact;

import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Profile;
import com.exrade.platform.persistence.BaseEntityUUID;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contact extends BaseEntityUUID {

	private Date created;

	private Date updated;

	private String email;

	private String name;

	private String organization;

	private String phone;

	private String address;

	private String city;

	private String country;

	private String note;
	
	private String externalId;

	private List<String> tags;
	
	private Map<String, Object> customFields = new HashMap<>();

	private Membership linkedMembership; // linked Membership

	private Profile ownerProfile; // owner profile of the Contact

	private Membership owner; // owner of the Contact

	public Contact(){}

	public Contact(Membership fromMembership){
		if(fromMembership != null){
			this.setLinkedMembership(fromMembership);
			this.setEmail(fromMembership.getEmail());
			this.setAddress(fromMembership.getProfile().getAddress());
			this.setCity(fromMembership.getProfile().getCity());
			this.setCountry(fromMembership.getProfile().getCountry());
			this.setName(fromMembership.getFirstName() + " " + fromMembership.getLastName());
			this.setPhone(fromMembership.getProfile().getPhone());
			this.setOrganization(fromMembership.getName());
		}
	}

	public Contact(String email){
		this.setEmail(email);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Map<String, Object> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, Object> customFields) {
		this.customFields = customFields;
	}

	public Membership getLinkedMembership() {
		return linkedMembership;
	}

	public void setLinkedMembership(Membership membership) {
		this.linkedMembership = membership;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Profile getOwnerProfile() {
		return ownerProfile;
	}

	public void setOwnerProfile(Profile profile) {
		this.ownerProfile = profile;
	}

	public String getLinkedMembershipIdentifier() {
		if(getLinkedMembership() != null)
			return getLinkedMembership().getUuid();
		return null;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Membership getOwner() {
		return owner;
	}

	public void setOwner(Membership owner) {
		this.owner = owner;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
}
