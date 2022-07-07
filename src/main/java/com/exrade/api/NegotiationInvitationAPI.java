package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.invitations.NegotiationInvitation;
import com.exrade.models.userprofile.Negotiator;

import java.util.List;
import java.util.Map;

public interface NegotiationInvitationAPI extends InvitationAPI {

	void acceptNegotiationInvitationIfExists(ExRequestEnvelope request, String iInvitedUserUUID, String iNegotiationUUID);
	
	List<NegotiationInvitation> getAllIncomingInvitations(ExRequestEnvelope request, Map<String, String> iFilters);
	
	List<NegotiationInvitation> getSentInvitationsOfNegotiation(ExRequestEnvelope request, String iNegotiationUUID, Map<String, String> iFilters);
	
	NegotiationInvitation getNegotiationInvitation(ExRequestEnvelope request, Negotiator iInvitedMember,
			String iInvitedUserEmail, String iNegotiationUUID);
	
	NegotiationInvitation inviteToNegotiation(ExRequestEnvelope request, String iNegotiationUUID, String iInvitedMemberUUID,
			String iInvitedEmail);
	
	void updateInvitationsForNewUser(ExRequestEnvelope request, Negotiator iNegotiator);

}