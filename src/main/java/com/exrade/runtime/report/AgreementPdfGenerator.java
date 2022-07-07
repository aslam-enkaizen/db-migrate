package com.exrade.runtime.report;

import com.exrade.core.ExLogger;
import com.exrade.models.messaging.Agreement;
import com.exrade.models.messaging.Offer;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.template.Template;
import com.exrade.models.userprofile.IProfile;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExException;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.filemanagement.FileMetadata;
import com.exrade.runtime.filemanagement.FileStorageProvider;
import com.exrade.runtime.filemanagement.IFileStorageController;
import com.exrade.runtime.template.*;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/***
 * Generates agreement pdf document and stores in the database
 * @author john
 *
 */
public class AgreementPdfGenerator {

	private static final Logger LOGGER = ExLogger.get();
	private IFileStorageController fsc = FileStorageProvider.getFileStorageController();
	private ITemplateManager templateManager = new TemplateManager();

//	private ITemplateProcessor templateProcessor = new FreemarkerTemplateProcessor();
//
//	public ITemplateProcessor getTemplateProcessor() {
//		return templateProcessor;
//	}
//
//	public void setTemplateProcessor(ITemplateProcessor templateProcessor) {
//		this.templateProcessor = templateProcessor;
//	}

	public ITemplateProcessor getTemplateProcessor(Negotiation negotiation) {
		if(!Strings.isNullOrEmpty(negotiation.getInformationModelDocument().getTemplate())){
			return new HtmlContractTemplateProcessor();
		}
		else
			return new FreemarkerTemplateProcessor();
	}

//	public String generate(Negotiation negotiation, Agreement agreement) {
//		String fileUuid = null;
//		try {
//			//TODO add header, footer, pdf meta info (author, ...)
//			//TODO enable digital signature
//
//			String templateBaseDir = getTemplateBaseDir(negotiation);
//
//			byte[] processedTemplateData = getTemplateProcessor().processTemplate(getTemplateData(negotiation, agreement, null),
//					templateBaseDir,FreemarkerTemplateProcessor.DEFAULT_TEMPLATE_FILE, getLocale(negotiation));
//			byte[] cleanedHtmlData = TemplateUtil.cleanHtml(processedTemplateData);
//
//			PdfRenderer pdfRenderer = new PdfRenderer();
//			byte[] pdfData = pdfRenderer.render(cleanedHtmlData, templateBaseDir);
//
//			IFileStorageController fsc = FileStorageProvider.getFileStorageController();
//			// fsc.storeFileWithName(fileName, outputStream.toByteArray());
//			Map<String, Object> metaData = new HashMap<String, Object>();
//			metaData.put(FileMetadata.NEGOTIATION_UUID, negotiation.getUuid());
//			metaData.put(FileMetadata.MESSAGE_UUID, agreement.getUuid());
//			metaData.put(FileMetadata.AUTHOR, agreement.getSenderUUID());
//
//			fileUuid = fsc.storeFile(pdfRenderer.signPdf(pdfData), "pdf", metaData);
//
//			LOGGER.info("Created agreement pdf {} for negotiation {}", fileUuid, negotiation.getUuid());
//		}
//		catch (Exception e) {
//			LOGGER.error("Error creating agreement pdf for negotiation " + negotiation.getUuid(), e);
//		}
//
//		return fileUuid;
//	}

	public String generate(Negotiation negotiation, Agreement agreement, List<Map<String, Object>> signers) {
		String fileUuid = null;
		try {
			//TODO add header, footer, pdf meta info (author, ...)
			//TODO enable digital signature

			String templateBaseDir = getTemplateBaseDir(negotiation);

			byte[] processedTemplateData = getTemplateProcessor(negotiation).processTemplate(getTemplateData(negotiation, agreement, signers), TemplateUtil.PDF_TAMPLATE_BASE_DIR_V2,
					templateBaseDir, null, getLocale(negotiation));
			byte[] cleanedHtmlData = TemplateUtil.cleanHtml(processedTemplateData);
			//LOGGER.info(new String(cleanedHtmlData));

			IPdfRenderer pdfRenderer = new OpenHtmlPdfRenderer();
			byte[] pdfData = pdfRenderer.render(cleanedHtmlData, templateBaseDir);
			byte[] digitalSignedPdfData = pdfRenderer.signPdf(pdfData, null, null, null);

			// fsc.storeFileWithName(fileName, outputStream.toByteArray());
			Map<String, Object> metaData = new HashMap<String, Object>();
			metaData.put(FileMetadata.NEGOTIATION_UUID, negotiation.getUuid());
			metaData.put(FileMetadata.MESSAGE_UUID, agreement.getUuid());
			metaData.put(FileMetadata.AUTHOR, agreement.getSenderUUID());
			metaData.put(FileMetadata.HASH, Hashing.sha256().hashBytes(digitalSignedPdfData).toString());

			fileUuid = fsc.storeFile(digitalSignedPdfData, "pdf", metaData);

			LOGGER.info("Created agreement pdf {} for negotiation {}", fileUuid, negotiation.getUuid());
		}
		catch (Exception e) {
			LOGGER.error("Error creating agreement pdf for negotiation " + negotiation.getUuid(), e);
		}

		return fileUuid;
	}

	/***
	 * Provides dictionary of string and object that will be used to replace place-holders in template
	 * @param negotiation
	 * @param agreement
	 * @return
	 */
	private Map<String, Object> getTemplateData(Negotiation negotiation, Agreement agreement, List<Map<String, Object>> signers){
		Map<String, Object> input = new HashMap<String, Object>();
        input.put("negotiation", negotiation);
        input.put("agreement", agreement);

        Offer offer = null;
        if(agreement.getOffer() != null)
        	offer = agreement.getOffer();
        else if(agreement.getOfferResponse() != null)
        	offer = agreement.getOfferResponse().getOffer();
        else
        	throw new ExException(ErrorKeys.AGREEMENT_CANNOT_GENERATE);

        input.put("offer", offer);
        input.put("owner", negotiation.getOwner());
        if(signers != null){
        	input.put("signers", signers);
        }

        List<Negotiator> agreedParticipants = new ArrayList<Negotiator>();
        if(agreement.getAgreedParticipants() != null && agreement.getAgreedParticipants().size() > 0)
        	agreedParticipants.addAll(agreement.getAgreedParticipants());
        else{
        	agreedParticipants.add(offer.getSender());
        	agreedParticipants.add(offer.getReceiver());
        }

        agreedParticipants.remove(negotiation.getOwner());
        input.put("agreedParticipants", agreedParticipants);

        try {
	        if(!Strings.isNullOrEmpty(negotiation.getHeadedPaperTemplateUUID())) {
	        	Template template = templateManager.getTemplateByUUID(negotiation.getHeadedPaperTemplateUUID());
				setHeaderFooter(input, template, fsc);
			}
	        else if(!Strings.isNullOrEmpty(((Membership)negotiation.getOwner()).getBusinessLogo())) {
	        		byte[] imageData = fsc.retrieveFileAsByte(((Membership)negotiation.getOwner()).getBusinessLogo());
	        		//ExLogger.get().debug(String.format("data:image/png;base64,%s", new String(Base64.encodeBase64(logo))));
	        		input.put("headerImage", String.format("data:image/png;base64,%s", new String(Base64.encodeBase64(imageData))));
	        		input.put("isHeaderLogo", true);
	        		//input.put("logo", "data:image/png;base64,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");
	        }
        }
        catch(Exception ex) {
        	ExLogger.get().warn("Error getting pdf template header/footer", ex);
        }

        return input;
	}

	public static void setHeaderFooter(Map<String, Object> input, Template template, IFileStorageController fsc) {
		if(!Strings.isNullOrEmpty(template.getHeader())) {
			byte[] imageData = fsc.retrieveFileAsByte(template.getHeader());
			input.put("headerImage", String.format("data:image/png;base64,%s", new String(Base64.encodeBase64(imageData))));
		}
		if(!Strings.isNullOrEmpty(template.getFooter())) {
			byte[] imageData = fsc.retrieveFileAsByte(template.getFooter());
			input.put("footerImage", String.format("data:image/png;base64,%s", new String(Base64.encodeBase64(imageData))));
		}
	}

	private String getLocale(Negotiation negotiation){
		String locale = negotiation.getInformationModelDocument().getLanguage();

		return locale;
	}

	private String getTemplateBaseDir(Negotiation negotiation){
		String template = "";

		if("Sales Quote".equals(negotiation.getInformationModelDocument().getTitle()) || "Preventivo".equals(negotiation.getInformationModelDocument().getTitle())) {
			template = "agreement-sales-quotation";
		}
		else {
			Negotiator owner = negotiation.getOwner();
			IProfile businessProfile = owner.getProfile();
			template = businessProfile.getAgreementTemplate();

			if(Strings.isNullOrEmpty(template)){
				//template = negotiation.getProcessModel().getAgreementTemplate();

				if(!Strings.isNullOrEmpty(negotiation.getInformationModelDocument().getTemplate()))
					template = "agreement-html-contract-negotiation";
				else{
					if(Strings.isNullOrEmpty(template)){
						template = ExConfiguration.getStringProperty("agreement.pdf.template");
						if(Strings.isNullOrEmpty(template)){
							template = "agreement-default";
						}
					}
				}
			}
		}

		return template;
	}


}
