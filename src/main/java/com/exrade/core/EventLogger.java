package com.exrade.core;

import com.exrade.models.event.LogEventType;
import com.exrade.models.history.NegotiationEvent;
import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.processmodel.protocol.Transition;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.engine.StateMachine;
import com.exrade.util.ContextHelper;

public class EventLogger {

	public static void logNegotiationEvent(Negotiation negotiation, Negotiator negotiatorTriggerer,Transition transition){
		logNegotiationEventWithNote(negotiation, negotiatorTriggerer, transition, null);
	}
	
	public static void logNegotiationEventWithNote(Negotiation negotiation, Negotiator negotiatorTriggerer,Transition transition,String note){
		StateMachine userStateMachine = negotiation.getStateMachine(negotiatorTriggerer.getIdentifier());
		Negotiator actualActor = ContextHelper.getMembership() != null? ContextHelper.getMembership() : negotiatorTriggerer;
		
		NegotiationEvent negotiationEvent = new NegotiationEvent(negotiation.getUuid(), actualActor, transition, note);
		// log only change of states, not circular transitions
		if (!transition.getSource().equals(transition.getTarget())){
			userStateMachine.getEvents().add(negotiationEvent);
		}
	}
	
	public static void logNegotiationEvent(Negotiation negotiation,Negotiator negotiatorTriggerer, Negotiator negotiatorInvolved,
			LogEventType logEventType) {
		logNegotiationEventWithNote(negotiation, negotiatorTriggerer, negotiatorInvolved, logEventType, null, null);
	}
	
	public static void logNegotiationEvent(Negotiation negotiation,Negotiator negotiatorTriggerer, Negotiator negotiatorInvolved,
			LogEventType logEventType, NegotiationMessage negotiationMessage) {
		if(negotiationMessage == null)
			logNegotiationEventWithNote(negotiation, negotiatorTriggerer, negotiatorInvolved, logEventType, negotiationMessage, null);
		else
			logNegotiationEventWithNote(negotiation, negotiatorTriggerer, negotiatorInvolved, logEventType, negotiationMessage, negotiationMessage.getNote());
	}
	
	public static void logNegotiationEventWithNote(Negotiation negotiation, Negotiator negotiatorTriggerer,Negotiator negotiatorInvolved,
			LogEventType logEventType, String note){
		logNegotiationEventWithNote(negotiation, negotiatorTriggerer, negotiatorInvolved, logEventType, null,note);
	}
	
	public static void logNegotiationEventWithNote(Negotiation negotiation,Negotiator negotiatorTriggerer, Negotiator negotiatorInvolved,
			LogEventType logEventType, NegotiationMessage negotiationMessage,String note) {
		StateMachine userStateMachine = negotiation.getStateMachine(negotiatorTriggerer.getIdentifier());
		Negotiator actualActor = ContextHelper.getMembership() != null && negotiatorTriggerer.getProfile().getUuid().equals(ContextHelper.getMembership().getProfile().getUuid())
									? ContextHelper.getMembership() : negotiatorTriggerer;
		NegotiationEvent negotiationEvent = new NegotiationEvent(negotiation.getUuid(), actualActor, 
				negotiatorInvolved,logEventType, negotiationMessage, note);
		userStateMachine.getEvents().add(negotiationEvent);
	}
	
}
