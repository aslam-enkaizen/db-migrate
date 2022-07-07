package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.invitations.AbstractInvitation;
import com.exrade.models.invitations.InvitationStatus;

public interface InvitationAPI {

	void acceptInvitation(ExRequestEnvelope request, String invitationID);

	void rejectInvitation(ExRequestEnvelope request, String invitationID);

	void blockInvitation(ExRequestEnvelope request, String invitationID);
	
	void deleteInvitation(ExRequestEnvelope request, String invitationUUID);
	
	void updateInvitationStatus(ExRequestEnvelope request, String invitationUUID,	InvitationStatus invitationStatus);
	
	boolean isValidInvitation(ExRequestEnvelope request, String invitationUUID);
	
	void doAfterAccept(ExRequestEnvelope request, AbstractInvitation invite);

	void updateInvitationStatus(ExRequestEnvelope request, AbstractInvitation invitation,InvitationStatus invitationStatus);
	
	AbstractInvitation getInvitation(ExRequestEnvelope request, String invitationID);
	
}
