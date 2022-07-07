package com.exrade.models.userprofile.usage;

import com.exrade.models.userprofile.IProfile;
import com.exrade.platform.persistence.BaseEntityUUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class UsageSummary extends BaseEntityUUID {

	@JsonIgnore
	private IProfile profile;
	private List<UsageDetail> usageDetails=new ArrayList<UsageDetail>();

	public UsageSummary(){}

	public UsageSummary(IProfile profile){
		setProfile(profile);
	}

	public List<UsageDetail> getUsageDetails() {
		return usageDetails;
	}

	public void setUsageDetails(List<UsageDetail> usageDetails) {
		this.usageDetails = usageDetails;
	}

	public IProfile getProfile() {
		return profile;
	}

	public void setProfile(IProfile profile) {
		this.profile = profile;
	}

	public String getProfileUUID() {
		return getProfile().getUuid();
	}

	public String getPlanTitle() {
		return getProfile().getPlanSubscription().getPlan().getTitle();
	}

	public String getPlanUUID() {
		return getProfile().getPlanSubscription().getPlanUUID();
	}

	public String getSubscriptionUUID() {
		return getProfile().getPlanSubscription().getUuid();
	}
}
