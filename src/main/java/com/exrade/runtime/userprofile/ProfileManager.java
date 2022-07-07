package com.exrade.runtime.userprofile;

import com.exrade.Messages;
import com.exrade.core.ExLogger;
import com.exrade.models.event.LogEventType;
import com.exrade.models.payment.IPaymentMethod;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.MemberRole;
import com.exrade.models.userprofile.security.ProfileStatus;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExForbiddenException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.exception.ExParamException;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.event.EventManager;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.payment.PaymentManager;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.userprofile.persistence.ProfilePersistenceManager;
import com.exrade.runtime.userprofile.persistence.query.ProfileQuery;
import com.exrade.util.ContextHelper;
import com.google.common.base.Strings;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ProfileManager implements IProfileManager {
	private static final Logger LOGGER = ExLogger.get();

	public static final String GIFT_CODE = "GIFT";

	private ProfilePersistenceManager persistentManager;

	private SubscriptionManager subscriptionManager = new SubscriptionManager();

	public ProfileManager() {
		this(new ProfilePersistenceManager());
	}

	public ProfileManager(ProfilePersistenceManager iPersistentManager) {
		persistentManager = iPersistentManager;
	}

	@Override
	public Profile create(Profile iProfile){
		setProfileAsPersonalOrBusiness(iProfile);
		if(iProfile.getProfileStatus() == null)
			iProfile.setProfileStatus(ProfileStatus.ACTIVE);

		Profile profile = persistentManager.create(iProfile);
		if(profile.getPlanSubscription() == null)
			subscriptionManager.createDefaultFreeSubscription(profile);

		return findByUUID(profile.getUuid());
	}

	/* (non-Javadoc)
	 * @see com.exrade.runtime.userprofile.IProfileManager#createPersonal(httpcontrollers.userprofile.Profiles.ProfileForm, com.exrade.models.userprofile.User)
	 */
	@Override
	public Profile create(Profile iProfile, User iUser){
		setProfileAsPersonalOrBusiness(iProfile);
		iProfile.setProfileStatus(ProfileStatus.ACTIVE);
		return initProfile(iProfile, iUser);
	}

	/* (non-Javadoc)
	 * @see com.exrade.runtime.userprofile.IProfileManager#update(T)
	 */
	@Override
	public <T extends Profile> T update(T negotiationProfile){
		//Plan should be updated with a dedicated procedure
		setProfileAsPersonalOrBusiness(negotiationProfile);
		return persistentManager.update(negotiationProfile);
	}

//	public Profile updateWithPlanFromBusinessToPersonal(Profile profile, Negotiator negotiator, Plan iPlan, String offerID, String externalClientID, String externalSubscriptionID){
//		updateStatus(profile, ProfileStatus.DISABLED, "Downgrade profile");
//
//		Profile upgradedProfile = null;
//		IMembershipManager membershipManager = new MembershipManager();
//
//		// Since 1 personal account per user is allowed, it not possible to create a personal account for each business plan unsubscribed,
//		// so it's searched if another active profile is already linked with the user
//		List<Membership> memberships = membershipManager.getUserMemberships(negotiator.getUser().getUuid());
//		for(Membership membership : memberships){
//			if(!negotiator.getIdentifier().equals(membership.getIdentifier()) && membership.isProfileActive()){
//				upgradedProfile = membership.getProfile();
//				break;
//			}
//		}
//
//		if(upgradedProfile == null){
//			Profile personalProfile = new Profile();
//			ObjectsUtil.bindFields(negotiator.getProfile(), personalProfile);
//			upgradedProfile = createPersonal(personalProfile, (User)negotiator.getUser());
//		}
//
//		return upgradedProfile;
//	}

//	@SuppressWarnings("unchecked")
//	public <T extends Profile> T updateWithPlan(T iProfile, String iPlanName, String iOfferID, String externalClientID, String externalSubscriptionID, String iBusinessProfileName){
//		Objects.requireNonNull(iProfile.getPlanSubscription());
//		Objects.requireNonNull(iProfile.getPlanSubscription().getPlan());
//
//		Plan plan = PlanManager.getInstance().findByName(iPlanName);
//		if(plan == null) throw new ExParamException(ErrorKeys.PARAM_INVALID,PlanSubscriptionFields.PLAN);
//
////		if (plan.equals(iProfile.getPlanSubscription().getPlan())){
////			throw new ExParamException(ErrorKeys.PLAN_ALREADY_SUBSCRIBED);
////		}
//
//		LOGGER.info("Updating subscription {} - from: {} to: {}", iProfile.getPlanSubscription().getUuid(), iProfile.getPlanSubscription().getPlan().getName(), iPlanName);
//
//		IMembershipManager membershipManager = new MembershipManager();
//		Negotiator negotiator = membershipManager.getOwnerMembership(iProfile.getUuid());
//		Profile upgradedProfile = null;
//		try {
//			// unsubscribe current plan
//			//FIXME: unsubscribe
////			if(iProfile.getPlanSubscription() != null && !Strings.isNullOrEmpty(iProfile.getPlanSubscription().getPaymentToken()) && !GIFT_CODE.equals(iProfile.getPlanSubscription().getPaymentToken()))
////				StripeManager.getInstance().unsubscribePlan(iProfile.getPlanSubscription());
//
//			if(iProfile.isBusinessProfile() && !plan.isBusiness()){ // business to personal
//				upgradedProfile = updateWithPlanFromBusinessToPersonal(iProfile, negotiator, plan, iOfferID, externalClientID, externalSubscriptionID);
//			}
//			else if (!iProfile.isBusinessProfile() && plan.isBusiness()){ // personal to business
//				upgradedProfile = updateWithPlanFromPersonalToBusiness(negotiator, plan, iOfferID, externalClientID, externalSubscriptionID,iBusinessProfileName);
//			}
//			else { // personal to personal or business to business
//				upgradedProfile = updateWithPlan(negotiator, plan, iOfferID, externalClientID, externalSubscriptionID);
//				membershipManager.disableAdditionalMembersFromProfile(upgradedProfile);
//			}
//
//		}
//		catch (Exception e){
//			/*planSubscription = createFreePlansubscription();
//			negotiator.getProfile().setPlanSubscription(planSubscription);
//			Profile profile = persistentManager.update(negotiator.getProfile());
//			planSubscription = profile.getPlanSubscription();*/
//			//TODO: handle upgrade failure
//			LOGGER.error("Error upgrading profile", e);
//			throw new ExException (e);
//		}
//		return (T) upgradedProfile;
//	}

	/* (non-Javadoc)
	 * @see com.exrade.runtime.userprofile.IProfileManager#upgradePersonalToBusiness(com.exrade.models.userprofile.Negotiator, com.exrade.models.userprofile.Plan, java.lang.String, java.lang.String)
	 */
//	@Override
//	public BusinessProfile updateWithPlanFromPersonalToBusiness(Negotiator negotiator,Plan iPlan, String offerID, String externalClientID, String externalSubscriptionID,String businessProfileName){
//
//		if (!(negotiator.getProfile() instanceof Profile)) {
//			throw new ExParamException(ErrorKeys.PLAN_UPGRADE_NOT_ALLOWED);
//		}
//		if (Strings.isNullOrEmpty(businessProfileName)){
//			throw new ExParamException(ErrorKeys.PARAM_INVALID,PlanSubscriptionFields.BUSINESS_PROFILE_NAME);
//		}
//
//		BusinessProfile businessProfile = BusinessProfile.create((Profile)negotiator.getProfile(),businessProfileName);
//		businessProfile = assignPlanSubscription(businessProfile, iPlan.getName(),offerID, externalClientID, externalSubscriptionID, ((Membership)negotiator).getEmail(), (User)negotiator.getUser());
//
//		updateProfileOnMemberships((Profile)negotiator.getProfile(), businessProfile);
//		persistentManager.delete((Profile)negotiator.getProfile());
//		((Membership)negotiator).setProfile(businessProfile);
//		return (BusinessProfile) negotiator.getProfile();
//	}

	/* (non-Javadoc)
	 * @see com.exrade.runtime.userprofile.IProfileManager#updateStatus(com.exrade.models.userprofile.Profile, com.exrade.models.userprofile.security.ProfileStatus, java.lang.String)
	 */
	@Override
//	@Restrict({@Group(PlatformRole.SUPERADMIN), @Group(PlatformRole.MODERATOR)}) //todo commented
	public void updateStatus(Profile profile,ProfileStatus profileStatus,String comment) {
		profile.setProfileStatus(profileStatus);
		if (ProfileStatus.DISABLED.equals(profileStatus)){
			// TODO A DB transaction here is needed since a canceling operation could fail
			cancelNegotiationsProfile(profile,Messages.get("profile.disabled.reason",profile.getUuid()));
			profile.getPlanSubscription().setCancelDate(TimeProvider.now());

			IMembershipManager membershipManager = new MembershipManager();
			membershipManager.disableAdditionalMembersFromProfile(profile);
		}
		else if (ProfileStatus.ACTIVE.equals(profileStatus)){
			IMembershipManager membershipManager = new MembershipManager();
			membershipManager.enableOwnerMembership(profile);
		}
		persistentManager.update(profile);
		String eventLog = "Profile status has been updated to: "+profileStatus.name()+(Strings.isNullOrEmpty(comment) ? "." : ". Comment: "+comment);
		EventManager.getInstance().create(ContextHelper.getMembership(),LogEventType.PROFILE_STATUS,profile.getUuid(),eventLog);
	}

	/* (non-Javadoc)
	 * @see com.exrade.runtime.userprofile.IProfileManager#findByUUID(java.lang.String)
	 */
	@Override
	public Profile findByUUID(String uuid){
		return persistentManager.readObjectByUUID(Profile.class, uuid);
	}

	/* (non-Javadoc)
	 * @see com.exrade.runtime.userprofile.IProfileManager#getProfiles(com.exrade.platform.persistence.query.QueryFilters)
	 */
	@Override
	public List<Profile> getProfiles(QueryFilters iFilters){
		List<Profile> profiles = persistentManager.listObjects(new ProfileQuery(), iFilters);
		return profiles;
	}

	@Override
	public void addPaymentMethod(String iProfileUUID, IPaymentMethod iPaymentMethod) {
		Security.checkAddPaymentMethodPermission();

		Profile profile = findByUUID(iProfileUUID);
		if(profile.getPaymentMethods() != null){
			for(IPaymentMethod existingPaymentMethod : profile.getPaymentMethods()){
				if(existingPaymentMethod.getPaymentType() == iPaymentMethod.getPaymentType()
						&& existingPaymentMethod.getIdentifier().equals(iPaymentMethod.getIdentifier())){
					throw new ExParamException(ErrorKeys.PARAM_DUPLICATE, "email");
				}
			}
			profile.getPaymentMethods().add(iPaymentMethod);
		}
		else{
			List<IPaymentMethod> paymentMethods = new ArrayList<IPaymentMethod>();
			paymentMethods.add(iPaymentMethod);
			profile.setPaymentMethods(paymentMethods);
		}
		update(profile);
	}

	@Override
	public void updatePaymentMethod(String iProfileUUID, IPaymentMethod iPaymentMethod) {
		Security.checkAddPaymentMethodPermission();

		Profile profile = findByUUID(iProfileUUID);
		for(IPaymentMethod paymentMethod : profile.getPaymentMethods()){
			if(paymentMethod.getUuid().equals(iPaymentMethod.getUuid())){
				profile.getPaymentMethods().remove(paymentMethod);
				profile.getPaymentMethods().add(iPaymentMethod);
				update(profile);
				return;
			}
		}
		throw new ExNotFoundException(iPaymentMethod.getUuid());
	}

	@Override
	public List<IPaymentMethod> getPaymentMethods(String iProfileUUID) {
		Profile profile = findByUUID(iProfileUUID);
		return profile.getPaymentMethods();
	}

	@Override
	public void deletePaymentMethod(String iProfileUUID, String iPaymentMethodUUID) {
		Security.checkAddPaymentMethodPermission();

		if(PaymentManager.hasAssociatedNegotiation(iPaymentMethodUUID))
			throw new ExForbiddenException(ErrorKeys.PAYMENT_METHOD_DELETE_FORBIDDEN);

		Profile profile = findByUUID(iProfileUUID);
		for(IPaymentMethod paymentMethod : profile.getPaymentMethods()){
			if(paymentMethod.getUuid().equals(iPaymentMethodUUID)){
				profile.getPaymentMethods().remove(paymentMethod);
				break;
			}
		}
		update(profile);
	}

	@Override
	public IPaymentMethod getPaymentMethod(String iProfileUUID, String iPaymentMethodUUID) {
		Profile profile = findByUUID(iProfileUUID);
		for(IPaymentMethod paymentMethod : profile.getPaymentMethods()){
			if(paymentMethod.getUuid().equals(iPaymentMethodUUID)){
				return paymentMethod;
			}
		}
		throw new ExNotFoundException(iPaymentMethodUUID);
	}

//	private Profile updateWithPlan(Negotiator negotiator,Plan iPlan, String offerID, String externalClientID, String externalSubscriptionID){
//		Profile profile = (Profile)negotiator.getProfile();
//		profile = assignPlanSubscription(profile,iPlan.getName(),offerID,externalClientID, externalSubscriptionID,
//				((Membership)negotiator).getEmail(), (User)negotiator.getUser());
//		return profile;
//	}

	@SuppressWarnings("unchecked")
	private <T extends Profile> T initProfile(T bnProfile, User iUser){
		//T profile = assignPlanSubscription(bnProfile,planName,offerID,externalClientID, externalSubscriptionID,email,iUser);
		IMembershipManager membershipManager = new MembershipManager();
		Membership membership = membershipManager.addMembership(iUser, bnProfile, MemberRole.OWNER.toString());// assignOwnerMembership(profile,iUser);
		return (T) membership.getProfile();
	}

//	private <T extends Profile> T assignPlanSubscription(T profile, String planName, String offerID, String externalClientID, String externalSubscriptionID,String email,User user) {
//		Plan plan = PlanManager.getInstance().findByName(planName);
//		PlanSubscription planSubscription = subscriptionManager.createSubscription(profile, plan, externalClientID, externalSubscriptionID);
//
//		profile.setPlanSubscription(planSubscription);
//
//		profile = persistentManager.update(profile);
//		return profile;
//	}

	private void updateProfileOnMemberships(Profile oldProfile, Profile newProfile){
		IMembershipManager membershipManager = new MembershipManager();
		List<Membership> memberships = membershipManager.getProfileMembers(oldProfile.getUuid());

		for (Membership membership : memberships) {
			membership.setProfile(newProfile);
			persistentManager.update(membership);
		}
	}

	private void cancelNegotiationsProfile(Profile profile,String iReason) {
		IMembershipManager membershipManager = new MembershipManager();

		List<Membership> memberships = membershipManager.getProfileMembers(profile.getUuid());
		// Put in cancel status running negotiations
		INegotiationManager negotiationManager = new NegotiationManager();
		for (Membership membership : memberships) {
			negotiationManager.cancelByMembership(membership,iReason);
		}
	}

	private void setProfileAsPersonalOrBusiness(Profile profile) {
		if (!TextUtils.isBlank(profile.getSubdomain()))
			//checking profile permission
			Security.hasAccessPermission(Security.ProfilePermissions.PROFILES_SUBDOMAIN);


		profile.setBusinessProfile(!Strings.isNullOrEmpty(profile.getName()));
	}
}

