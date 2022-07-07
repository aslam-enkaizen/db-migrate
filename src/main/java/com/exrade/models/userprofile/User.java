package com.exrade.models.userprofile;

import com.exrade.models.userprofile.security.AccountStatus;
import com.exrade.models.userprofile.security.PlatformRole;
import com.exrade.platform.persistence.BaseEntityUUID;
import com.exrade.platform.security.Security;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ExCollections;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class User extends BaseEntityUUID implements IUser {
//	@Required
	private String userName;

//	@Required
	private String email;

//	@Required
	private String phone;

//	@Required
	private String firstName;

//	@Required
	private String lastName;

//	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastLogin;

//	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dateJoined = TimeProvider.now();

	public Date getDateJoined() {
		return dateJoined;
	}

	private AccountStatus accountStatus = AccountStatus.TO_VALIDATE;

	private String language;

	private String timezone;

	private String avatar;

	private List<LinkedAccount> linkedAccounts = new ArrayList<>();

	private Membership defaultMembership;

	private Membership currentMembership;

	private PlatformRole platformRole;

	private Map<String, Object> customFields = new HashMap<>();

	public static User createUser(String iUserName){
		User user = new User();
		user.setUserName(iUserName);
		return user;
	}

	public String setUuid(String uuid) {
		return this.uuid = uuid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public List<LinkedAccount> getLinkedAccounts() {
		return linkedAccounts;
	}

	public void setLinkedAccounts(List<LinkedAccount> linkedAccounts) {
		this.linkedAccounts = linkedAccounts;
	}

	@JsonIgnore
	public Membership getCurrentMembership() {
		if (currentMembership == null){
			return getDefaultMembership();
		}
		return currentMembership;
	}

	@JsonIgnore
	public Membership getDefaultMembership(){
		return defaultMembership;
	}

	public void setCurrentMembership(Membership currentMemebership) {
		this.currentMembership = currentMemebership;
	}

	public PlatformRole getPlatformRole() {
		return platformRole;
	}

	public void setPlatformRole(PlatformRole platformRole) {
		this.platformRole = platformRole;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isSuperAdmin(){
		return Security.hasRole(getPlatformRole(),PlatformRole.SUPERADMIN);
	}

	public void setDefaultMembership(Membership defaultMembership) {
		this.defaultMembership = defaultMembership;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	@JsonIgnore
	public boolean isActive() {
		return AccountStatus.ACTIVE.equals(getAccountStatus());
	}

	@JsonIgnore
	public boolean isToValidate() {
		return AccountStatus.TO_VALIDATE.equals(getAccountStatus());
	}

	@JsonIgnore
	public boolean isDisabled() {
		return AccountStatus.DISABLED.equals(getAccountStatus());
	}

	@JsonIgnore
	public boolean isGuest() {
		return ExCollections.isEmpty(getLinkedAccounts());
	}

	public String getFullName(){
		return getFirstName() + " " + getLastName();
	}

	public Map<String, Object> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, Object> customFields) {
		this.customFields = customFields;
	}

	@Override
	public String toString() {
		return "User{" +
				"userName='" + userName + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", lastLogin=" + lastLogin +
				", dateJoined=" + dateJoined +
				", accountStatus=" + accountStatus +
				", language='" + language + '\'' +
				", timezone='" + timezone + '\'' +
				", avatar='" + avatar + '\'' +
				", linkedAccounts=" + linkedAccounts +
				", defaultMembership=" + defaultMembership +
				", currentMembership=" + currentMembership +
				", platformRole=" + platformRole +
				", customFields=" + customFields +
				", uuid='" + uuid + '\'' +
				", id='" + id + '\'' +
				", version=" + version +
				'}';
	}
}
