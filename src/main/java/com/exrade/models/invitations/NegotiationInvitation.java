package com.exrade.models.invitations;

import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.google.common.base.MoreObjects;

public class NegotiationInvitation extends AbstractInvitation {

	private Negotiator invitedMembership;

	private Negotiation invitedNegotiation;

	public NegotiationInvitation(){}

	public NegotiationInvitation(Negotiator iInvitedMembership, Negotiation negotiation) {
		this(((Membership)iInvitedMembership).getEmail(),negotiation);
		setInvitedMembership(iInvitedMembership);
	}

	public NegotiationInvitation(String invitedEmail, Negotiation negotiation) {
		super(invitedEmail);
		setInvitedNegotiation(negotiation);
	}

	public void setInvitedNegotiation(Negotiation invitedNegotiation) {
		this.invitedNegotiation = invitedNegotiation;
	}

	public Negotiation getInvitedNegotiation() {
		return invitedNegotiation;
	}

	public Negotiator getInvitedMembership() {
		return invitedMembership;
	}

	public String getInvitedNegotiationUUID() {
		if (getInvitedNegotiation() != null){
			return getInvitedNegotiation().getUuid();
		}
		return null;
	}

	public String getInvitedMembershipUUID() {
		if (getInvitedMembership() != null){
			return getInvitedMembership().getIdentifier();
		}
		return null;
	}

	public void setInvitedMembership(Negotiator invitedMembership) {
		this.invitedMembership = invitedMembership;
	}

	@Override
	public String toString(){
		return MoreObjects.toStringHelper(getClass().getSimpleName()).add("uuid", getUuid()).toString();
	}
}
