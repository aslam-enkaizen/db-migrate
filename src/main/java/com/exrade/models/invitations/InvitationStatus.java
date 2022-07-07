package com.exrade.models.invitations;

public enum InvitationStatus {
	DRAFT, // invitation is created but not sent
	ACCEPTED, //invitation accepted by the participant 
    REJECTED, //invitation rejected by the participant, owner can invite again
    PENDING, //participant did not response to the invitation
    BLOCKED, //participant blocked invitation, owner should not be able to resend invitation again
}
