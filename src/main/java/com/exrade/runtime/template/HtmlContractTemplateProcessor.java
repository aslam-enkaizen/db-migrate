package com.exrade.runtime.template;

import com.exrade.models.i18n.ExLang;
import com.exrade.models.messaging.Agreement;
import com.exrade.models.messaging.Offer;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExParamException;
import com.google.common.base.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlContractTemplateProcessor implements ITemplateProcessor {

	private boolean processSignatureContainerEnabled = true;

	private ITemplateProcessor templateProcessor = new FreemarkerTemplateProcessor();

	private boolean removeEmptyVariablesEnabled = true;

	public static final String DEFAULT_TEMPLATE_FILE = "default.html";

	public HtmlContractTemplateProcessor(){
	}

	public boolean isProcessSignatureContainerEnabled() {
		return processSignatureContainerEnabled;
	}

	public void setProcessSignatureContainerEnabled(boolean processSignatureContainerEnabled) {
		this.processSignatureContainerEnabled = processSignatureContainerEnabled;
	}

	public boolean isRemoveEmptyVariablesEnabled() {
		return removeEmptyVariablesEnabled;
	}

	public void setRemoveEmptyVariablesEnabled(boolean removeEmptyVariablesEnabled) {
		this.removeEmptyVariablesEnabled = removeEmptyVariablesEnabled;
	}

	@Override
	public byte[] process(Map<String, Object> dataMap, String templateContent)
			throws Exception {
		throw new UnsupportedOperationException("Not implemented");
	}

	/***
	 * This method loads the specified template from a the base directory templateSet.
	 */
	@Override
	public byte[] processTemplate(Map<String, Object> dataMap, String templateBaseDir, String templateSet, String templateFile,
			String locale) throws Exception {
		byte[] outputData = null;

		Agreement agreement = (Agreement) dataMap.get("agreement");
		Offer offer = agreement.getOffer() == null ? agreement.getOfferResponse().getOffer() : agreement.getOffer();

		ClassLoader classLoader = getClass().getClassLoader();
		String templatePath = getTemplateDir(templateBaseDir, templateSet, locale) + "/" + DEFAULT_TEMPLATE_FILE;

		String template = offer.getTemplate();
		if(!Strings.isNullOrEmpty(template)){
			Document doc = Jsoup.parse(classLoader.getResourceAsStream(templatePath), "UTF-8", classLoader.getResource(templatePath).toString());
			Element agreementIdElement = doc.select("#agreementId").first();
			agreementIdElement.html(agreement.getUuid());

			if(dataMap.containsKey("headerImage") && dataMap.get("headerImage") != null) {
				Element logoElement = doc.select(".header-image").first();
				logoElement.attr("src", dataMap.get("headerImage").toString());
				if(dataMap.containsKey("isHeaderLogo") && Boolean.TRUE.equals(Boolean.parseBoolean(dataMap.get("isHeaderLogo").toString()))) {
					logoElement.addClass("header-logo");
				}
			}

			if(dataMap.containsKey("footerImage") && dataMap.get("footerImage") != null) {
				Element logoElement = doc.select(".footer-image").first();
				logoElement.attr("src", dataMap.get("footerImage").toString());
			}

			Element contractElement = doc.select("#contract").first();
			contractElement.html(offer.getTemplate().replace("font-family", "trakti-family"));

			if(isProcessSignatureContainerEnabled()) {
				Elements signatureContainers = doc.select(".signature-container");
				for(Element signatureContainer : signatureContainers){
					Element signatureRightBlock = signatureContainer.select(".signature-right").first();
					String signerId = signatureRightBlock.select(".signer").first().attr("data-signerid");

					if(Strings.isNullOrEmpty(signerId)){
						throw new ExParamException(ErrorKeys.BADREQUEST_MISSING_PARAMETER, signerId);
					}
					else {
						Map<String, Object> signerData = getSignerData(dataMap, signerId);

						if(signerData == null){
							throw new ExParamException(ErrorKeys.BADREQUEST_MISSING_PARAMETER, signerId);
						}
						else{
							Map<String, Object> signer = new HashMap<String, Object>();
							signer.put("signer", signerData);
							byte[] signatureBlock = templateProcessor.processTemplate(signer, templateBaseDir, templateSet, "signature.ftl", locale);
							Element newSignatureContainer = new Element("div");
							newSignatureContainer.html(new String(signatureBlock));
							signatureContainer.replaceWith(newSignatureContainer);
//						String signatureImage = (String)signerData.get("signature");
//						Date signatureDate = (Date)signerData.get("signatureDate");
//
//						if(!Strings.isNullOrEmpty(signatureImage) && signatureDate != null){
//
//							//<div class="signature" data-src="${signer.signature!''}" style="width: 135px">
//							//</div>
//							Element signatureElement = new Element("div");
//							signatureElement.addClass("signature");
//							signatureElement.attr("data-src", signatureImage);
//							signatureElement.attr("style", "width: 135px");
//							signatureRightBlock.attr("width", "60%");
//							signatureRightBlock.prependChild(signatureElement);
//
//							DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
//							Element signatureLeftBlock = signatureContainer.select(".signature-left").first();
//							signatureLeftBlock.attr("width", "40%");
//							signatureLeftBlock.append(df.format(signatureDate));
//						}
						}
					}
				}
			}

			if(isRemoveEmptyVariablesEnabled())
				InformationModelUtil.removeVariableNamesFromHtmlWhereNoValue(doc);

			Elements kycDataItemElements = doc.select(".kycdataitem");
			for(Element kycDataItemElement : kycDataItemElements) {
				kycDataItemElement.removeAttr("data-kycdata");
			}

			//ExLogger.get().debug(doc.outerHtml());
			outputData = doc.outerHtml().getBytes("UTF-8");
		}

		return outputData;
	}

	private String getTemplateDir(String templateBaseDir, String templateSet, String locale) {
		String templatePath = templateBaseDir + templateSet;
		if(templatePath.startsWith("/"))
			templatePath = templatePath.substring(1);

		if(!templatePath.endsWith(File.separator))
			templatePath = templatePath + File.separator;

		if (Strings.isNullOrEmpty(locale))
			return templatePath + ExLang.ENGLISH.getCode();
		else
			return templatePath + locale.toLowerCase();
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getSignerData(Map<String, Object> dataMap, String signerId){
		List<Map<String, Object>> signers = (List<Map<String, Object>>) dataMap.get("signers");
		for(Map<String, Object> signerData : signers){
			Membership membership = (Membership) signerData.get("membership");
			if(signerId.equals(membership.getUuid())){
				return signerData;
			}
		}

		return null;
	}

	@Override
	public byte[] processTemplateForPdf(Map<String, Object> dataMap, String templateBaseDir, String templateSet,
			String templateFile, String locale) throws Exception {

		byte[] outputData = null;
		ClassLoader classLoader = getClass().getClassLoader();
		String templatePath = getTemplateDir(templateBaseDir, templateSet, locale) + "/" + DEFAULT_TEMPLATE_FILE;

		Document doc = Jsoup.parse(classLoader.getResourceAsStream(templatePath), "UTF-8",
				classLoader.getResource(templatePath).toString());

		if (dataMap.containsKey("headerImage") && dataMap.get("headerImage") != null) {
			Element headerLogoElement = doc.select(".header-image").first();
			headerLogoElement.attr("src", dataMap.get("headerImage").toString());
			if (dataMap.containsKey("isHeaderLogo")
					&& Boolean.TRUE.equals(Boolean.parseBoolean(dataMap.get("isHeaderLogo").toString()))) {
				headerLogoElement.addClass("header-logo");
			}
		}

		if (dataMap.containsKey("footerImage") && dataMap.get("footerImage") != null) {
			Element footerLogoElement = doc.select(".footer-image").first();
			footerLogoElement.attr("src", dataMap.get("footerImage").toString());
		}

		if (dataMap.containsKey("content") && dataMap.get("content") != null) {
			Element contentElement = doc.select("#contract").first();
			contentElement.html(dataMap.get("content").toString().replace("font-family", "trakti-family"));
		}

		InformationModelUtil.removeVariableNamesFromHtmlWhereNoValue(doc);

		outputData = doc.outerHtml().getBytes("UTF-8");

		return outputData;

	}

}
