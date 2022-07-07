package com.exrade.runtime.invitation;

import com.exrade.models.invitations.AbstractInvitation;
import com.exrade.models.invitations.InvitationStatus;

public interface IInvitationManager {

	void acceptInvitation(String invitationID);

	void rejectInvitation(String invitationID);

	void blockInvitation(String invitationID);
	
	void deleteInvitation(String invitationUUID);
	
	void updateInvitationStatus(String invitationUUID,	InvitationStatus invitationStatus);
	
	boolean isValidInvitation(String invitationUUID);
	
	void doAfterAccept(AbstractInvitation invite);

	void updateInvitationStatus(AbstractInvitation invitation,InvitationStatus invitationStatus);
	
	AbstractInvitation getInvitation(String invitationID);
	
}
