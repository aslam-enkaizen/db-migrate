package com.exrade.runtime.userprofile;

import com.exrade.models.payment.IPaymentMethod;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.ProfileStatus;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IProfileManager {

	Profile create(Profile iProfile);
	
	Profile create(Profile iProfile, User iUser);

	Profile findByUUID(String uuid);

	List<Profile> getProfiles(QueryFilters iFilters);

	<T extends Profile> T update(T negotiationProfile);

	//<T extends Profile> T updateWithPlan(T profile, String planName, String offerID, String externalClientID, String externalSubscriptionID, String businessProfileName);

//	BusinessProfile updateWithPlanFromPersonalToBusiness(Negotiator negotiator,
//			Plan iPlan, String offerID, String externalClientID, String externalSubscriptionID, String businessProfileName);

	void updateStatus(Profile profile, ProfileStatus profileStatus,
			String comment);

	void addPaymentMethod(String iProfileUUID, IPaymentMethod iPaymentMethod);
	
	void updatePaymentMethod(String iProfileUUID, IPaymentMethod iPaymentMethod);
	
	void deletePaymentMethod(String iProfileUUID, String iPaymentMethodUUID);
	
	List<IPaymentMethod> getPaymentMethods(String iProfileUUID);
	
	IPaymentMethod getPaymentMethod(String iProfileUUID, String iPaymentMethodUUID);

//	Profile updateWithPlanFromBusinessToPersonal(Profile profile, Negotiator negotiator, Plan iPlan, String offerID,
//			String externalClientID, String externalSubscriptionID);

}