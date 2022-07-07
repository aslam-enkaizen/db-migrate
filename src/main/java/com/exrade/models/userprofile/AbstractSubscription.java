package com.exrade.models.userprofile;

import com.exrade.models.userprofile.security.SubscriptionStatus;
import com.exrade.platform.persistence.BaseEntityUUID;

import java.util.Date;

public class AbstractSubscription extends BaseEntityUUID {
	
	protected Date cancelDate;

	protected Date creationDate;
	
	protected Date updateDate;
	
	protected Date trialStartDate;
	
	protected Date trialEndDate;
	
	protected SubscriptionStatus status;
    
	protected String externalSubscriptionID;
	
	protected String externalClientID;
	
	protected boolean gift;
	
	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getExternalSubscriptionID() {
		return externalSubscriptionID;
	}

	public void setExternalSubscriptionID(String externalSubscriptionID) {
		this.externalSubscriptionID = externalSubscriptionID;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getExternalClientID() {
		return externalClientID;
	}

	public void setExternalClientID(String externalClientID) {
		this.externalClientID = externalClientID;
	}

	public Date getTrialStartDate() {
		return trialStartDate;
	}

	public void setTrialStartDate(Date trialStartDate) {
		this.trialStartDate = trialStartDate;
	}

	public Date getTrialEndDate() {
		return trialEndDate;
	}

	public void setTrialEndDate(Date trialEndDate) {
		this.trialEndDate = trialEndDate;
	}

	public SubscriptionStatus getStatus() {
		return status;
	}

	public void setStatus(SubscriptionStatus status) {
		this.status = status;
	}
	
    public boolean isGift() {
		return gift;
	}

	public void setGift(boolean gift) {
		this.gift = gift;
	}

}
