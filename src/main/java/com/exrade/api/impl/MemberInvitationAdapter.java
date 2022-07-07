package com.exrade.api.impl;

import com.exrade.api.MemberInvitationAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.invitations.AbstractInvitation;
import com.exrade.models.invitations.InvitationStatus;
import com.exrade.models.invitations.MemberInvitation;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.User;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExParamException;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.invitation.IMemberInvitationManager;
import com.exrade.runtime.invitation.MemberInvitationManager;
import com.exrade.runtime.invitation.persistence.InvitationQuery;
import com.exrade.runtime.rest.RestParameters.MemberInvitationFields;
import com.exrade.runtime.rest.RestParameters.MemberInvitationFilters;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.MultiLanguageUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class MemberInvitationAdapter implements MemberInvitationAPI {

	IMemberInvitationManager manager = new MemberInvitationManager();
	
	@Override
	public void acceptInvitation(ExRequestEnvelope request, String invitationID) {
		ContextHelper.initContext(request);
		manager.acceptInvitation(invitationID);
	}

	@Override
	public void rejectInvitation(ExRequestEnvelope request, String invitationID) {
		ContextHelper.initContext(request);
		manager.rejectInvitation(invitationID);
	}

	@Override
	public void blockInvitation(ExRequestEnvelope request, String invitationID) {
		ContextHelper.initContext(request);
		manager.blockInvitation(invitationID);
	}

	@Override
	public void deleteInvitation(ExRequestEnvelope request,
			String invitationUUID) {
		ContextHelper.initContext(request);
		manager.deleteInvitation(invitationUUID);
	}

	@Override
	public void updateInvitationStatus(ExRequestEnvelope request,
			String invitationUUID, InvitationStatus invitationStatus) {
		ContextHelper.initContext(request);
		manager.updateInvitationStatus(invitationUUID, invitationStatus);
	}

	@Override
	public boolean isValidInvitation(ExRequestEnvelope request,
			String invitationUUID) {
		ContextHelper.initContext(request);
		return manager.isValidInvitation(invitationUUID);
	}

	@Override
	public void doAfterAccept(ExRequestEnvelope request,
			AbstractInvitation invite) {
		ContextHelper.initContext(request);
		manager.doAfterAccept(invite);
	}

	@Override
	public void updateInvitationStatus(ExRequestEnvelope request,
			AbstractInvitation invitation, InvitationStatus invitationStatus) {
		ContextHelper.initContext(request);
		manager.updateInvitationStatus(invitation, invitationStatus);
	}

	@Override
	public AbstractInvitation getInvitation(ExRequestEnvelope request,
			String invitationID) {
		ContextHelper.initContext(request);
		return manager.getInvitation(invitationID);
	}

	@Override
	public MemberInvitation inviteToProfile(ExRequestEnvelope request, String iProfileUUID, String invitedMembershipUUID, String iInvitedUserEmail, String iRoleName,
			String iTitle, Date iExpirationDate, List<String> iIdentityDocuments, Double iMaxNegotiationAmount, boolean iAgreementSigner, String iSupervisor) {
		ContextHelper.initContext(request);
		
		Membership supervisor = null;
		IMembershipManager membershipManager = new MembershipManager();
		
		if(iInvitedUserEmail == null && invitedMembershipUUID == null){
			throw new ExParamException(ErrorKeys.PARAMS_INVALID, MultiLanguageUtil.getLabel("Invitation email and member id cant be both empty"));
		}
		else if (iInvitedUserEmail == null && invitedMembershipUUID != null){
			Membership membership = membershipManager.findByUUID(invitedMembershipUUID, false);
			iInvitedUserEmail = membership.getEmail();
		}
		
		supervisor = membershipManager.findByUUID(iSupervisor, true);
		return manager.inviteToProfile(iProfileUUID, iInvitedUserEmail.toLowerCase(), iRoleName, iTitle, iExpirationDate, iIdentityDocuments, iMaxNegotiationAmount, iAgreementSigner, supervisor);
	}

	@Override
	public MemberInvitation getMemberInvitation(ExRequestEnvelope request,
			String iInvitedUserEmail, Profile iProfile) {
		ContextHelper.initContext(request);
		return manager.getMemberInvitation(iInvitedUserEmail, iProfile);
	}

	@Override
	public MemberInvitation getMemberInvitation(ExRequestEnvelope request,
			User iUser, String iProfileUUID) {
		ContextHelper.initContext(request);
		return manager.getMemberInvitation(iUser, iProfileUUID);
	}

	@Override
	public void updateInvitationsForNewUser(ExRequestEnvelope request,
			User iUser) {
		ContextHelper.initContext(request);
		manager.updateInvitationsForNewUser(iUser);
	}

	@Override
	public List<MemberInvitation> getAllIncomingInvitations(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putAll(getMemberInvitationFilters(iFilters));
		filters.put(MemberInvitationFilters.INVITATION_INBOX, InvitationQuery.INCOMING);
		return manager.find(filters);
	}

	@Override
	public List<MemberInvitation> getSentInvitationsOfProfile(ExRequestEnvelope request, String iProfileUUID,
			Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putAll(getMemberInvitationFilters(iFilters));
		filters.put(MemberInvitationFilters.INVITED_PROFILE_UUID, iProfileUUID);
		return manager.find(filters);
	}

	private static QueryFilters getMemberInvitationFilters(Map<String, String> iFilters) {
		QueryFilters filters = QueryFilters.create(iFilters);
		if (filters.isNullOrEmpty(MemberInvitationFilters.INVITED_PROFILE_UUID)) {
			filters.putIfNotNull(MemberInvitationFilters.INVITED_PROFILE_UUID,
					iFilters.get(MemberInvitationFilters.INVITED_PROFILE_UUID));
		}
		if (filters.isNullOrEmpty(MemberInvitationFilters.INVITATION_INBOX)) {
			filters.putIfNotNull(MemberInvitationFilters.INVITATION_INBOX,
					iFilters.get(MemberInvitationFilters.INVITATION_INBOX));
		}
		filters.putIfNotNull(MemberInvitationFields.INVITED_EMAIL,
				iFilters.get(MemberInvitationFields.INVITED_EMAIL));
		filters.putIfNotNull(MemberInvitationFields.INVITATION_STATUS,
				iFilters.get(MemberInvitationFields.INVITATION_STATUS));
		filters.putIfNotNull(MemberInvitationFields.INVITED_USER,
				iFilters.get(MemberInvitationFields.INVITED_USER));
		filters.putIfNotNull(MemberInvitationFilters.INVITED_USER_UUID,
				iFilters.get(MemberInvitationFilters.INVITED_USER_UUID));
		filters.putIfNotNull(MemberInvitationFields.INVITED_PROFILE,
				iFilters.get(MemberInvitationFields.INVITED_PROFILE));
		filters.putIfNotNull(MemberInvitationFields.ROLENAME, iFilters.get(MemberInvitationFields.ROLENAME));
		return filters;
	}
}
