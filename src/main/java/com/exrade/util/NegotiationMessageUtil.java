package com.exrade.util;

import com.exrade.core.ExLogger;
import com.exrade.models.messaging.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NegotiationMessageUtil {
	
	/*
	 * return true if this message mathces this event 
	 */
	public static boolean messageMatchesEvent(String recMessage, NegotiationMessage message){
		if (recMessage == null || message == null)
			return false;
		if(recMessage.equals(message.getMessageType()))
			return true;
		if(recMessage.equalsIgnoreCase(OfferResponse.class.getSimpleName())){
			if(message.getMessageType().equalsIgnoreCase(AcceptOfferResponse.class.getSimpleName())||
					message.getMessageType().equalsIgnoreCase(RejectOfferResponse.class.getSimpleName())){
				return true;
			}
		}
		return false;
	}
	
	
	// return true if infoMessages are the same typ, including check for proxy
	public static boolean compare(NegotiationMessage msg1, String iMessageType) {
		boolean result = false;
		String msg1Str = msg1.getClass().getSimpleName();
		if (msg1Str.contains("_$$_")) {
			msg1Str = msg1Str.substring(0, msg1Str.indexOf("_$$_"));
		}

		if (msg1Str.equalsIgnoreCase(iMessageType)) {
			result = true;
		}
		
		//special case for accept/reject offerresponse
		if(iMessageType.equalsIgnoreCase(OfferResponse.class.getSimpleName())){
			if(msg1.getMessageType().equalsIgnoreCase(AcceptOfferResponse.class.getSimpleName())||
					msg1.getMessageType().equalsIgnoreCase(RejectOfferResponse.class.getSimpleName())){
						return true;
			}
		}
		if(iMessageType.equalsIgnoreCase(CounterOffer.class.getSimpleName()) 
				&& msg1.getMessageType().equalsIgnoreCase(CounterOffer.class.getSimpleName())){
			return true;
		}
		return result;

	}

	/**
	 * This method deserialize json representation of NegotiationMessage subclasses
	 * 
	 * @param msgString
	 * @return
	 */
	public static NegotiationMessage deserialize(String msgString){
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode rootNode;
		try {
			rootNode = mapper.readTree(mapper.getFactory().createParser(msgString));
			//TODO: take hard coded package name into some other static class
			String messageType = "com.exrade.models.messaging." + rootNode.findValue("messageType").textValue();
			NegotiationMessage negotiationMessage = (NegotiationMessage) mapper.readValue(msgString, Class.forName(messageType).newInstance().getClass());
			return negotiationMessage;
		} catch (Exception e) {
			ExLogger.get().error("Error deserialization message",e);
			return null;
		}
	}
	
	public static String[] getIgnorableProperties(){
		String[] excludes = {"sender", "receiver"};
		return excludes;
	}
}
