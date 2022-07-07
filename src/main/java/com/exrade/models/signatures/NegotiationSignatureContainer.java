package com.exrade.models.signatures;

import com.exrade.models.messaging.Agreement;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthenticationException;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ContextHelper;

import java.util.ArrayList;
import java.util.List;

public class NegotiationSignatureContainer extends BaseEntityUUIDTimeStampable{
	
	private String negotiationID;
	private String originalAgreementPDFUUID;
	private String finalSignedAgreementPDFUUID;
	private RegisteredSignedDocument nextToSign;
	private SignatureType signatureType;

	private List<RegisteredSignedDocument> allUploaded = new ArrayList<RegisteredSignedDocument>();

	public NegotiationSignatureContainer(){}

	
	public List<RegisteredSignedDocument> getAllUploaded() {
		return allUploaded;
	}

	public void setAllUploaded(List<RegisteredSignedDocument> allUploaded) {
		this.allUploaded = allUploaded;
	}

	public void setOriginalAgreementPDFUUID(String originalAgreementPDFUUID) {
		this.originalAgreementPDFUUID = originalAgreementPDFUUID;
	}

	/**
	 * create a new NegotiationSignatureContainer,
	 * @param negotiationID
	 * @param agreement
	 */
	public NegotiationSignatureContainer(String negotiationID, Agreement agreement, List<Negotiator> agreementSigners) {
		setNegotiationID(negotiationID);
		
		for (Negotiator negotiator : agreementSigners) {
			boolean existsNegotiator = false;
			for(RegisteredSignedDocument registeredSignedDocument : getAllUploaded()){
				if(negotiator.getIdentifier().equals(registeredSignedDocument.getSignerUUID())){
					existsNegotiator = true;
					break;
				}
			}
			if(!existsNegotiator)
				allUploaded.add(new RegisteredSignedDocument(negotiator.getIdentifier(), null, "unsigned"));
		} 
		setNextToSign(allUploaded.get(0));
	}
	
	public String getOriginalAgreementPDFUUID() {
		return originalAgreementPDFUUID;
	}

	public String getFinalSignedAgreementPDFUUID() {
		return finalSignedAgreementPDFUUID;
	}


	public String getNegotiationID() {
		return negotiationID;
	}

	public void setNegotiationID(String negotiationID) {
		this.negotiationID = negotiationID;
	}

	public void setNextToSign(RegisteredSignedDocument nextToSign) {
		this.nextToSign = nextToSign;
	}
	
	public RegisteredSignedDocument getNextToSign() {
		return nextToSign;
	}

	public void setFinalSignedAgreementPDFUUID(String finalSignedAgreementPDFUUID) {
		this.finalSignedAgreementPDFUUID = finalSignedAgreementPDFUUID;
	}


	/**
	 * in this method we register the users uploaded file, and update the next signer and file pointers
	 * @param iUserUUID
	 * @param fileUUID
	 */
	
	public void registerSignedAgreement(String iUserUUID, String fileUUID,String secretKey) {
		for(int i=0;i<getAllUploaded().size();i++){
			RegisteredSignedDocument signedDoc = getAllUploaded().get(i);
				if(signedDoc.getSignerUUID().equals(iUserUUID)){
					
					if(!secretKey.equalsIgnoreCase(signedDoc.getSecretSignkey())){
						 throw new ExAuthenticationException(ErrorKeys.SECRET_SIGN_KEY_INCORRECT);
					}
					
					signedDoc.setFileUUID(fileUUID);
					signedDoc.setSigningstatus("signed");
					signedDoc.setIpAddress(ContextHelper.getIpAddress());
					if(signedDoc.getSignatureDate() == null)
						signedDoc.setSignatureDate(TimeProvider.now());
					
					if(getAllUploaded().size()>i+1){
						//there are more to sign
						setNextToSign(getAllUploaded().get(i+1));
					}
					else{ //signing finished
						setNextToSign(null);
						setFinalSignedAgreementPDFUUID(fileUUID);
					}
				}
			}
		}


	public SignatureType getSignatureType() {
		return signatureType;
	}


	public void setSignatureType(SignatureType signatureType) {
		this.signatureType = signatureType;
	}
	
	public boolean isNextSigner() {
		if(ContextHelper.getMembership() != null && getNextToSign() != null) {
			if(ContextHelper.getMembership().getIdentifier().equals(getNextToSign().getSignerUUID()))
				return true;
		}
		return false;
	}
}
