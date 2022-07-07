package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.invitations.MemberInvitation;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MemberInvitationAPI extends InvitationAPI {
	
	MemberInvitation inviteToProfile(ExRequestEnvelope request, String iProfileUUID, String invitedMembershipUUID, String iInvitedUserEmail,String iRoleName, String iTitle, Date iExpirationDate, List<String> iIdentityDocuments, Double iMaxNegotiationAmount, boolean iAgreementSigner, String iSupervisor);
	
	List<MemberInvitation> getAllIncomingInvitations(ExRequestEnvelope request, Map<String, String> iFilters);
	
	List<MemberInvitation> getSentInvitationsOfProfile(ExRequestEnvelope request, String iProfileUUID, Map<String, String> iFilters);

	MemberInvitation getMemberInvitation(ExRequestEnvelope request, String iInvitedUserEmail,Profile iProfile);

	MemberInvitation getMemberInvitation(ExRequestEnvelope request, User iUser, String iProfileUUID);
	
	void updateInvitationsForNewUser(ExRequestEnvelope request, User iUser);
}
