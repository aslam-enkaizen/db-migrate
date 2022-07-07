package com.exrade.models.review;

public class Reviewer {

	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String title;
	private String businessName;
	private String membershipIdentifier;
	private boolean guest;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getMembershipIdentifier() {
		return membershipIdentifier;
	}
	public void setMembershipIdentifier(String identifier) {
		this.membershipIdentifier = identifier;
	}
	public boolean isGuest() {
		return guest;
	}
	public void setGuest(boolean guest) {
		this.guest = guest;
	}

}
