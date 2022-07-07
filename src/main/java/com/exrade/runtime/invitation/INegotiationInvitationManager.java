package com.exrade.runtime.invitation;

import com.exrade.models.invitations.NegotiationInvitation;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface INegotiationInvitationManager extends IInvitationManager {

	void acceptNegotiationInvitationIfExists(String iInvitedUserUUID, String iNegotiationUUID);
	
	List<NegotiationInvitation> find(QueryFilters iFilters);
	
	NegotiationInvitation getNegotiationInvitation(Negotiator iInvitedMember,
			String iInvitedUserEmail, String iNegotiationUUID);

	NegotiationInvitation inviteToNegotiation(Negotiator iNegotiator,
			String iNegotiationUUID, Negotiator iInvitedMember,
			String iInvitedEmail);
	
	void updateInvitationsForNewUser(Negotiator iNegotiator);
	
	void sendDraftInvitations(Negotiation negotiation);

}