package com.exrade.runtime.userprofile;

import com.exrade.core.ExLogger;
import com.exrade.models.Role;
import com.exrade.models.invitations.MemberInvitation;
import com.exrade.models.userprofile.*;
import com.exrade.models.userprofile.security.MemberRole;
import com.exrade.models.userprofile.security.MemberStatus;
import com.exrade.models.userprofile.security.PlatformRole;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.platform.security.Security.ProfilePermissions;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.rest.RestParameters.MembershipFilters;
import com.exrade.runtime.security.RoleManager;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.userprofile.persistence.MemberProfilePersistence;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.google.common.base.Strings;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MembershipManager implements IMembershipManager {

    private MemberProfilePersistence memberProfilePersistent;

    public MembershipManager() {
        this(new MemberProfilePersistence());
    }

    public MembershipManager(MemberProfilePersistence iMemberProfilePersistent) {
        memberProfilePersistent = iMemberProfilePersistent;
    }

    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#addMembership(com.exrade.models.userprofile.User, com.exrade.models.userprofile.Profile, java.lang.String)
     */
    @Override
    public Membership addMembership(User user, Profile profile, String roleName) {
        if (getMembershipOf(user.getUuid(), profile.getUuid(), true) != null)
            throw new ExException("Membership already exists!");

        Role role = new RoleManager().findByName(roleName);

        Membership membership = Membership.createMembership(user, profile, (MemberRole) role);
        membership = memberProfilePersistent.create(membership);

        ExLogger.get().info("Added membership. MembershipUUID: {}, ProfileUUID: {}, UserUUID: {}", membership.getUuid(), profile.getUuid(), user.getUuid());
        return membership;
    }

    @Override
    public Membership addMembership(User user, Profile profile, String title, String roleName, Date expirationDate,
                                    List<String> authorizationDocuments, Double maxNegotiationAmount, boolean agreementSigner, Membership supervisor) {
        if (getMembershipOf(user.getUuid(), profile.getUuid(), true) != null)
            throw new ExException("Membership already exists!");

        Role role = new RoleManager().findByName(roleName);

        Membership membership = Membership.createMembership(user, profile, (MemberRole) role);
        membership.setTitle(title);
        membership.setAgreementSigner(agreementSigner);
        membership.setExpirationDate(expirationDate);
        membership.setAuthorizationDocuments(authorizationDocuments);
        membership.setMaxNegotiationAmount(maxNegotiationAmount);
        membership.setSupervisor(supervisor);
        membership = memberProfilePersistent.create(membership);

        ExLogger.get().info("Added membership. MembershipUUID: {}, ProfileUUID: {}, UserUUID: {}", membership.getUuid(), profile.getUuid(), user.getUuid());
        return membership;
    }

    @Override
    public Membership addMembership(MemberInvitation invitation) {
        return addMembership(invitation.getInvitedUser(), invitation.getInvitedProfile(), invitation.getTitle(), invitation.getRoleName(), invitation.getMembershipExpirationDate(),
                invitation.getAuthorizationDocuments(), invitation.getMaxNegotiationAmount(), invitation.isAgreementSigner(), invitation.getSupervisor());
    }

    @Override
    public Membership updateMembership(MemberInvitation invitation) {
        Membership membership = this.getMembershipOf(invitation.getInvitedUserUUID(), invitation.getInvitedProfileUUID(), true);
        checkRoleChangePermission(membership.getRole().getName(), invitation.getRoleName());

        Role role = new RoleManager().findByName(invitation.getRoleName());
        membership.setTitle(invitation.getTitle());
        membership.setAgreementSigner(invitation.isAgreementSigner());
        membership.setExpirationDate(invitation.getMembershipExpirationDate());
        membership.setAuthorizationDocuments(invitation.getAuthorizationDocuments());
        membership.setMaxNegotiationAmount(invitation.getMaxNegotiationAmount());
        membership.setSupervisor(invitation.getSupervisor());
        membership.setGuest(false);
        membership.setRole((MemberRole) role);
        membership = memberProfilePersistent.update(membership);

        ExLogger.get().info("Updated membership from invitation. MembershipUUID: {}, InvitationUUID: {}", membership.getUuid(), invitation.getUuid());
        return membership;
    }

    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#updateRole(java.lang.String, java.lang.String)
     */
    @Override
    public void updateRole(String memberUUID, String roleName) {
        Membership member = this.findByUUID(memberUUID, true);
        checkPermission(member);
        checkRoleChangePermission(member.getRole().getName(), roleName);

        Role role = new RoleManager().findByName(roleName);
        member.setRole((MemberRole) role);
        memberProfilePersistent.update(member);
        ExLogger.get().info("Updated membership role. MembershipUUID: {}, Status: {}", member.getUuid(), roleName);
    }

    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#setDefaultMembership(java.lang.String, boolean)
     */
    @Override
    public void setDefaultMembership(String memberUUID, boolean iDefaultProfileValue) {
        Security.checkMemberOwnership(memberUUID);
        IMembershipManager membershipManager = new MembershipManager();
        Membership member = membershipManager.findByUUID(memberUUID, true);
        User user = (User) ContextHelper.getMembership().getUser();
        user.setDefaultMembership(member);

        IAccountManager accountManager = new AccountManager();
        user = accountManager.updateAccount(user);
    }

    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#findDefaultMembershipByEmail(java.lang.String)
     */
    @Override
    public Negotiator findDefaultMembershipByEmail(String iEmail) {
        Negotiator negotiator = null;

        if (!Strings.isNullOrEmpty(iEmail)) {
            IAccountManager accountManager = new AccountManager();
            User user = accountManager.findByUsername(iEmail);

            if (user != null && user.getDefaultMembership() != null) {
                negotiator = user.getDefaultMembership();
            }
        }

        return negotiator;
    }


    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#findByUUID(java.lang.String)
     */
    @Override
    public Membership findByUUID(String uuid, boolean noCache) {
        if (Strings.isNullOrEmpty(uuid))
            return null;

        //handle using Play Cache
//		if(noCache) {
//			Cache.remove(uuid);
//			return memberProfilePersistent.readObjectByUUID(Membership.class, uuid);
//		}
//		else {
//			Membership membership = (Membership)Cache.get(uuid);
//			
//			if(membership != null) {
//				return membership;
//			}
//			
//			membership = memberProfilePersistent.readObjectByUUID(Membership.class, uuid);
//			ExLogger.get().debug("Membership loaded from db: " + membership.getId() + membership.getUser().getId() + membership.getProfile().getId());
//			Cache.set(uuid, membership, 60 * 10);
//			return membership;
//		}

        // handle using ThreadLocal
        if (noCache) {
            ContextHelper.remove(uuid);
            return memberProfilePersistent.readObjectByUUID(Membership.class, uuid);
        } else {
            Membership membership = (Membership) ContextHelper.get(uuid);

            if (membership == null) {
                membership = memberProfilePersistent.readObjectByUUID(Membership.class, uuid);
                ContextHelper.put(uuid, membership);
            }

            return membership;
        }

    }

    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#findByProfileUUID(java.lang.String)
     */
    @Override
    public List<Membership> getProfileMembers(String iProfileUUID) {
        QueryFilters filters = QueryFilters.create(MembershipFilters.PROFILE, iProfileUUID);
        return memberProfilePersistent.find(filters);
    }

    @Override
    public List<Membership> getActiveProfileMembers(String iProfileUUID) {
        QueryFilters filters = QueryFilters.create(MembershipFilters.PROFILE, iProfileUUID);
        filters.put(MembershipFilters.IS_ACTIVE, true);
        return memberProfilePersistent.find(filters);
    }

    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#findByUserUUID(java.lang.String)
     */
    @Override
    public List<Membership> getUserMemberships(String iUserUUID) {
        QueryFilters filters = QueryFilters.create(MembershipFilters.USER, iUserUUID);
        return memberProfilePersistent.find(filters);
    }

    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#getMembershipOf(java.lang.String, java.lang.String)
     */
    @Override
    public Membership getMembershipOf(String userUUID, String iProfileUUID, boolean includeGuest) {
        QueryFilters filters = new QueryFilters();
        filters.put(MembershipFilters.USER, userUUID);
        filters.put(MembershipFilters.PROFILE, iProfileUUID);
        if (includeGuest)
            filters.put(MembershipFilters.INCLUDE_GUEST, true);
        Membership membership = memberProfilePersistent.findMembership(filters);
        return membership;
    }

    @Override
    public Membership getMembershipByEmail(String email, String iProfileUUID, boolean includeGuest) {
        QueryFilters filters = new QueryFilters();
        filters.put(MembershipFilters.EMAIL, email);
        filters.put(MembershipFilters.PROFILE, iProfileUUID);
        if (includeGuest)
            filters.put(MembershipFilters.INCLUDE_GUEST, true);
        Membership membership = memberProfilePersistent.findMembership(filters);
        return membership;
    }

    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#isMembership(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isMembership(String iUserUUID, String iProfileUUID) {
        Membership membership = getMembershipOf(iUserUUID, iProfileUUID, false);
        return membership != null;
    }

    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#isMembershipOwnedBy(com.exrade.models.userprofile.User, java.lang.String)
     */
    @Override
    public boolean isMembershipOwnedBy(IUser user, String iMembershipUUID) {
        Membership membership = findByUUID(iMembershipUUID, false);
        return user != null && user.equals(membership.getUser());
    }

    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#find(com.exrade.platform.persistence.query.QueryFilters)
     */
    @Override
    public List<Membership> find(QueryFilters iFilters) {
        return memberProfilePersistent.find(iFilters);
    }

    /* (non-Javadoc)
     * @see com.exrade.runtime.userprofile.IMembershipManager#getOwnerMembership(java.lang.String)
     */
    @Override
    public Membership getOwnerMembership(String iProfileUUID) {
        QueryFilters filters = QueryFilters.create(MembershipFilters.PROFILE, iProfileUUID);
        filters.put(MembershipFilters.ROLENAME, MemberRole.OWNER);
        List<Membership> memberships = find(filters);

        if (memberships != null && memberships.size() > 0)
            return memberships.get(0);

        return null;
    }

    @Override
    public void updateMemberStatus(String iMembershipUUID, MemberStatus iMemberStatus) {
        if (MemberStatus.ACTIVE == iMemberStatus)
            Security.checkAddMembershipPermission();


        Membership member = this.findByUUID(iMembershipUUID, true);
        checkPermission(member);
        member.setStatus(iMemberStatus);
        memberProfilePersistent.update(member);

        ExLogger.get().info("Updated membership status. MembershipUUID: {}, Status: {}", member.getUuid(), iMemberStatus);

        if (MemberStatus.DISABLED == iMemberStatus) {
            INegotiationManager negotiationManager = new NegotiationManager();
            negotiationManager.transferNegotiations(member, getOwnerMembership(member.getProfileUUID()));
        }
    }

    private void checkPermission(Membership member) {
        if (member == null) {
            throw new ExNotFoundException("member");
        }
        Security.checkMembership(member.getProfileUUID(), Arrays.asList(MemberRole.ADMIN, MemberRole.OWNER));
    }

    @Override
    public Membership updateMembership(Membership membership) {
        Membership existingMembership = this.findByUUID(membership.getUuid(), true);
        checkPermission(existingMembership);
        if (MemberStatus.ACTIVE == membership.getStatus() && MemberStatus.ACTIVE != existingMembership.getStatus())
            Security.checkAddMembershipPermission();
        checkRoleChangePermission(existingMembership.getRole().getName(), membership.getRole().getName());
        existingMembership.setTitle(membership.getTitle());
        existingMembership.setAgreementSigner(membership.isAgreementSigner());
        existingMembership.setExpirationDate(membership.getExpirationDate());
        existingMembership.setAuthorizationDocuments(membership.getAuthorizationDocuments());
        existingMembership.setMaxNegotiationAmount(membership.getMaxNegotiationAmount());
        existingMembership.setRole((MemberRole) membership.getRole());
        existingMembership.setStatus(membership.getStatus());
        existingMembership.setUpdateDate(TimeProvider.now());
        existingMembership.setUpdatedBy((Membership) ContextHelper.getMembership());
        if (membership.getSupervisor() != null)
            existingMembership.setSupervisor(this.findByUUID(membership.getSupervisor().getUuid(), true));
        else
            existingMembership.setSupervisor(null);

        Membership updatedMembership = memberProfilePersistent.update(existingMembership);
        ExLogger.get().info("Updated membership. MembershipUUID: {}", updatedMembership.getUuid());
        return updatedMembership;
    }

    @Override
    public Membership createGuestMembership(String firstName, String lastName, String email, String phone, String title,
                                            IProfile profile) {
        if ((Strings.isNullOrEmpty(email) && Strings.isNullOrEmpty(phone)) || profile == null)
            throw new ExException("Email or profile cannot be null");

        PhoneNumber phoneNumber = null;
        String formattedPhoneNumber = null;
        if (!Strings.isNullOrEmpty(phone)) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
                phoneNumber = phoneUtil.parse(phone, null);
                formattedPhoneNumber = phoneUtil.format(phoneNumber, PhoneNumberFormat.E164);
            } catch (NumberParseException e) {
                throw new ExException("Invalid phone number format.");
            }
        }

        User user = null;

        QueryFilters filters = new QueryFilters();
        if (!Strings.isNullOrEmpty(email))
            filters.put(MembershipFilters.EMAIL, email.toLowerCase());
        else if (!Strings.isNullOrEmpty(formattedPhoneNumber))
            filters.put(MembershipFilters.PHONE, formattedPhoneNumber);
        filters.put(MembershipFilters.INCLUDE_GUEST, true);
        List<Membership> membership = memberProfilePersistent.find(filters);

        if (!ExCollections.isEmpty(membership)) {
            user = membership.get(0).getUser();
            if (!user.getFirstName().equals(firstName) && !user.getLastName().equals(lastName)) {
                throw new ExException("User exists with the same email/phone number but different name!");
            }
        } else {
            user = new User();
            Role role = new RoleManager().findByName(PlatformRole.MEMBER);

            user = !Strings.isNullOrEmpty(email) ? User.createUser(email) : User.createUser(formattedPhoneNumber);

            if (!Strings.isNullOrEmpty(email))
                user.setEmail(email);
            user.setPhone(formattedPhoneNumber);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setLanguage(ContextHelper.getMembership().getUser().getLanguage());
            user.setTimezone(ContextHelper.getMembership().getUser().getTimezone());
            user.setPlatformRole((PlatformRole) role);

            user = memberProfilePersistent.create(user);

            ExLogger.get().info("Created user for guest member. ProfileUUID: {}, UserUUID: {}", profile.getUuid(), user.getUuid());
        }

        return createGuestMembership(user, profile, title);
    }

    @Override
    public Membership createGuestMembership(IUser user, IProfile profile, String title) {
        Role role = new RoleManager().findByName(MemberRole.GUEST);

        Membership membership = Membership.createMembership((User) user, (Profile) profile, (MemberRole) role);
        membership.setTitle(title);
        membership.setGuest(true);
        membership = memberProfilePersistent.create(membership);

        ExLogger.get().info("Created guest member. ProfileUUID: {}, MembershipUUID: {}", profile.getUuid(), membership.getUuid());
        return membership;
    }

    @Override
    public void disableAdditionalMembersFromProfile(Profile profile) {
        INegotiationManager negotiationManager = new NegotiationManager();
        List<Membership> profileMembers = getProfileMembers(profile.getUuid());
        Membership ownerMembership = getOwnerMembership(profile.getUuid());

        long maximumAllowedMember = !profile.isActive() ? 0 : Security.getResourceLimit(ProfilePermissions.PROFILE_MAX_MEMBER, profile.getPermissions());
        long activeMemberCount = 0;
        for (Membership membership : profileMembers) {
            try {
                if (membership.isActive()) {
                    activeMemberCount++;

                    if (activeMemberCount > maximumAllowedMember) {
                        membership.setStatus(MemberStatus.DISABLED);
                        memberProfilePersistent.update(membership);

                        ExLogger.get().info(String.format("Disabled membership. ProfileUUID: {}, MembershipUUID: {}", profile.getUuid(), membership.getUuid()));

                        negotiationManager.transferNegotiations(membership, ownerMembership);
                    }
                }

            } catch (Exception ex) {
                ExLogger.get().warn(String.format("Disabling membership failed! ProfileUUID: {}, MembershipUUID: {}", profile.getUuid(), membership.getUuid()), ex);
            }
        }
    }

    @Override
    public void enableOwnerMembership(Profile profile) {
        Membership ownerMembership = getOwnerMembership(profile.getUuid());
        ownerMembership.setStatus(MemberStatus.ACTIVE);
        memberProfilePersistent.update(ownerMembership);
        ExLogger.get().info(String.format("Enabled owner membership. ProfileUUID: {}, MembershipUUID: {}", profile.getUuid(), ownerMembership.getUuid()));
    }

    private void checkRoleChangePermission(String existingRole, String newRole) {
        if (MemberRole.OWNER.equals(existingRole) && !existingRole.equals(newRole))
            throw new ExException("Owner role cannot be changed!");
    }
}
