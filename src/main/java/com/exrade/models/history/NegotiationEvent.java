package com.exrade.models.history;


import com.exrade.models.event.Event;
import com.exrade.models.event.LogEventType;
import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.models.processmodel.protocol.Transition;
import com.exrade.models.userprofile.Negotiator;
/**
 * A LogEvent is any meaningful occurrence that happens within a negotiation
 * 
 * @author jasonfinnegan
 *
 */
public class NegotiationEvent extends Event {

	public Negotiator otherUser;  //if this event involves another user, message sent or received, user joins etc.
	
	public NegotiationMessage negotiationMessage; //if a message sent/received event then this point to the relevant message
	
	public String transitionSource;
	
	public String transitionTarget;

	public NegotiationEvent(){
	}
	
	public NegotiationEvent(String negotiationID, Negotiator user,Negotiator otherUser,
			LogEventType logEventType, NegotiationMessage negotiationMessage, String note) {
		super(user,logEventType,negotiationID,System.currentTimeMillis(), note);
		this.otherUser=otherUser;
		this.negotiationMessage = negotiationMessage;
	}
	
	public NegotiationEvent(String negUUID,Negotiator user,Transition transition, String note){
		super(user,LogEventType.TRANSITION_TAKEN,negUUID,System.currentTimeMillis(), note);
		this.transitionSource = transition.getSource().getName();
		this.transitionTarget = transition.getTarget().getName();
	}
	
	public String getNegotiation() {
		return getObjectUUID();
	}
	public Negotiator getOtherUser() {
		return otherUser;
	}
	
	public NegotiationMessage getNegotiationMessage() {
		return negotiationMessage;
	}
	
	public String getMessageUUID(){
		if (getNegotiationMessage() != null){
			return getNegotiationMessage().getUuid();
		}
		return null;
	}
	
	public String getMessageType(){
		if (getNegotiationMessage() != null){
			return getNegotiationMessage().getMessageType();
		}
		return null;
	}

	/**
	 * Return current negotiation event note, if it's null it tries to retrieve it from the attached message
	 */
	public String getNote() {
		if (note == null) {
			if (getNegotiationMessage() != null && getNegotiationMessage().getNote() != null){
				return getNegotiationMessage().getNote();
			}
		}
		return note;
	}

	public String getTransitionSource() {
		return transitionSource;
	}

	public String getTransitionTarget() {
		return transitionTarget;
	}
	
}