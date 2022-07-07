package com.exrade.api.impl;

import com.exrade.api.ProfileAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.payment.IPaymentMethod;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.ProfileStatus;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExParamException;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.rest.RestParameters.ProfileFields;
import com.exrade.runtime.userprofile.IProfileManager;
import com.exrade.runtime.userprofile.ProfileManager;
import com.exrade.util.ContextHelper;

import java.util.List;
import java.util.Map;

public class ProfileManagerAdapter implements ProfileAPI {

	private IProfileManager manager = new ProfileManager();

	@Override
	public Profile create(ExRequestEnvelope request,
			Profile iProfile) {
		ContextHelper.initContext(request);

		Profile profile = null;
		if(Security.isPlatformAdministrator()) {
			profile = manager.create(iProfile);
		}
		else {
			Security.checkAuthentication();
			profile = manager.create(iProfile, (User)ContextHelper.getMembership().getUser());
		}
		return profile;
	}

	@Override
	public Profile findByUUID(ExRequestEnvelope request, String uuid) {
		ContextHelper.initContext(request);
		return manager.findByUUID(uuid);
	}

	@Override
	public List<Profile> getProfiles(ExRequestEnvelope request,
			Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.put(QueryParameters.SORT,ProfileFields.NAME);
		filters.putAll(getProfileFilters(iFilters));
		return manager.getProfiles(filters);
	}

	@Override
	//@Pattern(Permissions.MANAGE_BUSINESS_PROFILE)
	public <T extends Profile> T update(ExRequestEnvelope request,
			T profile) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();
		return manager.update(profile);
	}

	@Override
	public void updateStatus(ExRequestEnvelope request, Profile profile,
			ProfileStatus profileStatus, String comment) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();
		manager.updateStatus(profile, profileStatus, comment);
	}

	private static QueryFilters getProfileFilters(Map<String, String> iFilters) {
		QueryFilters filters = QueryFilters.create(iFilters);
		String active = iFilters.get(ProfileFields.ACTIVE);
		// by default select only active profiles
		filters.put(ProfileFields.ACTIVE, active != null ? active : true);
		filters.putIfNotNull(QueryParameters.KEYWORDS, iFilters.get(QueryParameters.KEYWORDS));
		//filters.putIfNotNull(CompanyFilters.CREATOR_UUID, request().getQueryString(CompanyFilters.CREATOR_UUID));
		filters.putIfNotNull(ProfileFields.COUNTRY, iFilters.get(ProfileFields.COUNTRY));
		filters.putIfNotNull(ProfileFields.CITY, iFilters.get(ProfileFields.CITY));
		filters.putIfNotNull(ProfileFields.COMPETENCES, iFilters.get(ProfileFields.COMPETENCES));
		filters.putIfNotNull(ProfileFields.NACE, iFilters.get(ProfileFields.NACE));
		filters.putIfNotNull(ProfileFields.NAME, iFilters.get(ProfileFields.NAME));
		filters.putIfNotNull(ProfileFields.VAT, iFilters.get(ProfileFields.VAT));
		filters.putIfNotNull(ProfileFields.LEGAL_EMAIL, iFilters.get(ProfileFields.LEGAL_EMAIL));
		filters.putIfNotNull(ProfileFields.SUBDOMAIN, iFilters.get(ProfileFields.SUBDOMAIN));

		if(!(filters.containsKey(QueryParameters.UUID)
				|| filters.containsKey(ProfileFields.SUBDOMAIN)
				|| Security.isProfileAdministrator())) {
			throw new ExParamException(ErrorKeys.BADREQUEST_MISSING_PARAMETER);
		}

		return filters;
	}

	@Override
	public void addPaymentMethod(ExRequestEnvelope request, String iProfileUUID, IPaymentMethod paymentMethod) {
		ContextHelper.initContext(request);
		manager.addPaymentMethod(iProfileUUID, paymentMethod);
	}

	@Override
	public void updatePaymentMethod(ExRequestEnvelope request, String iProfileUUID, IPaymentMethod iPaymentMethod) {
		ContextHelper.initContext(request);
		manager.updatePaymentMethod(iProfileUUID, iPaymentMethod);
	}

	@Override
	public void deletePaymentMethod(ExRequestEnvelope request, String iProfileUUID, String iPaymentMethodUUID) {
		ContextHelper.initContext(request);
		manager.deletePaymentMethod(iProfileUUID, iPaymentMethodUUID);
	}

	@Override
	public List<IPaymentMethod> getPaymentMethods(ExRequestEnvelope request, String iProfileUUID) {
		ContextHelper.initContext(request);
		return manager.getPaymentMethods(iProfileUUID);
	}

	@Override
	public IPaymentMethod getPaymentMethod(ExRequestEnvelope request, String iProfileUUID, String iPaymentMethodUUID) {
		ContextHelper.initContext(request);
		return manager.getPaymentMethod(iProfileUUID, iPaymentMethodUUID);
	}
}
