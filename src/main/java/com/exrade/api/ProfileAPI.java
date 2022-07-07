package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.payment.IPaymentMethod;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.security.ProfileStatus;

import java.util.List;
import java.util.Map;

public interface ProfileAPI {

	Profile create(ExRequestEnvelope request, Profile iProfile);

	Profile findByUUID(ExRequestEnvelope request, String uuid);

	List<Profile> getProfiles(ExRequestEnvelope request, Map<String, String> iFilters);

	<T extends Profile> T update(ExRequestEnvelope request, T negotiationProfile);
	
	void updateStatus(ExRequestEnvelope request, Profile profile, ProfileStatus profileStatus,
			String comment);
	
	void addPaymentMethod(ExRequestEnvelope request, String iProfileUUID, IPaymentMethod paymentMethod);
	
	void updatePaymentMethod(ExRequestEnvelope request, String iProfileUUID, IPaymentMethod iPaymentMethod);
	
	void deletePaymentMethod(ExRequestEnvelope request, String iProfileUUID, String iPaymentMethodUUID);
	
	List<IPaymentMethod> getPaymentMethods(ExRequestEnvelope request, String iProfileUUID);
	
	IPaymentMethod getPaymentMethod(ExRequestEnvelope request, String iProfileUUID, String iPaymentMethodUUID);

}