package com.exrade.models.signatures;

import com.exrade.platform.persistence.BaseEntityUUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

public class RegisteredSignedDocument extends BaseEntityUUID {
	
	private String fileUUID;
	private String signerUUID;
	private String signature;
	private Date signatureDate;
	private Integer signingOrder;
	private String ipAddress;
	
	@JsonIgnore
	private String secretSignkey;
	
	/**
	 * cureent status of the siging
	 */
	private String signingstatus; 
	
	public RegisteredSignedDocument(){}
	
	public RegisteredSignedDocument(String signerUUID, String fileID, String status){
		this.setSignerUUID(signerUUID);
		this.setFileUUID(fileID);
		this.setSigningstatus(status);
		
		//create secret key
		SecureRandom random = new SecureRandom();
		this.setSecretSignkey(new BigInteger(50, random).toString(32));
		  
	}
	
	public String getFileUUID() {
		return fileUUID;
	}
	public void setFileUUID(String fileUUID) {
		this.fileUUID = fileUUID;
	}

	public String getSigningstatus() {
		return signingstatus;
	}
	public void setSigningstatus(String signingstatus) {
		this.signingstatus = signingstatus;
	}

	public String getSignerUUID() {
		return signerUUID;
	}

	public void setSignerUUID(String signerUUID) {
		this.signerUUID = signerUUID;
	}

	public String getSecretSignkey() {
		return secretSignkey;
	}

	private void setSecretSignkey(String secretSignkey) {
		this.secretSignkey = secretSignkey;
	}

	@JsonIgnore
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Date getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(Date signatureDate) {
		this.signatureDate = signatureDate;
	}

	public Integer getSigningOrder() {
		return signingOrder;
	}

	public void setSigningOrder(Integer signingOrder) {
		this.signingOrder = signingOrder;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
