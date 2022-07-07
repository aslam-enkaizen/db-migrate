package com.exrade.runtime.invitation;

import com.exrade.models.invitations.MemberInvitation;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Profile;
import com.exrade.models.userprofile.User;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.Date;
import java.util.List;

public interface IMemberInvitationManager extends IInvitationManager {
	
	MemberInvitation inviteToProfile(String iProfileUUID, String iInvitedUserEmail,String iRoleName, String iTitle, Date iExpirationDate, List<String> iIdentityDocuments, Double iMaxNegotiationAmount, boolean iAgreementSigner, Membership iSupervisor);
	
	MemberInvitation inviteToProfile(String iProfileUUID, String iInvitedUserEmail,String iRoleName);
	
	List<MemberInvitation> find(QueryFilters iFilters);

	MemberInvitation getMemberInvitation(String iInvitedUserEmail,Profile iProfile);

	MemberInvitation getMemberInvitation(User iUser, String iProfileUUID);
	
	void updateInvitationsForNewUser(User iUser);
}
