package com.exrade.runtime.signatures;

import com.exrade.Messages;
import com.exrade.core.ExLogger;
import com.exrade.models.activity.Verb;
import com.exrade.models.messaging.Agreement;
import com.exrade.models.messaging.Offer;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.negotiation.UserAdmissionStatus;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.signatures.NegotiationSignatureContainer;
import com.exrade.models.signatures.RegisteredSignedDocument;
import com.exrade.models.signatures.Signer;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthorizationException;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.contract.ContractManager;
import com.exrade.runtime.filemanagement.FileManager;
import com.exrade.runtime.filemanagement.IFileManager;
import com.exrade.runtime.mail.EmailSender;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.SignatureNotificationEvent;
import com.exrade.runtime.report.AgreementPdfGenerator;
import com.exrade.runtime.signatures.persistence.SignaturePersistenceManager;
import com.exrade.runtime.sms.SmsSender;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.runtime.userprofile.TraktiJwtManager;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignatureManager implements ISignatureManager{

	private static final Logger LOGGER = ExLogger.get();

	private SignaturePersistenceManager signaturePersistentManager;
	private INegotiationManager negManager=new NegotiationManager();
	private NotificationManager notificationManager = new NotificationManager();
	private IMembershipManager membershipManager = new MembershipManager();

	public SignatureManager() {
		this(new SignaturePersistenceManager());
	}

	public SignatureManager(SignaturePersistenceManager iProjectPersistentManager) {
		this.signaturePersistentManager = iProjectPersistentManager;
	}


	/**
	 * Create a new signature container, used to track the progress of signing the agreement
	 * @param negotiationID
	 */
	@Override
	public void createSignatureContainer(Negotiation neg, Agreement agreement){
		if(neg.isAgreementSigningEnabled()){
			NegotiationSignatureContainer negSigContainer = buildNegotiationSignatureContainer(neg, agreement);

			String agreementDocumentId = new AgreementPdfGenerator().generate(neg, agreement, buildSignerDataMapForPdf(negSigContainer));
			if(!Strings.isNullOrEmpty(agreementDocumentId)){
				agreement.setAgreementDocumentID(agreementDocumentId);
				negSigContainer.setOriginalAgreementPDFUUID(agreementDocumentId);
				notifyUsertoSign(negSigContainer.getNextToSign().getSignerUUID(),neg,
						negSigContainer.getNextToSign().getSecretSignkey());
				signaturePersistentManager.create(negSigContainer);
				LOGGER.info("Signature container created: negotiationUUID-{}, agreementUUID-{}", neg.getUuid(), agreement.getUuid());
			}
			else{
				LOGGER.warn("Failed to create signature container: negotiationUUID-{}", neg.getUuid());
			}
		}
		else {
			String agreementDocumentId = new AgreementPdfGenerator().generate(neg, agreement, null);
			if(!Strings.isNullOrEmpty(agreementDocumentId)){
				agreement.setAgreementDocumentID(agreementDocumentId);
				LOGGER.info("Agreement document created: negotiationUUID-{}, agreementUUID-{}", neg.getUuid(), agreement.getUuid());
			}
			else{
				LOGGER.warn("Failed to create agreement document: negotiationUUID-{}", neg.getUuid());
			}
		}

		IFileManager fileManager = new FileManager();
		fileManager.updateFileMetadata(neg, agreement);
	}


	@Override
	public boolean registerSignedAgreement(String negotiationID,String fileUUID,String secretkey) {

		NegotiationSignatureContainer negSigContainer = signaturePersistentManager.readbyUUID(negotiationID);
		if(negSigContainer != null){
			INegotiationManager negManager=new NegotiationManager();

			IFileManager fileManager = new FileManager();
			if(fileManager.getFileAsByteArray(fileUUID) != null){

				//check agreement is correct here (matches uplaode file) //TODO

				if(ContextHelper.getMembershipUUID().equals(negSigContainer.getNextToSign().getSignerUUID())){
					negSigContainer.registerSignedAgreement(ContextHelper.getMembershipUUID(),fileUUID,secretkey);
					signaturePersistentManager.update(negSigContainer);

					Negotiation negotiation = negManager.getNegotiation(negotiationID);
					Negotiator involvedNegotiator = negManager.getInvolvedNegotiator(negotiation, ContextHelper.getMembership());
					List<Agreement> agreements = negotiation.getMessageBox().getAgreements(involvedNegotiator);

					fileManager.updateFileMetadata(negotiation, agreements.get(0), negSigContainer, fileUUID);

					if(negSigContainer.getNextToSign()!=null){
						notifyUsertoSign(negSigContainer.getNextToSign().getSignerUUID(),negotiation,
							negSigContainer.getNextToSign().getSecretSignkey());
					}
					else {
						notificationManager.process(new SignatureNotificationEvent(NotificationType.SIGNATURE_COMPLETED, negSigContainer, negotiation));

						try {
							new ContractManager().createContract(negotiation);
						}
						catch(Exception ex) {
							LOGGER.warn("Failed to create contract: negotiationUUID-{}", negotiationID);
						}
					}
					ActivityLogger.log((Membership)ContextHelper.getMembership(), Verb.SIGN, agreements.get(0), negotiation, agreements.get(0).getAgreedParticipants());
					LOGGER.info("Registered signed pdf agreement: negotiationUUID-{}, fileUUID-{}", negotiationID, fileUUID);
					return true;
				}
			}
		}
		LOGGER.warn("Failed to register signed pdf agreement: negotiationUUID-{}, fileUUID-{}", negotiationID, fileUUID);
		return false; //could not save the signed pdf refernece

	}

	@Override
	public NegotiationSignatureContainer getRegisterSignedContainer(String negotiationID){
		return getRegisterSignedContainer(negotiationID, null);
	}

	@Override
	public NegotiationSignatureContainer getRegisterSignedContainer(String negotiationID, String secretKey){

		// check user is permitted
		NegotiationSignatureContainer negSigCont=signaturePersistentManager.readbyUUID(negotiationID);
		if(negSigCont != null){
			Negotiation negotiation = negManager.getNegotiation(negotiationID);
			if(!Strings.isNullOrEmpty(secretKey)) {
				for(RegisteredSignedDocument registeredSignedDocument : negSigCont.getAllUploaded()) {
					if(secretKey.equals(registeredSignedDocument.getSecretSignkey()))
						return negSigCont;
				}
			}
			else {
				Negotiator negotiator = negManager.getInvolvedNegotiator(negotiation, ContextHelper.getMembership());

				if (negotiation != null && negotiator != null) {
					return negSigCont;
				}
			}


//			for(RegisteredSignedDocument regDoc : negSigCont.getAllUploaded()){
//
//				if(ContextHelper.getMembershipUUID().equalsIgnoreCase(regDoc.getSignerUUID())){
//					return negSigCont;
//				}
//			}
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
		}
		else{
			throw new ExNotFoundException(negotiationID);
		}
	}
	
	@Override
	public boolean isSignaturePending(String negotiationID){
		try{
			NegotiationSignatureContainer signatureContainer = signaturePersistentManager.readbyUUID(negotiationID);
			if(signatureContainer != null && signatureContainer.getNextToSign() != null)
				return true;
		}
		catch(Exception ex){}
		return false;
	}

	@Override
	public boolean isSignaturePendingForRequestor(String negotiationID){
		try{
			NegotiationSignatureContainer signatureContainer = getRegisterSignedContainer(negotiationID, null);
			if(signatureContainer != null && signatureContainer.getNextToSign() != null && ContextHelper.getMembershipUUID().equalsIgnoreCase(signatureContainer.getNextToSign().getSignerUUID()))
				return true;
		}
		catch(Exception ex){}
		return false;
	}


	/**
	 * This send an email to the user via their legal address, notifying them that it is their turn to sign the
	 * aggreement and giving them the secret key needed to upload the file
	 * @param user
	 * @param neg
	 * @param secretkey
	 */
	private void notifyUsertoSign(String userUUID,Negotiation neg, String secretkey ){
		Membership userToNotify = (Membership)getNegotiator(userUUID);

		if(!Strings.isNullOrEmpty(userToNotify.getEmail())) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("secretKey", secretkey);
			if(userToNotify.isGuest())
				data.put("url", ExConfiguration.getStringProperty("site.url") + "/negotiation/" + neg.getUuid() + "/view-agreement/" + "?token=" + TraktiJwtManager.getInstance().generateToken(userUUID));
			else
				data.put("url", ExConfiguration.getStringProperty("site.url") + "/negotiation/" + neg.getUuid() + "/view-agreement/");

			EmailSender emailSender = new EmailSender();
			emailSender.send(userToNotify, neg, "email-sign-agreement", data);
		}
		else if(!Strings.isNullOrEmpty(userToNotify.getPhone())) {
			SmsSender.getInstance().send(Messages.get("SIGN_AGREEMENT_CODE", StringUtils.left(neg.getTitle(), 50), secretkey), userToNotify.getPhone(), neg.getOwner().getProfile().getUuid());
		}
	}

	@Override
	public List<NegotiationSignatureContainer> find(QueryFilters iFilters) {
		return signaturePersistentManager.find(iFilters);
	}

	@Override
	public boolean signAgreement(String negotiationID, String signature,
			String secretKey) {

		NegotiationSignatureContainer negSigContainer = signaturePersistentManager.readbyUUID(negotiationID);
		if(negSigContainer != null){
			INegotiationManager negManager=new NegotiationManager();

			if(!Strings.isNullOrEmpty(signature)){

				//check agreement is correct here (matches uplaode file) //TODO
				// get signer from secrectKey | next to sign
				Negotiator nextToSign = getNegotiator(negSigContainer.getNextToSign().getSignerUUID());

				if(ContextHelper.getMembershipUUID().equals(negSigContainer.getNextToSign().getSignerUUID())
						|| (nextToSign.isGuest() && nextToSign.getProfile().getUuid().equals(ContextHelper.getMembership().getProfile().getUuid()))){
					negSigContainer.getNextToSign().setSignature(signature);
					negSigContainer.getNextToSign().setSignatureDate(TimeProvider.now());
					Negotiation negotiation = negManager.getNegotiation(negotiationID);
					Negotiator involvedNegotiator = negManager.getInvolvedNegotiator(negotiation, ContextHelper.getMembership());
					if(negotiation != null){
						List<Agreement> agreements = negotiation.getMessageBox().getAgreements(involvedNegotiator);

						//TODO: handle multiple agreement
						String fileUUID = new AgreementPdfGenerator().generate(negotiation, agreements.get(0), buildSignerDataMapForPdf(negSigContainer));
						negSigContainer.registerSignedAgreement(nextToSign.getIdentifier(), fileUUID, secretKey);
						signaturePersistentManager.update(negSigContainer);

						IFileManager fileManager = new FileManager();
						fileManager.updateFileMetadata(negotiation, agreements.get(0), negSigContainer, fileUUID);

						if(negSigContainer.getNextToSign()!=null){
							notifyUsertoSign(negSigContainer.getNextToSign().getSignerUUID(),negManager.getNegotiation(negotiationID),
								negSigContainer.getNextToSign().getSecretSignkey());
						}
						else {
							notificationManager.process(new SignatureNotificationEvent(NotificationType.SIGNATURE_COMPLETED, negSigContainer, negotiation));
							try {
								new ContractManager().createContract(negotiation);
							}
							catch(Exception ex) {
								LOGGER.warn("Failed to create contract: negotiationUUID-{}", negotiationID);
							}
						}

						ActivityLogger.log((Membership)nextToSign, Verb.SIGN, agreements.get(0), negotiation, agreements.get(0).getAgreedParticipants());
						LOGGER.info("Registered signed pdf agreement: negotiationUUID-{}, fileUUID-{}", negotiationID, fileUUID);
						return true;
					}
				}
			}
		}
		LOGGER.warn("Failed to sign pdf agreement: negotiationUUID-{}", negotiationID);
		return false; //could not save the signed pdf refernece
	}

	private List<Map<String, Object>> buildSignerDataMapForPdf(NegotiationSignatureContainer negSigContainer){
		List<Map<String, Object>> signers = new ArrayList<Map<String,Object>>();

		for(RegisteredSignedDocument registeredSignedDocument : negSigContainer.getAllUploaded()){
			if(registeredSignedDocument.getSignerUUID().equals(negSigContainer.getNextToSign().getSignerUUID())){
				registeredSignedDocument.setSignature(negSigContainer.getNextToSign().getSignature());
				registeredSignedDocument.setSignatureDate(negSigContainer.getNextToSign().getSignatureDate());
			}

			Map<String, Object> signer = new HashMap<String, Object>();
			signer.put("membership", getNegotiator(registeredSignedDocument.getSignerUUID()));
			signer.put("signatureDate", registeredSignedDocument.getSignatureDate());
			signer.put("signature", registeredSignedDocument.getSignature());
			signer.put("secretSignkey", registeredSignedDocument.getSecretSignkey());

			signers.add(signer);
		}

		return signers;
	}

	@Override
	public void resendSecretSignkey(String negotiationID) {
		NegotiationSignatureContainer negSigContainer = signaturePersistentManager.readbyUUID(negotiationID);

		if(negSigContainer.getNextToSign() != null){
			INegotiationManager negManager=new NegotiationManager();
			Negotiation negotiation = negManager.getNegotiation(negotiationID);


			notifyUsertoSign(negSigContainer.getNextToSign().getSignerUUID(),negotiation,
					negSigContainer.getNextToSign().getSecretSignkey());
			LOGGER.info("Resent secretSignKey: negotiationUUID-{}, membershipUUID-{}", negotiationID, negSigContainer.getNextToSign().getSignerUUID());
		}
	}

	private List<Negotiator> updateSigningSequence(Negotiation negotiation, List<Negotiator> agreementSigners){
		try {
			List<Negotiator> signers = new ArrayList<>();

			for(Negotiator agreementSigner : agreementSigners) {

				if(!negotiation.getOwner().getProfile().getUuid().equals(agreementSigner.getProfile().getUuid())){
					signers.add(0, agreementSigner);
				}
				else {
					signers.add(agreementSigner);
				}
			}

			return signers;
		}
		catch(Exception ex) {
			return agreementSigners;
		}
	}

	@Override
	public List<Signer> updateSigners(String negotiationID, List<Signer> signers) {
		Negotiation negotiation = negManager.getNegotiation(negotiationID);
		if(!canUpdateSigner(negotiation))
			throw new ExException("Updating signatory is not allowed");

		negManager.updateAgreementSigners(negotiation, bindNegotiators(signers));

		List<Agreement> agreements = negotiation.getMessageBox().getAgreements(ContextHelper.getMembership());
		for(Agreement agreement : agreements) {
			Offer offer = null;
			if(agreement.getOffer() != null)
				offer = agreement.getOffer();
			else if(agreement.getOfferResponse() != null)
				offer = agreement.getOfferResponse().getOffer();
			else
				throw new ExException("Cannot update signers");

			if(!Strings.isNullOrEmpty(offer.getTemplate())) {
				offer.setTemplate(attachSignersToTemplate(offer.getTemplate(), signers));

				createSignatureContainer(negotiation, agreement);
			}
		}

		negManager.storeNegotiation(negotiation);

		List<Negotiator> notificationReceivers = new ArrayList<Negotiator>();
		notificationReceivers.add(negotiation.getOwner());
		if(!ContextHelper.getMembership().getProfile().getUuid().equals(negotiation.getOwner().getProfile().getUuid()))
			notificationReceivers.add(negotiation.getInvolvedNegotiatorsMembershipForProfile(ContextHelper.getMembership().getProfile().getUuid()));

		ActivityLogger.log(ContextHelper.getMembership(), Verb.CHANGE_SIGNER, negotiation, notificationReceivers);

		//TODO: update signature container for agreed negotiation
		//NegotiationSignatureContainer negSigContainer = signaturePersistentManager.readbyUUID(negotiationID);

		return signers;
	}

	@Override
	public List<Negotiator> bindNegotiators(List<Signer> signers){
		List<Negotiator> agreementSigners = new ArrayList<>();

		for(Signer signer : signers){
			// use email and profileId for relating membership, guest etc.
			// update template or update signer??
			Negotiator negotiator = null;

			if(!Strings.isNullOrEmpty(signer.getMembershipIdentifier())) {
				negotiator = getNegotiator(signer.getMembershipIdentifier());
			}

			if(negotiator == null && !Strings.isNullOrEmpty(signer.getEmail())) {
				negotiator = membershipManager.getMembershipByEmail(signer.getEmail(), ContextHelper.getMembership().getProfile().getUuid(), true);
			}

			if(negotiator == null) {
				negotiator = membershipManager.createGuestMembership(signer.getFirstName(), signer.getLastName(), signer.getEmail(), signer.getPhone(), signer.getTitle(), ContextHelper.getMembership().getProfile());
			}

			if(negotiator != null) {
				agreementSigners.add(negotiator);
				signer.setMembershipIdentifier(negotiator.getIdentifier()); // update negotiator identifier reference
				signer.setFirstName(negotiator.getUser().getFirstName());
				signer.setLastName(negotiator.getUser().getLastName());
				signer.setEmail(negotiator.getUser().getEmail());
				signer.setPhone(negotiator.getUser().getPhone());
			}
		}

		return agreementSigners;
	}

	@Override
	public List<Signer> extractSignersFromTemplate(String template){
		List<Signer> signers = new ArrayList<>();
		if(!Strings.isNullOrEmpty(template)) {
			Document doc = Jsoup.parse(template);

			Elements signatureContainers = doc.select(".signature-container");

			for(Element signatureContainer : signatureContainers){
				String signerEmail = signatureContainer.select(".signer").first().attr("data-email");
				String signerId = signatureContainer.select(".signer").first().attr("data-signerid");

				if(!Strings.isNullOrEmpty(signerEmail) || !Strings.isNullOrEmpty(signerId)) {
					Signer signer = new Signer();

					if(!Strings.isNullOrEmpty(signerEmail)){
						signer.setEmail(signerEmail);
						signer.setFirstName(signatureContainer.select(".signer").first().attr("data-firstname"));
						signer.setLastName(signatureContainer.select(".signer").first().attr("data-lastname"));
						signer.setTitle(signatureContainer.select(".signer").first().attr("data-title"));
					}
					else {
						signer.setMembershipIdentifier(signatureContainer.select(".signer").first().attr("data-signerid"));
					}
					signer.setTemplateRefId(signatureContainer.attr("id"));
					signers.add(signer);
				}

			}
		}

		return signers;
	}

	@Override
	public String attachSignersToTemplate(String template, List<Signer> signers) {
		if(!Strings.isNullOrEmpty(template) && !ExCollections.isEmpty(signers)) {
			Document doc = Jsoup.parse(template);

			for(Signer signer : signers){
				if(!Strings.isNullOrEmpty(signer.getTemplateRefId())) {
					Element signatureContainer = doc.select("#" + signer.getTemplateRefId()).first();
					if(signatureContainer != null) {
						Element signerElement =	signatureContainer.select(".signer").first();

						signerElement.removeAttr("data-email")
						.removeAttr("data-firstname")
						.removeAttr("data-lastname")
						.removeAttr("data-title")
						.attr("data-signerid", signer.getMembershipIdentifier());

						signerElement.text(signer.getFirstName() + " " + signer.getLastName()+" (" + (signer.getEmail() !=null ? signer.getEmail() : signer.getPhone())+")");
					}
				}
			}

			template = doc.outerHtml();
		}

		return template;
	}

	@Override
	public String updateSigners(String iNegotiationID, String template) {

		//TODO: handle signature field removal from template
		if(!Strings.isNullOrEmpty(template)) {
			List<Signer> signers = extractSignersFromTemplate(template);

			Negotiation negotiation = negManager.getNegotiation(iNegotiationID);
			if(negotiation.isClosed())
				throw new ExException("Negotiation is closed!");

			negManager.updateAgreementSigners(negotiation, bindNegotiators(signers));
			negManager.storeNegotiation(negotiation);

			template = attachSignersToTemplate(template, signers);
		}

		return template;
	}

	@Override
	public void createSignatureContainerAsOwner(String negotiationUUID) {
		ExLogger.get().info("Creating contract as Owner. Negotiation: {}", negotiationUUID);
		Negotiation negotiation = negManager.getNegotiation(negotiationUUID);

		if (negotiation == null)
			throw new ExNotFoundException(negotiationUUID);

		Negotiator requestor = ContextHelper.getMembership();
		Negotiator involvedNegotiator = negManager.getInvolvedNegotiator(negotiation, requestor);

		if (!(negotiation.isOwner(requestor) || (Security.isProfileAdministrator(requestor.getProfile().getUuid())
				&& negotiation.loadUserStatus(involvedNegotiator) == UserAdmissionStatus.OWNED)))
			throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);

		List<Agreement> agreements = negotiation.getMessageBox().getAgreements(negotiation.getOwner());
		for (Agreement agreement : agreements) {
			createSignatureContainerAsOwner(negotiation, agreement);
		}

		negManager.storeNegotiation(negotiation);
	}

	@Override
	public boolean canCreateSignatureContainer(Negotiation negotiation, Agreement agreement) {
		if (agreement == null)
			return false;

		if (!Strings.isNullOrEmpty(agreement.getAgreementDocumentID()))
			return false;

		return true;
	}

	@Override
	public void createSignatureContainerAsOwner(Negotiation negotiation, Agreement agreement) {
		if (canCreateSignatureContainer(negotiation, agreement)) {
			createSignatureContainer(negotiation, agreement);
		}
	}

	private Negotiator getNegotiator(String identifier) {
		Negotiator negotiator = null;
		negotiator = membershipManager.findByUUID(identifier, true);

		return negotiator;
	}

	private boolean canUpdateSigner(Negotiation negotiation) {
		if(!negotiation.isAgreementSigningEnabled())
			return false;

		NegotiationSignatureContainer signatureContainer = null;

		try {
			signatureContainer = getRegisterSignedContainer(negotiation.getUuid());
		}
		catch(ExAuthorizationException | ExNotFoundException ex) {

		}

		if(!Strings.isNullOrEmpty(negotiation.getInformationModelDocument().getTemplate()) && signatureContainer == null)
			return false;

		if(signatureContainer != null && signatureContainer.getNextToSign() == null)
			return false;

		Negotiator involvedNegotiator = negManager.getInvolvedNegotiator(negotiation, ContextHelper.getMembership());
		if(involvedNegotiator == null)
			return false;

		return true;
	}

	private NegotiationSignatureContainer buildNegotiationSignatureContainer(Negotiation neg, Agreement agreement) {
		List<Negotiator> agreementSigners = new ArrayList<Negotiator>();

		if(ExCollections.isNotEmpty(neg.getAgreementSigners())){
			for(Negotiator agreementSigner : neg.getAgreementSigners()){
				for(Negotiator agreedParticipant : agreement.getAgreedParticipants()){
					if(agreedParticipant.getProfile().getUuid().equals(agreementSigner.getProfile().getUuid()))
						agreementSigners.add(agreementSigner);
				}
			}
			//agreementSigners.addAll(neg.getAgreementSigners());
		}
		else
			agreementSigners.addAll(agreement.getAgreedParticipants());

		//Quickfix: to overcome the issue of not updating signer role inside negotiators list of Negotiation due to orientdb bug
		try {
			if(agreement.retrieveAcceptedOffer() != null && !Strings.isNullOrEmpty(agreement.retrieveAcceptedOffer().getTemplate())) {
				List<Signer> signers = extractSignersFromTemplate(agreement.retrieveAcceptedOffer().getTemplate());
				for(Signer signer : signers) {
					boolean signerExit = false;
					for(Negotiator agreementSigner : agreementSigners) {
						if(agreementSigner.getIdentifier().equals(signer.getMembershipIdentifier())) {
							signerExit = true;
							break;
						}
					}

					if(!signerExit) {
						agreementSigners.add(getNegotiator(signer.getMembershipIdentifier()));
						LOGGER.warn("Signer does not exist in negotation.negotiators: negotiationUUID-{}, membershipUUID-{}, templateRefId-{}", neg.getUuid(), signer.getMembershipIdentifier(), signer.getTemplateRefId());
					}
				}
			}
		}
		catch(Exception ex) {
			LOGGER.error("Could not merge agreement.gereedParticipants with signers in offer.template", ex);
		}

		NegotiationSignatureContainer negSigContainer=
				new NegotiationSignatureContainer(neg.getUuid(), agreement, updateSigningSequence(neg, agreementSigners));

		return negSigContainer;
	}
}
