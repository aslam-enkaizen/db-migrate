package com.exrade.models.invitations;

import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.exrade.runtime.timer.TimeProvider;

import java.util.Date;

public class AbstractInvitation extends BaseEntityUUIDTimeStampable {

	/** Default expiry days*/
	private final static Integer EXPIRY_DAYS = 14;
	
	private Date expiryDate = new Date();
	
	private String invitedEmail;
	
	private InvitationStatus invitationStatus = InvitationStatus.PENDING;
	
	private Membership invitedBy;
	
	public AbstractInvitation(){
	}

	public AbstractInvitation(String invitedEmail) {
		this(invitedEmail,EXPIRY_DAYS);
	}
	
	public AbstractInvitation(String invitedEmail,Integer expiryDays) {
		final Date created = TimeProvider.now();
		expiryDate = new Date(created.getTime() +  expiryDays * 24 * 3600 * 1000);
		setInvitedEmail(invitedEmail);
	}

	public String getInvitedEmail() {
		return invitedEmail;
	}

	public void setInvitedEmail(String invitedEmail) {
		this.invitedEmail = invitedEmail;
	}

	public InvitationStatus getInvitationStatus() {
		return invitationStatus;
	}

	public boolean isInvitationStatus(InvitationStatus iInvitationStatus){
		if (getInvitationStatus() != null){
			return getInvitationStatus().equals(iInvitationStatus);
		}
		return false;
	}
	
	public void setInvitationStatus(InvitationStatus invitationStatus) {
		this.invitationStatus = invitationStatus;
	}
	
	public Date getExpiryDate() {
		return expiryDate;
	}

	public Membership getInvitedBy() {
		return invitedBy;
	}

	public void setInvitedBy(Membership invitedBy) {
		this.invitedBy = invitedBy;
	}
	
}
