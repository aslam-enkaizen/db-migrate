package com.exrade.runtime.signatures;

import com.exrade.models.messaging.Agreement;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.signatures.NegotiationSignatureContainer;
import com.exrade.models.signatures.Signer;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface ISignatureManager {

	/**
	 * Saves this fileUUID in the agreement Message for this negotiation
	 * @param userID
	 * @param negotiationID
	 * @param fileUUID
	 * @param secretLey a secrte code that is supplied to the user via their legal email address
	 * @return true if valid
	 */
	boolean registerSignedAgreement(String negotiationID,String fileUUID,String secretKey);

	/**
	 * get all details abouts the signing status of this negotiation
	 * @param userUUID
	 * @param negotiationID
	 * @return
	 */
	NegotiationSignatureContainer getRegisterSignedContainer(String negotiationID);
	
	NegotiationSignatureContainer getRegisterSignedContainer(String negotiationID, String secretKey);
	
	void createSignatureContainer(Negotiation negotiation, Agreement agreement);
	
	List<Signer> updateSigners(String negotiationID, List<Signer> signers);
	
	boolean isSignaturePending(String negotiationID);
	
	boolean isSignaturePendingForRequestor(String negotiationID);
	
	List<NegotiationSignatureContainer> find(QueryFilters iFilters);

	boolean signAgreement(String negotiationID, String signature,
			String secretKey);
	
	void resendSecretSignkey(String negotiationID);

	String updateSigners(String iNegotiationID, String template);

	List<Negotiator> bindNegotiators(List<Signer> signers);

	List<Signer> extractSignersFromTemplate(String template);

	String attachSignersToTemplate(String template, List<Signer> signers);

	void createSignatureContainerAsOwner(String negotiationUUID);
	
	void createSignatureContainerAsOwner(Negotiation negotiation, Agreement agreement);

	boolean canCreateSignatureContainer(Negotiation negotiation, Agreement agreement);
}