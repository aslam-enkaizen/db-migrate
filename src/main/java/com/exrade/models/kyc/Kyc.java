package com.exrade.models.kyc;

import com.exrade.models.messaging.Offer;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

public class Kyc extends BaseEntityUUIDTimeStampable {

	private String rawRequest;
	private String rawResponse;
	private String rawDocumentUploadRequest;
	private String rawDocumentUploadResponse;
	private String rawDocumentVerificationResponse;
	private String documentUID;
	private boolean documentVerificationResultPending;
	private Negotiation negotiation;
	private Offer offer;
	private String bundleName;
	private String serviceName;
	private String serviceCallReference;
	private String interpretedResult;

	public String getRawRequest() {
		return rawRequest;
	}

	public void setRawRequest(String rawRequest) {
		this.rawRequest = rawRequest;
	}

	public String getRawResponse() {
		return rawResponse;
	}

	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}

	public String getRawDocumentUploadRequest() {
		return rawDocumentUploadRequest;
	}

	public void setRawDocumentUploadRequest(String rawDocumentUploadRequest) {
		this.rawDocumentUploadRequest = rawDocumentUploadRequest;
	}

	public String getRawDocumentUploadResponse() {
		return rawDocumentUploadResponse;
	}

	public void setRawDocumentUploadResponse(String rawDocumentUploadResponse) {
		this.rawDocumentUploadResponse = rawDocumentUploadResponse;
	}

	public String getRawDocumentVerificationResponse() {
		return rawDocumentVerificationResponse;
	}

	public void setRawDocumentVerificationResponse(String rawDocumentVerificationResponse) {
		this.rawDocumentVerificationResponse = rawDocumentVerificationResponse;
	}

	public String getDocumentUID() {
		return documentUID;
	}

	public void setDocumentUID(String documentUID) {
		this.documentUID = documentUID;
	}

	public boolean isDocumentVerificationResultPending() {
		return documentVerificationResultPending;
	}

	public void setDocumentVerificationResultPending(boolean documentVerificationResultPending) {
		this.documentVerificationResultPending = documentVerificationResultPending;
	}

	public Negotiation getNegotiation() {
		return negotiation;
	}

	public void setNegotiation(Negotiation negotiation) {
		this.negotiation = negotiation;
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public String getBundleName() {
		return bundleName;
	}

	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceCallReference() {
		return serviceCallReference;
	}

	public void setServiceCallReference(String serviceCallReference) {
		this.serviceCallReference = serviceCallReference;
	}

	public String getInterpretedResult() {
		return interpretedResult;
	}

	public void setInterpretedResult(String interpretedResult) {
		this.interpretedResult = interpretedResult;
	}

}
