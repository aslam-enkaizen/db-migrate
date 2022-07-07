package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.messaging.Agreement;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.signatures.NegotiationSignatureContainer;
import com.exrade.models.signatures.Signer;

import java.util.List;

public interface SignatureAPI {

	/**
	 * Saves this fileUUID in the agreement Message for this negotiation
	 * @param userID
	 * @param negotiationID
	 * @param fileUUID
	 * @param secretLey a secrte code that is supplied to the user via their legal email address
	 * @return true if valid
	 */
	boolean registerSignedAgreement(ExRequestEnvelope request, String negotiationID, String fileUUID, String secretKey);
	
	boolean signAgreement(ExRequestEnvelope request, String negotiationID, String signature, String secretKey);

	/**
	 * get all details abouts the signing status of this negotiation
	 * @param userUUID
	 * @param negotiationID
	 * @return
	 */
	NegotiationSignatureContainer getRegisterSignedContainer(ExRequestEnvelope request, String negotiationID, String secretKey);
	
	void createSignatureContainer(ExRequestEnvelope request, Negotiation negotiation, Agreement agreement);
	
	void updateSigners(ExRequestEnvelope request, String negotiationID, List<Signer> signers);
	
	void resendSecretSignkey(ExRequestEnvelope request, String negotiationID);
	
	void createSignatureContainer(ExRequestEnvelope request, String negotiationUUID);
}