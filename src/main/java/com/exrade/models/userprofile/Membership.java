package com.exrade.models.userprofile;

import com.exrade.models.Permission;
import com.exrade.models.Role;
import com.exrade.models.userprofile.security.MemberRole;
import com.exrade.models.userprofile.security.MemberStatus;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.exrade.runtime.timer.TimeProvider;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Membership extends BaseEntityUUIDTimeStampable implements Negotiator {

	//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="uuid")
	//@JsonIdentityReference(alwaysAsId=true)
	private User user;

	//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="uuid")
	//@JsonIdentityReference(alwaysAsId=true)
	private Profile profile;

	private MemberRole role;

	private MemberStatus status;

	private Date expirationDate;

	private boolean agreementSigner = true;

	private Double maxNegotiationAmount;

	private List<String> authorizationDocuments = new ArrayList<String>();

	private String title;

	private String walletAddress;

	//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="uuid")
	//@JsonIdentityReference(alwaysAsId=true)
	@JsonIgnoreProperties({"user", "profile", "plan", "updatedBy", "supervisor" })
	private Membership updatedBy;

	//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="uuid")
	//@JsonIdentityReference(alwaysAsId=true)
	@JsonIgnoreProperties({"user", "profile", "plan", "updatedBy", "supervisor" })
	private Membership supervisor;

	private boolean guest = false;

	public static Membership createMembership(User iUser, Profile iProfile, MemberRole iRole) {
		Membership memberProfile = new Membership();
		memberProfile.user = iUser;
		memberProfile.profile = iProfile;
		memberProfile.role = iRole;
		memberProfile.status = MemberStatus.ACTIVE;
		return memberProfile;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(MemberRole role) {
		this.role = role;
	}

	public User getUser() {
		return user;
	}

	public Profile getProfile() {
		return profile;
	}

	@Override
	public String getIdentifier () {
		return getUuid();
	}

	@Override
	public boolean isPublicProfile() {
		return getProfile().isPublicProfile();
	}

	/**
	 * @return name of the business.
	 */
	@Override
	public String getName() {
		return getProfile().getName();
	}

	@Override
	//@JsonIgnoreProperties({"permissions", "profile", "plan" })
	public Plan getPlan() {
		if(getProfile().getPlanSubscription() != null)
			return getProfile().getPlanSubscription().getPlan();
		else
			return null;
	}

	@Override
	public List<Role> getRoles() {
		List<Role> roles = new ArrayList<>(getProfile().getRoles());
		roles.add(getUser().getPlatformRole());
		roles.add(getRole());
		return Collections.unmodifiableList(roles);
	}

	@Override
	public List<? extends Permission> getPermissions() {
		return getProfile().getPermissions();
	}

	public String getTimezone(){
		return getUser().getTimezone();
	}

	public String getFirstName(){
		return getUser().getFirstName();
	}

	public String getLastName(){
		return getUser().getLastName();
	}

	public String getUserAvatar(){
		return getUser().getAvatar();
	}

	public String getBusinessLogo(){
		return getProfile().getLogo();
	}

	public boolean isBusinessProfile(){
		return getProfile() != null && getProfile().isBusinessProfile();
	}

	public String getLanguage(){
		return getUser().getLanguage();
	}

	public String getProfileUUID(){
		return getProfile().getIdentifier();
	}

	public String getUserUUID(){
		return getUser().getUuid();
	}

	public String getEmail(){
		return getUser().getEmail();
	}

	public String getPhone(){
		return getUser().getPhone();
	}

	public String getPlatformRole(){
		return getUser().getPlatformRole().getName();
	}

	public void setProfile(Profile iProfile){
		this.profile = iProfile;
	}

	public MemberStatus getStatus() {
		return status;
	}

	public void setStatus(MemberStatus status) {
		this.status = status;
	}

	public List<String> getProviderNames(){
		List<String> providerNames = new ArrayList<>();
		for (LinkedAccount linkedAccount : getUser().getLinkedAccounts()) {
			providerNames.add(linkedAccount.getProviderKey());
		}
		return providerNames;
	}

	public boolean isProfileActive(){
		return getUser().isActive() && getProfile().isActive();
	}

	public boolean isActive(){
		return isGuest() || (getUser().isActive() && MemberStatus.ACTIVE.equals(getStatus()) && !isMembershipExpired());
	}

	public String getFullName(){
		return getUser().getFullName();
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public boolean isAgreementSigner() {
		return agreementSigner;
	}

	public void setAgreementSigner(boolean agreementSigner) {
		this.agreementSigner = agreementSigner;
	}

	@Override
	public Double getMaxNegotiationAmount() {
		return maxNegotiationAmount;
	}

	public void setMaxNegotiationAmount(Double maxNegotiationAmount) {
		this.maxNegotiationAmount = maxNegotiationAmount;
	}

	public boolean isMembershipExpired(){
		if(getExpirationDate() != null && getExpirationDate().before(TimeProvider.now())){
			return true;
		}
		return false;
	}

	public List<String> getAuthorizationDocuments() {
		return authorizationDocuments;
	}

	public void setAuthorizationDocuments(List<String> authorizationDocuments) {
		this.authorizationDocuments = authorizationDocuments;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Membership getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Membership updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Membership getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Membership supervisor) {
		this.supervisor = supervisor;
	}

	public String getSupervisorUUID(){
		return getSupervisor() != null ? getSupervisor().getUuid() : null;
	}

	@Override
	public boolean isGuest() {
		return guest;
	}

	public void setGuest(boolean guest) {
		this.guest = guest;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public String getProfileWalletAddress() {
		return getProfile() != null ? getProfile().getWalletAddress() : null;
	}
	
	public String getRoleName() {
		return getRole() != null ? getRole().getName() : null;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Membership{" +
				"user=" + user +
				", profile=" + profile +
				", role=" + role +
				", status=" + status +
				", expirationDate=" + expirationDate +
				", agreementSigner=" + agreementSigner +
				", maxNegotiationAmount=" + maxNegotiationAmount +
				", authorizationDocuments=" + authorizationDocuments +
				", title='" + title + '\'' +
				", walletAddress='" + walletAddress + '\'' +
				", updatedBy=" + updatedBy +
				", supervisor=" + supervisor +
				", guest=" + guest +
				", creationDate=" + creationDate +
				", updateDate=" + updateDate +
				", uuid='" + uuid + '\'' +
				", id='" + id + '\'' +
				", version=" + version +
				'}';
	}
}
