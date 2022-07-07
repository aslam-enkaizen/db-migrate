package com.exrade.models.invitations;

import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemberInvitation extends AbstractInvitation {

	private User invitedUser;
	
	private Profile invitedProfile;
	
	private String roleName;
	
	private String title;
	
	private Date membershipExpirationDate;
	
	private boolean agreementSigner = true;
	
	private Double maxNegotiationAmount;
	
	private List<String> authorizationDocuments = new ArrayList<String>();
	
	private Membership supervisor;

	public MemberInvitation(){}

	public MemberInvitation(User iInvitedUser,Profile iInvitedProfile,String iRoleName){
		this(iInvitedUser.getEmail(),iInvitedProfile,iRoleName);
		setInvitedUser(iInvitedUser);
	}
	
	public MemberInvitation(String invitedEmail, Profile iInvitedProfile,String iRoleName){
		super(invitedEmail);
		invitedProfile = iInvitedProfile;
		roleName = iRoleName;
	}
	
	@JsonIgnore
	public User getInvitedUser() {
		return invitedUser;
	}
	
	public String getInvitedUserUUID() {
		if (getInvitedUser()!= null){
			return getInvitedUser().getUuid();
		}
		return null;
	}
	
	public boolean isInvitedUserGuest() {
		if (getInvitedUser()!= null){
			return getInvitedUser().isGuest();
		}
		return false;
	}

	public void setInvitedUser(User invitedUser) {
		this.invitedUser = invitedUser;
	}

	@JsonIgnore
	public Profile getInvitedProfile() {
		return invitedProfile;
	}
	
	public void setInvitedProfile(Profile invitedProfile) {
		this.invitedProfile = invitedProfile;
	}
	
	public String getInvitedProfileUUID() {
		if (getInvitedProfile()!= null){
			return getInvitedProfile().getUuid();
		}
		return null;
	}

	public String getRoleName() {
		return roleName;
	}
	
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Date getMembershipExpirationDate() {
		return membershipExpirationDate;
	}

	public void setMembershipExpirationDate(Date expirationDate) {
		this.membershipExpirationDate = expirationDate;
	}

	public boolean isAgreementSigner() {
		return agreementSigner;
	}

	public void setAgreementSigner(boolean agreementSigner) {
		this.agreementSigner = agreementSigner;
	}

	public Double getMaxNegotiationAmount() {
		return maxNegotiationAmount;
	}

	public void setMaxNegotiationAmount(Double maxNegotiationAmount) {
		this.maxNegotiationAmount = maxNegotiationAmount;
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

	public Membership getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Membership supervisor) {
		this.supervisor = supervisor;
	}
}
