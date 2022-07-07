package com.exrade.models.userprofile.security;


public class NegotiationRole extends ExRole {

	public final static String OWNER = "negotiation.owner";
	public final static String PARTICIPANT = "negotiation.participant";
	public final static String REVIEWER = "negotiation.reviewer";
	public final static String ADMINISTRATOR = "negotiation.administrator";
	public final static String SIGNER = "negotiation.signer";
	public final static String ANONYMOUS = "anonymous";

	public NegotiationRole(){} 
	
	public NegotiationRole(String iName) {
		super(iName);
	}
	
}
