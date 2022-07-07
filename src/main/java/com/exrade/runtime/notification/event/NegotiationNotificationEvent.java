package com.exrade.runtime.notification.event;

import com.exrade.models.invitations.NegotiationInvitation;
import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.runtime.engine.StateMachine;

public class NegotiationNotificationEvent extends NotificationEvent<Negotiation> {
	
	private NegotiationMessage negotiationMessage;
	
	private NegotiationInvitation negotiationInvitation;
	
	private StateMachine stateMachine;
	private Membership member;
	
	public NegotiationNotificationEvent(NotificationType source, Negotiation payload) {
		super(source, payload);
	}
	
	public NegotiationNotificationEvent(NotificationType source, Negotiation payload, NegotiationMessage negotiationMessage) {
		super(source, payload);
		this.negotiationMessage = negotiationMessage;
	}
	
	public NegotiationNotificationEvent(NotificationType source, Negotiation payload, NegotiationInvitation negotiationInvitation) {
		super(source, payload);
		this.negotiationInvitation = negotiationInvitation;
	}
	
	public NegotiationNotificationEvent(NotificationType source, Negotiation payload, StateMachine stateMachine) {
		super(source, payload);
		this.stateMachine = stateMachine;
	}
	
	public NegotiationNotificationEvent(NotificationType source, Negotiation payload, StateMachine stateMachine, NegotiationMessage negotiationMessage) {
		super(source, payload);
		this.stateMachine = stateMachine;
		this.negotiationMessage = negotiationMessage;
	}
	
	public NegotiationNotificationEvent(NotificationType negotiationMemberAdded, Negotiation negotiation,
			Membership membership) {
		super(negotiationMemberAdded, negotiation);
		this.member = membership;
	}

	public Negotiation getNegotiation(){
		return getPayload();
	}
	
	public NegotiationMessage getNegotiationMessage(){
		return negotiationMessage;
	}
	
	public NegotiationInvitation getNegotiationInvitation(){
		return negotiationInvitation;
	}
	
	public StateMachine getStateMachine(){
		return stateMachine;
	}

	public Membership getMember() {
		return member;
	}
	
}
