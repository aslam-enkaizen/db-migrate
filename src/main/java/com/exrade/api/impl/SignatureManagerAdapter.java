package com.exrade.api.impl;

import com.exrade.api.SignatureAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.messaging.Agreement;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.signatures.NegotiationSignatureContainer;
import com.exrade.models.signatures.Signer;
import com.exrade.runtime.signatures.ISignatureManager;
import com.exrade.runtime.signatures.SignatureManager;
import com.exrade.util.ContextHelper;

import java.util.List;

public class SignatureManagerAdapter implements SignatureAPI {

	private ISignatureManager manager = new SignatureManager();
	
	@Override
	public boolean registerSignedAgreement(ExRequestEnvelope request,
			String negotiationID, String fileUUID, String secretKey) {
		ContextHelper.initContext(request);
		return manager.registerSignedAgreement(negotiationID, fileUUID, secretKey);
	}

	@Override
	public NegotiationSignatureContainer getRegisterSignedContainer(
			ExRequestEnvelope request, String negotiationID, String secretKey) {
		ContextHelper.initContext(request);
		return manager.getRegisterSignedContainer(negotiationID, secretKey);
	}

	@Override
	public void createSignatureContainer(ExRequestEnvelope request,
			Negotiation negotiation, Agreement agreement) {
		ContextHelper.initContext(request);
		manager.createSignatureContainer(negotiation, agreement);
	}

	@Override
	public boolean signAgreement(ExRequestEnvelope request,
			String negotiationID, String signature, String secretKey) {
		ContextHelper.initContext(request);
		return manager.signAgreement(negotiationID, signature, secretKey);
	}

	@Override
	public void resendSecretSignkey(ExRequestEnvelope request,
			String negotiationID) {
		ContextHelper.initContext(request);
		manager.resendSecretSignkey(negotiationID);
	}

	@Override
	public void updateSigners(ExRequestEnvelope request, String negotiationID, List<Signer> signers) {
		ContextHelper.initContext(request);
		manager.updateSigners(negotiationID, signers);
	}
	
	@Override
	public void createSignatureContainer(ExRequestEnvelope request, String negotiationUUID) {
		ContextHelper.initContext(request);
		manager.createSignatureContainerAsOwner(negotiationUUID);
	}

}
