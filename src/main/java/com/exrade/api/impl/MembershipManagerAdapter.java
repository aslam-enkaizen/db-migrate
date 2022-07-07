package com.exrade.api.impl;

import com.exrade.api.MembershipAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.MemberStatus;
import com.exrade.models.userprofile.security.PlatformRole;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.exception.ExParamException;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.rest.RestParameters.MembershipFields;
import com.exrade.runtime.rest.RestParameters.MembershipFilters;
import com.exrade.runtime.userprofile.*;
import com.exrade.util.ContextHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MembershipManagerAdapter implements MembershipAPI {

	private IMembershipManager manager = new MembershipManager();

	@Override
	public Membership addMembership(ExRequestEnvelope request, String userUUID, String iProfileUUID, String title, String roleName,
			Date expirationDate, List<String> identityDocuments, Double maxNegotiationAmount, boolean agreementSigner, String supervisor) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();
		Security.checkRole(ContextHelper.getMembership(),Arrays.asList(PlatformRole.SUPERADMIN,PlatformRole.MODERATOR));

		IAccountManager accountManager = new AccountManager();
		User user = accountManager.findByUUID(userUUID);
		IProfileManager profileManager = new ProfileManager();
		Profile profile = profileManager.findByUUID(iProfileUUID);

		IMembershipManager membershipManager = new MembershipManager();
		Membership supervisorMembership = membershipManager.findByUUID(supervisor, true);

		return manager.addMembership(user, profile, title, roleName, expirationDate, identityDocuments, maxNegotiationAmount, agreementSigner, supervisorMembership);
	}

	@Override
	public void updateRole(ExRequestEnvelope request, String memberUUID, String roleName) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();
		manager.updateRole(memberUUID, roleName);
	}

	@Override
	public void setDefaultMembership(ExRequestEnvelope request, String memberUUID, boolean iDefaultProfileValue) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();
		manager.setDefaultMembership(memberUUID, iDefaultProfileValue);
	}

	@Override
	public Negotiator findDefaultMembershipByEmail(ExRequestEnvelope request, String iEmail) {
		ContextHelper.initContext(request);
		return manager.findDefaultMembershipByEmail(iEmail);
	}

	@Override
	public Membership findByUUID(ExRequestEnvelope request, String uuid) {
		ContextHelper.initContext(request);
		//Security.checkAuthentication();
		return manager.findByUUID(uuid, false);
	}

	@Override
	public List<Membership> findByProfileUUID(ExRequestEnvelope request, String iProfileUUID) {
		ContextHelper.initContext(request);
		return manager.getProfileMembers(iProfileUUID);
	}

	@Override
	public List<Membership> findByUserUUID(ExRequestEnvelope request, String iUserUUID) {
		ContextHelper.initContext(request);
		return manager.getUserMemberships(iUserUUID);
	}

	@Override
	public Membership getMembershipOf(final ExRequestEnvelope request, final String iUserUUID, final String iProfileUUID) {
		ContextHelper.initContext(request);
		String userUUID = iUserUUID != null ? iUserUUID : ContextHelper.getMembership().getUser().getUuid();
		return manager.getMembershipOf(userUUID, iProfileUUID, false);
	}

	@Override
	public boolean isMembership(ExRequestEnvelope request, String iUserUUID, String iProfileUUID) {
		ContextHelper.initContext(request);
		return manager.isMembership(iUserUUID, iProfileUUID);
	}

	@Override
	public boolean isMembershipOwnedBy(ExRequestEnvelope request, User user, String iMembershipUUID) {
		ContextHelper.initContext(request);
		return manager.isMembershipOwnedBy(user, iMembershipUUID);
	}

	@Override
	public List<Membership> find(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		//Security.checkAuthentication();
//		if (iFilters.containsKey(MembershipFilters.PROFILE))
//			Security.checkMembership("" + iFilters.get(MembershipFilters.PROFILE),
//					Arrays.asList(MemberRole.ADMIN, MemberRole.OWNER));
		QueryFilters filters = getQueryFilter(iFilters);
		return manager.find(filters);
	}

	@Override
	public Membership getOwnerMembership(ExRequestEnvelope request, String iProfileUUID) {
		ContextHelper.initContext(request);
		return manager.getOwnerMembership(iProfileUUID);
	}

	@Override
	public Membership changeCurrentMembership(ExRequestEnvelope request, String newMembershipUUID) {
		ContextHelper.initContext(request);
		if (newMembershipUUID == null) {
			throw new ExParamException(ErrorKeys.PARAM_INVALID, ContextHelper.MEMBERSHIP_UUID);
		}

		IMembershipManager memberProfileManager = new MembershipManager();
		Membership membership = memberProfileManager.findByUUID(newMembershipUUID, true);
		if (membership == null) {
			throw new ExNotFoundException(newMembershipUUID);
		}
		if (!membership.isActive()) {
			throw new ExParamException(ErrorKeys.MEMBERSHIP_NOT_ACTIVE);
		}
		// Store current profile selection
		User user = membership.getUser();
		user.setCurrentMembership(membership);

		IAccountManager accountManager = new AccountManager();
		user = accountManager.updateAccount(user);

		return membership;
	}

	@Override
	public void updateMemberStatus(ExRequestEnvelope request, String iMembershipUUID, MemberStatus iMemberStatus) {
		ContextHelper.initContext(request);
		manager.updateMemberStatus(iMembershipUUID, iMemberStatus);
	}

	private static QueryFilters getQueryFilter(Map<String, String> iFilters) {
		// add sort by date creation
		// filters.put(QueryParameters.SORT,BusinessProfileFields.NAME);

		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putIfNotNull(MembershipFilters.USER, iFilters.get(MembershipFilters.USER));
		filters.putIfNotNull(MembershipFilters.PROFILE, iFilters.get(MembershipFilters.PROFILE));
		filters.putIfNotNull(MembershipFilters.EMAIL, iFilters.get(MembershipFilters.EMAIL));
		filters.putIfNotNull(MembershipFields.IS_DEFAULT_PROFILE,
				iFilters.get(MembershipFields.IS_DEFAULT_PROFILE));
		filters.putIfNotNull(MembershipFilters.NOT_IN_USE, iFilters.get(MembershipFilters.NOT_IN_USE));
		filters.putIfNotNull(MembershipFilters.STATUS, iFilters.get(MembershipFilters.STATUS));
		filters.putIfNotNull(MembershipFilters.ROLENAME, iFilters.get(MembershipFilters.ROLENAME));
		filters.putIfNotNull(QueryParameters.KEYWORDS, iFilters.get(QueryParameters.KEYWORDS));
		filters.putIfNotNull(MembershipFilters.NOT_IN_CONTACT, iFilters.get(MembershipFilters.NOT_IN_CONTACT));

		if (iFilters.containsKey(MembershipFilters.AGREEMENT_SIGNER))
			filters.putIfNotNull(MembershipFilters.AGREEMENT_SIGNER, Boolean.parseBoolean(iFilters.get(MembershipFilters.AGREEMENT_SIGNER)));

		if (iFilters.containsKey(MembershipFilters.INCLUDE_GUEST))
			filters.putIfNotNull(MembershipFilters.INCLUDE_GUEST, Boolean.parseBoolean(iFilters.get(MembershipFilters.INCLUDE_GUEST)));

		if (iFilters.containsKey(MembershipFilters.IS_ACTIVE))
			filters.putIfNotNull(MembershipFilters.IS_ACTIVE, Boolean.parseBoolean(iFilters.get(MembershipFilters.IS_ACTIVE)));

		if(!(filters.containsKey(MembershipFilters.EMAIL)
				|| filters.containsKey(MembershipFilters.USER_NAME))) {
			if(!Security.isPlatformAdministrator() 
					&& !(filters.containsKey(QueryParameters.UUID)
							|| filters.containsKey(MembershipFilters.PROFILE)
							|| filters.containsKey(MembershipFilters.USER)) 
					) {
				filters.put(MembershipFilters.PROFILE, ContextHelper.getMembership().getProfile().getUuid());
			}
		}

		return filters;
	}

	@Override
	public Membership updateMembership(ExRequestEnvelope request,  Membership membership) {
		ContextHelper.initContext(request);
		Security.checkAuthentication();
		return manager.updateMembership(membership);
	}

	@Override
	public Membership createGuestMembership(ExRequestEnvelope request, String firstName, String lastName, String email, String phone, String title) {
		ContextHelper.initContext(request);
		IProfileManager profileManager = new ProfileManager();
		Profile profile = profileManager.findByUUID(ContextHelper.getMembership().getProfile().getUuid());
		return manager.createGuestMembership(firstName, lastName, email, phone, title, profile);
	}

	@Override
	public Membership findGuestMembershipByEmail(ExRequestEnvelope request, String iEmail) {
		ContextHelper.initContext(request);
		return manager.getMembershipByEmail(iEmail, ContextHelper.getMembership().getProfile().getUuid(), true);
	}
}
