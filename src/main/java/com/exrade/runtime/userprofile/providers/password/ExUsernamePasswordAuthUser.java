package com.exrade.runtime.userprofile.providers.password;

import com.exrade.providers.password.UsernamePasswordAuthUser;
import com.exrade.runtime.userprofile.providers.ExLocalIdentity;
import com.exrade.runtime.userprofile.providers.password.ExUsernamePasswordAuthProvider.SignupForm;
import com.exrade.user.FirstLastNameIdentity;

public class ExUsernamePasswordAuthUser extends UsernamePasswordAuthUser implements ExLocalIdentity,FirstLastNameIdentity {

	private static final long serialVersionUID = -3691039228647415195L;

	private String firstName;

	private String lastName;

	private String timezone;

	private String planName;

	private String profileUUID;

	private String language;

	private String phone;

	private String address;

	private String country;

	private String city;

	private String postcode;

	private String businessName;

	private String vat;

	private String redirectUrl;


	public ExUsernamePasswordAuthUser(String clearPassword, String email) {
		super(clearPassword, email);
	}

	public static ExUsernamePasswordAuthUser create(String email,String clearPassword,String firstName,String lastName
			,String timezone,String planName,String profileUUID,String language) {
		ExUsernamePasswordAuthUser authUser = new ExUsernamePasswordAuthUser(clearPassword,email);
		authUser.firstName = firstName;
		authUser.lastName = lastName;
		authUser.timezone = timezone;
		authUser.planName = planName;
		authUser.profileUUID = profileUUID;
		authUser.language = language;
		return authUser;
	}

	public static ExUsernamePasswordAuthUser create(SignupForm iSignupForm) {
		ExUsernamePasswordAuthUser authUser = new ExUsernamePasswordAuthUser(iSignupForm.password, iSignupForm.email.toLowerCase());
		authUser.firstName = iSignupForm.firstName;
		authUser.lastName = iSignupForm.lastName;
		authUser.timezone = iSignupForm.timezone;
		authUser.planName= iSignupForm.planName;
		authUser.profileUUID = iSignupForm.profileUUID;
		authUser.language = iSignupForm.language;
		authUser.phone = iSignupForm.phone;
		authUser.address = iSignupForm.businessName;
		authUser.city = iSignupForm.city;
		authUser.country = iSignupForm.country;
		authUser.businessName = iSignupForm.businessName;
		authUser.vat = iSignupForm.vat;
		authUser.postcode = iSignupForm.postcode;
		authUser.redirectUrl = iSignupForm.redirectUrl;
		return authUser;
	}

	/**
	 * Used for password reset only - do not use this to signup a user!
	 * @param password
	 */
	public ExUsernamePasswordAuthUser(final String password) {
		super(password, null);
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getTimezone() {
		return timezone;
	}

	public String getPlanName() {
		return planName;
	}

	public String getName(){
		return getFirstName() +" "+ getLastName();
	}

	public String getLanguage() {
		return language;
	}

	@Override
	public String getPhone(){
		return phone;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getCountry() {
		return country;
	}

	@Override
	public String getCity() {
		return city;
	}

	@Override
	public String getProfileUUID() {
		return profileUUID;
	}

	@Override
	public String getBusinessName() {
		return businessName;
	}

	@Override
	public String getVat() {
		return vat;
	}

	@Override
	public String getPostcode() {
		return postcode;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

}
