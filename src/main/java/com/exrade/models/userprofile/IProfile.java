package com.exrade.models.userprofile;

import com.exrade.models.payment.IPaymentMethod;
import com.exrade.models.userprofile.security.ProfileStatus;

import java.util.List;
import java.util.Map;


public interface IProfile {
	public String getId();

	public String getUuid();

	public String getPhone();

	public String getDescription();

	public String getAddress();

	public String getPostcode();

	public String getCity();

	public String getCountry();

	public String getWebsite();

	public ProfileStatus getProfileStatus();

	public List<IPaymentMethod> getPaymentMethods();

	public boolean isPublicProfile();

	public boolean isBusinessProfile();

	public Map<String, Object> getCustomFields();

	public String getName();

	public String getLogo();

	public String getCompetences();

	public String getVat();

	public String getNace();

	public String getLegalEmail();

	public boolean isDomainVerified();

	public boolean isIdentityVerified();

	public String getAgreementTemplate();

	public String getSubdomain();

	public String getWalletAddress();

	public PlanSubscription getPlanSubscription();
}
