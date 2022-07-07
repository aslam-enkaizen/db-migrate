package com.exrade.runtime.notification.event;

import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.signatures.NegotiationSignatureContainer;

public class SignatureNotificationEvent extends NotificationEvent<NegotiationSignatureContainer> {
	
	private Negotiation negotiation;
	
	public SignatureNotificationEvent(NotificationType source, NegotiationSignatureContainer payload) {
		super(source, payload);
	}
	
	public SignatureNotificationEvent(NotificationType source, NegotiationSignatureContainer payload, Negotiation negotiation) {
		super(source, payload);
		this.negotiation = negotiation;
	}
	
	public Negotiation getNegotiation(){
		return negotiation;
	}
	
	public NegotiationSignatureContainer getNegotiationSignatureContainer() {
		return getPayload();
	}
}
