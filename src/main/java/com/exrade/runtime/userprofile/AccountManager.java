package com.exrade.runtime.userprofile;

import com.exrade.Messages;
import com.exrade.core.ExLogger;
import com.exrade.models.Role;
import com.exrade.models.activity.Verb;
import com.exrade.models.i18n.ExLang;
import com.exrade.models.invitations.InvitationStatus;
import com.exrade.models.invitations.MemberInvitation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.*;
import com.exrade.models.userprofile.TokenAction.Type;
import com.exrade.models.userprofile.security.AccountStatus;
import com.exrade.models.userprofile.security.PlatformRole;
import com.exrade.platform.exception.*;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.providers.password.UsernamePasswordAuthUser;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.filemanagement.FileStorageProvider;
import com.exrade.runtime.invitation.IMemberInvitationManager;
import com.exrade.runtime.invitation.INegotiationInvitationManager;
import com.exrade.runtime.invitation.MemberInvitationManager;
import com.exrade.runtime.invitation.NegotiationInvitationManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.UserNotificationEvent;
import com.exrade.runtime.rest.RestParameters.LinkedAccountFields;
import com.exrade.runtime.rest.RestParameters.UserFields;
import com.exrade.runtime.security.RoleManager;
import com.exrade.runtime.userprofile.persistence.AccountPersistence;
import com.exrade.runtime.userprofile.persistence.query.AccountQuery;
import com.exrade.runtime.userprofile.providers.ExLocalIdentity;
import com.exrade.runtime.userprofile.providers.ExSocialIdentity;
import com.exrade.runtime.userprofile.providers.local.LocalWalletProvider;
import com.exrade.runtime.userprofile.providers.password.ExUsernamePasswordAuthUser;
import com.exrade.user.*;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExHelpers;
import com.exrade.util.ObjectsUtil;
import com.google.common.base.Strings;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class AccountManager implements IAccountManager {

	private AccountPersistence userProfilePersistenceManager;
	private RoleManager roleManager;
	private IMemberInvitationManager membershipInvitationManager;
	private IMembershipManager membershipManager;
	private INegotiationInvitationManager negotiationInvitationManager;
	private IProfileManager profileManager;
	private TokenActionManager tokenActionManager;
	private LinkedAccountManager linkedAccountManager;
	private SubscriptionManager subscriptionManager = new SubscriptionManager();
	private NotificationManager notificationManager = new NotificationManager();
	private LocalWalletProvider localWalletProvider = new LocalWalletProvider();

	public AccountManager() {
		this(new AccountPersistence());
		roleManager = new RoleManager();
		membershipInvitationManager = new MemberInvitationManager();
		membershipManager = new MembershipManager();
		negotiationInvitationManager = new NegotiationInvitationManager();
		profileManager = new ProfileManager();
		tokenActionManager = new TokenActionManager();
		linkedAccountManager = new LinkedAccountManager();
	}

	public AccountManager(AccountPersistence iUserProfilePersistenceManager) {
		userProfilePersistenceManager = iUserProfilePersistenceManager;
	}

	@Override
	public User findByUUID(String iUUID) {
		return userProfilePersistenceManager.findByUUID(iUUID);
	}

	private User getAuthUserFind(final AuthUserIdentity identity) {
		QueryFilters filters = QueryFilters.create(UserFields.ACCOUNT_STATUS,AccountStatus.ACTIVE.name()).
				filter(LinkedAccountFields.PROVIDER_KEY, identity.getProvider()).
				filter(LinkedAccountFields.PROVIDER_USER_ID, identity.getId());
		return userProfilePersistenceManager.readObject(new AccountQuery(), filters);
	}

	@Override
	public User findByAuthUserIdentity(final AuthUserIdentity identity) {
		if (identity == null) {
			return null;
		}
		if (identity instanceof UsernamePasswordAuthUser) {
			return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
		} else {
			return getAuthUserFind(identity);
		}
	}

	@Override
	public User findByUsernamePasswordIdentity(final UsernamePasswordAuthUser identity) {
		QueryFilters filters = QueryFilters.create(UserFields.USER_NAME, identity.getEmail());
		filters.put(LinkedAccountFields.PROVIDER_KEY,identity.getProvider());
		User user = userProfilePersistenceManager.readObject(new AccountQuery(),filters);
		return user;
	}

	@Override
	public User findByUsername(String iUsername) {
		if(!Strings.isNullOrEmpty(iUsername))
			return userProfilePersistenceManager.findByUsername(iUsername);
		else
			return null;
	}

	@Override
	public List<User> find(QueryFilters iFilters){
		List<User> users = userProfilePersistenceManager.listObjects(new AccountQuery(), iFilters);
		return users;
	}

	@Override
	public User create(User user) {
		user.setAccountStatus(AccountStatus.TO_VALIDATE);
		Role platFormRole = roleManager.findByName(PlatformRole.MEMBER);
		user.setPlatformRole((PlatformRole) platFormRole);
		return userProfilePersistenceManager.create(user);
	}

	@Override
	public User create(final AuthUser authUser) {
		return create(authUser, AccountStatus.TO_VALIDATE);
	}

	@Override
	public User create(final AuthUser authUser,AccountStatus status) {


		// Creation of user account
		User user = new User();
		LinkedAccount linkedAccount = LinkedAccount.create(authUser);
		user.getLinkedAccounts().add(linkedAccount);
		Role platFormRole = roleManager.findByName(PlatformRole.MEMBER);
		user.setPlatformRole((PlatformRole) platFormRole);

		if (authUser instanceof EmailIdentity) {
			final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			checkIdentityExist(identity);
			user.setEmail(identity.getEmail().toLowerCase());
			user.setUserName(identity.getEmail());
		}

		updateUserFields(user, authUser);

		if (status != null){
			user.setAccountStatus(status);
		}
		user = userProfilePersistenceManager.create(user);
		// A reload is needed to get the ID of the objects valorized since the objects are new
		user = userProfilePersistenceManager.findByUUID(user.getUuid());
		membershipInvitationManager.updateInvitationsForNewUser(user);

		Profile profile = buildProfile(user, authUser);

		if(profile == null) {
			userProfilePersistenceManager.delete(user);
			throw new ExException("Error creating user profile!");
		}

		Membership membership = membershipManager.getMembershipOf(user.getUuid(), profile.getUuid(), false);
		user.setDefaultMembership(membership);
		user.setCurrentMembership(membership);
		user = userProfilePersistenceManager.update(user);

		negotiationInvitationManager.updateInvitationsForNewUser(membership);

		return user;
	}

	@Override
	public User convertFromGuest(final AuthUser authUser, AccountStatus status) {
		// retrieving of user account
		User user = this.findByUsername(((EmailIdentity) authUser).getEmail());

		LinkedAccount linkedAccount = LinkedAccount.create(authUser);
		user.getLinkedAccounts().add(linkedAccount);

		Role platFormRole = roleManager.findByName(PlatformRole.MEMBER);
		user.setPlatformRole((PlatformRole) platFormRole);

		updateUserFields(user, authUser);

		if (status != null){
			user.setAccountStatus(status);
		}

		user = userProfilePersistenceManager.update(user);
		// A reload is needed to get the ID of the objects valorized since the objects are new
		//user = userProfilePersistenceManager.findByUUID(user.getUuid());
		membershipInvitationManager.updateInvitationsForNewUser(user);

		Profile profile = buildProfile(user, authUser);

		if(profile == null) {
			//userProfilePersistenceManager.delete(user);
			throw new ExException("Error creating user profile!");
		}

		Membership membership = membershipManager.getMembershipOf(user.getUuid(), profile.getUuid(), false);
		user.setDefaultMembership(membership);
		user.setCurrentMembership(membership);
		user = userProfilePersistenceManager.update(user);

		negotiationInvitationManager.updateInvitationsForNewUser(membership);

		return user;
	}

	private void updateUserFields(User user, final AuthUser authUser) {
		if (authUser instanceof FirstLastNameIdentity) {
			user.setFirstName(((FirstLastNameIdentity) authUser).getFirstName());
			user.setLastName(((FirstLastNameIdentity) authUser).getLastName());
		}

		if (authUser instanceof ExLocalIdentity) {
			user.setTimezone(((ExLocalIdentity)authUser).getTimezone() != null ? ((ExLocalIdentity)authUser).getTimezone() : "UTC");
			user.setLanguage(((ExLocalIdentity)authUser).getLanguage() != null ? ((ExLocalIdentity)authUser).getLanguage() : ExLang.ENGLISH.getCode());
		}
		else {
			user.setTimezone("UTC");
			user.setLanguage(ExLang.ENGLISH.getCode());
		}

		if (authUser instanceof PicturedIdentity) {
			String pictureStoreUUID = null;
			if ( ((PicturedIdentity)authUser).getPicture() != null) {
				try {
					URL url = new URL(((PicturedIdentity)authUser).getPicture());
					// Save as attachment and store file in DB
					pictureStoreUUID = FileStorageProvider.getFileStorageController().storeFile(url,ObjectsUtil.generateUniqueID(), new HashMap<String, Object>());
					user.setAvatar(pictureStoreUUID);
				} catch (IOException e) {
					ExLogger.get().warn("Retrieving of social avatar failed",e);
				}
			}
		}
	}

	private Profile buildProfile(User user, final AuthUser authUser) {
		Profile profile = null;

		String planName = ((ExLocalIdentity)authUser).getPlanName();
		String profileUUID = ((ExLocalIdentity)authUser).getProfileUUID();

		if(!Strings.isNullOrEmpty(profileUUID)) {
			// check invitation
			MemberInvitation invitation = membershipInvitationManager.getMemberInvitation(user, profileUUID);
			if(invitation != null && invitation.getInvitationStatus() == InvitationStatus.PENDING) {
				membershipManager.addMembership(invitation);
				return profileManager.findByUUID(profileUUID);
			}
		}
		else {
			// Creation of negotiation profile, by default is assigned the free plan
			profile = new Profile();

			if (authUser instanceof ExLocalIdentity) {
				profile.setCountry(((ExLocalIdentity)authUser).getCountry() != null ? ((ExLocalIdentity)authUser).getCountry().toUpperCase() : null);
				profile.setAddress(((ExLocalIdentity)authUser).getAddress());
				profile.setPhone(((ExLocalIdentity)authUser).getPhone());
				profile.setCity(((ExLocalIdentity)authUser).getCity());
				profile.setPostcode(((ExLocalIdentity)authUser).getPostcode());
				profile.setVat(((ExLocalIdentity)authUser).getVat());
			}

			if (authUser instanceof ExSocialIdentity) {
				profile.setDescription( ((ExSocialIdentity)authUser).getDescription() );
				profile.setInterests( ((ExSocialIdentity)authUser).getInterests() );
				profile.setWebsite( ((ExSocialIdentity)authUser).getWebsite() );
			}

			try {
				if(ExConfiguration.getPropertyAsBoolean("wallet-auto-generate")) {
					String address = localWalletProvider.getWalletAddress();
					profile.setWalletAddress(address);
				}
			}
			catch(Exception ex) {
				ExLogger.get().warn("Wallet creation failed", ex);
			}

			String businessName = ((ExLocalIdentity)authUser).getBusinessName() != null ? ((ExLocalIdentity)authUser).getBusinessName() : "";
			profile.setName(businessName);
			profile = profileManager.create(profile, user);

			Plan plan = null;
			if(!Strings.isNullOrEmpty(planName)) {
				plan = PlanManager.getInstance().findByName(planName);
			}
			else {
				plan = PlanManager.getInstance().findByName(ExConfiguration.getStringProperty("DEFAULT_SUBSCRIPTIN_PLAN"));
			}

			if(plan == null)
				return null;

			subscriptionManager.createSubscription(profile, plan, null, null, null);
			//profile.setPlanSubscription(subscription);
			profile = profileManager.findByUUID(profile.getUuid()); //reload updated object

		}
		return profile;
	}

	@Override
	public User updateAccount(User iUser){
		return userProfilePersistenceManager.update(iUser);
	}

	@Override
	public User updateRole(String userUUID,String iPlatformRole){
		User userToUpdate = findByUUID(userUUID);
		if (userToUpdate == null) {
			throw new ExNotFoundException("user");
		}

		// right now only superadmin user should perform update of user role
		Security.checkPlatformRole((User)ContextHelper.getMembership().getUser(),PlatformRole.SUPERADMIN);
		Role role = roleManager.findByName(iPlatformRole);
		userToUpdate.setPlatformRole((PlatformRole)role);
		return userProfilePersistenceManager.update(userToUpdate);
	}

	private void checkIdentityExist(AuthUserIdentity iIdentity){
		if (iIdentity instanceof EmailIdentity) {
			EmailIdentity identity = (EmailIdentity) iIdentity;
			if (userProfilePersistenceManager.findByUsername(identity.getEmail().toLowerCase())!= null){
				throw new ExDuplicateKeyException(ErrorKeys.PARAM_DUPLICATE,identity.getEmail());
			}
		}
	}

	@Override
	public void changePassword(User user,String newPassword,String oldPassword,final boolean create) {
		UsernamePasswordAuthUser authUser = new ExUsernamePasswordAuthUser(newPassword);
		LinkedAccount linkedAccount = linkedAccountManager.getAccountByProvider(user,authUser.getProvider());
		if (linkedAccount == null) {
			if (create) {
				linkedAccount = LinkedAccount.create(authUser);
			} else {
				throw new ExException(ErrorKeys.AUTHENTICATOR_NOTPASSWORD_ENABLED);
			}
		}

		if (!ExHelpers.checkHash(linkedAccount.getProviderUserId(),oldPassword)) {
			throw new ExParamException(ErrorKeys.PARAM_INVALID,Messages.get("oldPassword"));
		}

		updatePassword(authUser, linkedAccount);
		notificationManager.process(new UserNotificationEvent(
				NotificationType.USER_PASSWORD_CHANGED, user));
		ActivityLogger.log(ContextHelper.getMembership(), Verb.CHANGE,user, Arrays.asList(user.getCurrentMembership()));
	}

	private void updatePassword(final UsernamePasswordAuthUser authUser,
			LinkedAccount linkedAccount) {
		linkedAccount.setProviderUserId(authUser.getHashedPassword());
		userProfilePersistenceManager.update(linkedAccount);
	}

	@Override
	public void resetPassword(String resetToken, String newPassword) {
		// You might want to wrap this into a transaction
		final TokenAction ta = tokenIsValid(resetToken, Type.PASSWORD_RESET);
		if (ta == null) {
			throw new ExParamException(ErrorKeys.AUTHENTICATOR_TOKEN_INVALID);
		}

		User user = ta.getTargetUser();
		UsernamePasswordAuthUser authUser = new ExUsernamePasswordAuthUser(newPassword);
		LinkedAccount linkedAccount = linkedAccountManager.getAccountByProvider(user, authUser.getProvider());
		if (linkedAccount == null){
			throw new ExException(ErrorKeys.AUTHENTICATOR_NOTPASSWORD_ENABLED);
		}
		this.updatePassword(authUser,linkedAccount);
		tokenActionManager.deleteByUser(user, Type.PASSWORD_RESET);
	}

	@Override
//	@Restrict(value=@Group({PlatformRole.SUPERADMIN})) todo
	public void updateAccountStatus(String userAccountUUID,AccountStatus iAccountStatus){
		User user = findByUUID(userAccountUUID);
		user.setAccountStatus(iAccountStatus);
		userProfilePersistenceManager.update(user);
	}

	private TokenAction tokenIsValid(final String iTokenCode, final Type type) {
		TokenAction token = null;
		if (iTokenCode != null && !iTokenCode.trim().isEmpty()) {
			final TokenAction ta = tokenActionManager.findByToken(iTokenCode,type.name());
			if (ta != null && ta.isValid()) {
				token = ta;
			}
		}

		return token;
	}

	@Override
	public boolean existsAccount(QueryFilters iFilters) {
		List<User> users = find(iFilters);
		if(users != null && users.size() > 0)
			return true;
		return false;
	}
}
