package com.exrade.models.userprofile;

import com.exrade.models.userprofile.security.SubscriptionStatus;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.runtime.timer.TimeProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Calendar;


public class PlanSubscription extends AbstractSubscription {

	private Plan plan;

	private Profile profile;

	private BillingAddress billingAddress;

	private String externalPaymentConfirmationToken;
	
	private String externalPaymentConfirmationType;

	public static PlanSubscription create(Plan iPlan, Profile iProfile, BillingAddress billingAddress){
		PlanSubscription subscription = PersistentManager.newDbInstance(PlanSubscription.class);
		subscription.setPlan(iPlan);
		subscription.setProfile(iProfile);
		subscription.setBillingAddress(billingAddress);
		subscription.setCreationDate(TimeProvider.now());

		if(iPlan != null && !iPlan.isFree() && iPlan.getTrialPeriodDays() > 0) {
			subscription.setTrialStartDate(subscription.getCreationDate());

			Calendar cal = Calendar.getInstance();
			cal.setTime(subscription.creationDate);
			cal.add(Calendar.DATE, iPlan.getTrialPeriodDays());
			subscription.setTrialEndDate(cal.getTime());
			subscription.setStatus(SubscriptionStatus.IN_TRIAL);
		}

		iProfile.setPlanSubscription(subscription);
		return subscription;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	@JsonIgnore
	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public String getProfileUUID() {
		if(getProfile() != null)
			return getProfile().getUuid();
		return null;
	}

	public String getPlanUUID() {
		if(getPlan() != null)
			return getPlan().getUuid();
		return null;
	}

	public BillingAddress getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(BillingAddress billingAddress) {
		this.billingAddress = billingAddress;
	}

	public String getExternalPaymentConfirmationToken() {
		return externalPaymentConfirmationToken;
	}

	public void setExternalPaymentConfirmationToken(String externalPaymentConfirmationToken) {
		this.externalPaymentConfirmationToken = externalPaymentConfirmationToken;
	}

	public String getExternalPaymentConfirmationType() {
		return externalPaymentConfirmationType;
	}

	public void setExternalPaymentConfirmationType(String externalPaymentConfirmationType) {
		this.externalPaymentConfirmationType = externalPaymentConfirmationType;
	}

}
