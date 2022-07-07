package com.exrade.runtime.invitation;

import com.exrade.models.activity.Verb;
import com.exrade.models.invitations.AbstractInvitation;
import com.exrade.models.invitations.InvitationStatus;
import com.exrade.models.invitations.MemberInvitation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.MemberRole;
import com.exrade.platform.exception.*;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.invitation.persistence.InvitationQuery;
import com.exrade.runtime.invitation.persistence.MemberInvitationPersistence;
import com.exrade.runtime.notification.event.ProfileNotificationEvent;
import com.exrade.runtime.rest.RestParameters.MemberInvitationFields;
import com.exrade.runtime.rest.RestParameters.MemberInvitationFilters;
import com.exrade.runtime.userprofile.*;
import com.exrade.util.ContextHelper;

import java.util.*;

public class MemberInvitationManager extends AbstractInvitationManager implements IMemberInvitationManager {
	private IProfileManager profileManager = new ProfileManager();
	private IMembershipManager membershipManager = new MembershipManager();

	public MemberInvitationManager() {
		this(new MemberInvitationPersistence());
	}

	public MemberInvitationManager(MemberInvitationPersistence iMemberPersistenceManager) {
		invitationPersistence = iMemberPersistenceManager;
	}

	@Override
	public MemberInvitation inviteToProfile(String iProfileUUID, String iInvitedUserEmail, String iRoleName){
		return inviteToProfile(iProfileUUID, iInvitedUserEmail.toLowerCase(), iRoleName, "", null, new ArrayList<String>(), Double.MAX_VALUE, true, null);
	}

	@Override
	public MemberInvitation inviteToProfile(String iProfileUUID, String iInvitedUserEmail, String iRoleName,
			String iTitle, Date iExpirationDate, List<String> iIdentityDocuments, Double iMaxNegotiationAmount, boolean iAgreementSigner, Membership iSupervisor) {
		Security.checkMembership(iProfileUUID, Arrays.asList(MemberRole.ADMIN,MemberRole.OWNER));
		Security.checkAddMembershipPermission();

		if(iSupervisor != null && !Security.checkRole(iSupervisor, Arrays.asList(MemberRole.ADMIN,MemberRole.OWNER)))
			throw new ExException(ErrorKeys.NOT_AUTHORIZED);

		Profile profileForInvite = profileManager.findByUUID(iProfileUUID);
		if (profileForInvite == null)
			throw new ExParamException(ErrorKeys.PARAM_INVALID,MemberInvitationFilters.INVITED_PROFILE_UUID);
		// check if the provided email is owned by an already registered user
		IAccountManager accountManager = new AccountManager();
		User userToInvite = accountManager.findByUsername(iInvitedUserEmail);
		// retrieve eventual already present invitation
		MemberInvitation invitation = getMemberInvitation(iInvitedUserEmail, profileForInvite);

		checkMemberInvitation(profileForInvite,userToInvite,invitation);

		if(invitation == null){
			invitation = createMemberInvitation(userToInvite, iInvitedUserEmail, profileForInvite,iRoleName, iTitle, iExpirationDate, iIdentityDocuments, iMaxNegotiationAmount, iAgreementSigner, iSupervisor);
		}
		else if(invitation.isInvitationStatus(InvitationStatus.REJECTED)){
			// if a new invite is requested by the owner and the invited user rejected previous invitation
			// the invite status is reverted to PENDING instead of creating a new invite
			updateInvitationStatus(invitation.getUuid(), InvitationStatus.PENDING);
		}

		notificationManager.process(new ProfileNotificationEvent(NotificationType.PROFILE_MEMBERSHIP_INVITATION_RECEIVED, invitation.getInvitedProfile(), invitation));
		ActivityLogger.log(ContextHelper.getMembership(), Verb.INVITE, invitation, Arrays.asList(membershipManager.findDefaultMembershipByEmail(iInvitedUserEmail)));
		return invitation;
	}

	private MemberInvitation createMemberInvitation(User userToInvite, String iInvitedUserEmail, Profile profileForInvite, String iRoleName,
			String iTitle, Date iExpirationDate, List<String> iIdentityDocuments, Double iMaxNegotiationAmount, boolean iAgreementSigner, Membership iSupervisor) {
		MemberInvitation invitation = null;
		if (userToInvite != null){
			// if the user is provided the invitation has to be created with the given user
			invitation = new MemberInvitation(userToInvite, profileForInvite,iRoleName);
		}
		else {
			invitation = new MemberInvitation(iInvitedUserEmail, profileForInvite,iRoleName);
		}
		invitation.setAgreementSigner(iAgreementSigner);
		invitation.setAuthorizationDocuments(iIdentityDocuments);
		invitation.setMembershipExpirationDate(iExpirationDate);
		invitation.setInvitedBy((Membership)ContextHelper.getMembership());
		invitation.setMaxNegotiationAmount(iMaxNegotiationAmount);
		invitation.setTitle(iTitle);
		invitation.setSupervisor(iSupervisor);

		return invitationPersistence.create(invitation);
	}

	private void checkMemberInvitation(Profile profileForInvite,
			User userToInvite, MemberInvitation invitation) {

		if(userToInvite != null){
			IMembershipManager membershipManager = new MembershipManager();
			Membership existingMembership =
					membershipManager.getMembershipOf(userToInvite.getUuid(),profileForInvite.getUuid(), false);
			if (existingMembership != null){
				throw new ExInvitationException(ErrorKeys.INVITATION_NOT_ALLOWED);
			}
		}

		if(invitation != null && invitation.getInvitationStatus() == InvitationStatus.BLOCKED){
			throw new ExInvitationException(ErrorKeys.INVITATION_STATUS_INVALID);
		}

	}

	@Override
	public List<MemberInvitation> find(QueryFilters iFilters) {
		List<MemberInvitation> invitations = new ArrayList<>();

		String iProfileUUID = (String) iFilters.get(MemberInvitationFilters.INVITED_PROFILE_UUID);

		// this is to prevent to select invitations sent on all the profiles of the system
		boolean isAdmin = false;
		if (iProfileUUID == null){
			iFilters.put(MemberInvitationFilters.INVITATION_INBOX,InvitationQuery.INCOMING);
		}
		else if (iProfileUUID != null){
			isAdmin = Security.isMembership(iProfileUUID, Arrays.asList(MemberRole.ADMIN,MemberRole.OWNER));
			if (isAdmin) {
				iFilters.put(MemberInvitationFilters.INVITATION_INBOX,InvitationQuery.SENT);
			}
			else {
				iFilters.put(MemberInvitationFilters.INVITATION_INBOX,InvitationQuery.INCOMING);
			}
		}

		/*if (iFilters.isNullOrEmpty(MemberInvitationFields.INVITATION_STATUS)){
			iFilters.put(MemberInvitationFields.INVITATION_STATUS, InvitationStatus.PENDING.name());
		}*/

		invitations = invitationPersistence.list(iFilters);

		return invitations;
	}

	@Override
	public MemberInvitation getMemberInvitation(String iInvitedUserEmail,
			Profile iProfile) {
		QueryFilters filters = new QueryFilters();
		filters.put(MemberInvitationFields.INVITED_EMAIL, iInvitedUserEmail);
		filters.put(MemberInvitationFields.INVITED_PROFILE, iProfile.getId());
		MemberInvitation invite = invitationPersistence.read(filters);
		return invite;
	}

	@Override
	public MemberInvitation getMemberInvitation(User iUser, String iProfileUUID) {
		QueryFilters filters = new QueryFilters();
		filters.put(MemberInvitationFields.INVITED_USER, iUser.getId());
		filters.put(MemberInvitationFilters.INVITED_PROFILE_UUID, iProfileUUID);
		MemberInvitation invite = invitationPersistence.read(filters);
		return invite;
	}

	@Override
	public void doAfterAccept(AbstractInvitation iInvitation) {
		MemberInvitation memberInvitation = (MemberInvitation) iInvitation;
		Membership membership = null;
		try {
			membership = membershipManager.getMembershipOf(memberInvitation.getInvitedUserUUID(), memberInvitation.getInvitedProfileUUID(), true);
			if(membership == null)
				membership = membershipManager.addMembership(memberInvitation);
			else if(membership.isGuest()) {
				membershipManager.updateMembership(memberInvitation);
			}
		}
		catch(ExException ex) {
			logger.warn(ex.getMessage(), ex);

			membership = membershipManager.getMembershipOf(memberInvitation.getInvitedUserUUID(), memberInvitation.getInvitedProfileUUID(), false);
		}
		iInvitation.setInvitationStatus(InvitationStatus.ACCEPTED);
		invitationPersistence.update(iInvitation);

		Membership profileOwner = membershipManager.getOwnerMembership(memberInvitation.getInvitedProfileUUID());
		createContact(iInvitation, membership, profileOwner); // automatically create contact after invitation being accepted
		notificationManager.process(new ProfileNotificationEvent(NotificationType.PROFILE_MEMBERSHIP_INVITATION_UPDATED, memberInvitation.getInvitedProfile(), memberInvitation));
		ActivityLogger.log(ContextHelper.getMembership(), Verb.ACCEPT, memberInvitation, Arrays.asList((Negotiator)profileOwner));
	}

	@Override
	public void deleteInvitation(String invitationID) {
		MemberInvitation invitation = invitationPersistence.readObjectByUUID(MemberInvitation.class, invitationID);
		Security.checkMembership(invitation.getInvitedProfile().getUuid(),Arrays.asList(MemberRole.ADMIN,MemberRole.OWNER));
		super.deleteInvitation(invitationID);
	}

	@Override
	public void updateInvitationsForNewUser(User iUser) {
		Objects.requireNonNull(iUser);
		try {
			QueryFilters filters = new QueryFilters();
			filters.put(MemberInvitationFields.INVITED_EMAIL, iUser.getEmail());
			List<MemberInvitation> inviteList = invitationPersistence
					.list(filters);
			for (MemberInvitation invite : inviteList) {
				invite.setInvitedUser(iUser);
				invitationPersistence.update(invite);
			}
		} catch (Exception ex) {
			logger.warn("Failed to update inviations for newly registered user. ErrorDetail: " + ex.getStackTrace());
			throw new ExPersistentException(ex);
		}
	}

	@Override
	public void doAfterReject(AbstractInvitation iInvitation) {
		MemberInvitation memberInvitation = (MemberInvitation) iInvitation;
		notificationManager.process(new ProfileNotificationEvent(NotificationType.PROFILE_MEMBERSHIP_INVITATION_UPDATED, memberInvitation.getInvitedProfile(), memberInvitation));
		ActivityLogger.log(ContextHelper.getMembership(), Verb.REJECT, memberInvitation, Arrays.asList((Negotiator)membershipManager.getOwnerMembership(memberInvitation.getInvitedProfileUUID())));
	}

}
