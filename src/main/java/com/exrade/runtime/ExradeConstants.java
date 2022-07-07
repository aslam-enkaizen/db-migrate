package com.exrade.runtime;

import com.exrade.models.processmodel.protocol.stages.InitialStage;

public class ExradeConstants {

	//negotiation types are string , as in futore custom negotiaitons will be added
	//simple negotiations (to be replaced
	public static final String LOWEST_PRICE_SEALED = "LOWEST_PRICE_SEALED";
	public static final String HIGHEST_PRICE_SEALED = "HIGHEST_PRICE_SEALED";
	public static final String ENGLISH_AUCTION = "ENGLISH_AUCTION";
	public static final String REVERSE_AUCTION = "REVERSE_AUCTION";
	public static final String VICKREY_AUCTION = "VICKREY_AUCTION";

	public static final String AGREED_STAGE_NAME = "Agreed";
	public static final String NOT_AGREED_STAGE_NAME = "Not_Agreed";
	public static final String CANCELED_STAGE_NAME = "Canceled";
	public static final String INITIAL_STAGE_NAME = InitialStage.class.getSimpleName();
	public static final String SIGNATURE_STAGE_NAME = "SIGNATURE";
	
	
	//every Item to be used for a negotiation must use a an attribute named PRICE in an Issue called PRICE
	//this is needed for all auctions and other proceses where prices is an essential attribute
	//  Remove and use a specifc parameter price in all negotiations
	public static final String PAYMENT_METHOD = "payment method";
	
	//Categories, these need to reference existing standards, ]
	//for now just use some static categories
	
	public static final String TRANSPORT="TRANSPORT";
	public static final String SERVICES="SERVICES";
	public static final String VEHICLES="VEHICLES";
	
	
	
	public static final String RESERVE_NOT_MET = "RESERVE_NOT_MET";
	
	
	//Bidding responses
	public static final String BID_VALID="BID_VALID";
	public static final String BID_INVALID="BID_INVALID";
	public static final String BID_TOO_HIGH="BID_TOO_HIGH";
	public static final String BID_TOO_LOW="BID_TOO_LOW";

	//Negotiation start and end date
	public static final String START_DATE = "START_DATE";
	public static final String END_DATE = "END_DATE";


	public static final String SINGLE_OFFER_MARKED_ACCEPT="SINGLE_OFFER_MARKED_ACCEPT";   //owner only
	

	//Log Event messages
	public static final String TRANSITION_TAKEN = "TRANSITION TAKEN";
	public static final String MESSAGE_SENT = "MESSAGE SENT";
	public static final String MESSAGE_RECEIVED = "MESSAGE RECEIVED";
	public static final String PARTICIPANT_JOINED = "PARTICIPANT JOINED";
	public static final String ADMISSION_REQUEST_RECEIVED = "ADMISSION REQUEST RECEIVED";
	
	//Do Action Names
	public static final String EVALUATEOFFER_DOACTION="EVALUATEOFFER";
	public static final String CREATEOFFER_DOACTION="CREATEOFFER";
	
}
