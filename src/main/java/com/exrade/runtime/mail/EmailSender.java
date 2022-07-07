package com.exrade.runtime.mail;

import com.exrade.core.ExLogger;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.template.FreemarkerTemplateProcessor;
import com.exrade.runtime.template.ITemplateProcessor;
import com.exrade.runtime.template.TemplateUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/***
 * This class facilitates email sending process.
 * @author john
 *
 */
public class EmailSender {

	private static Logger logger = ExLogger.get();

//	protected Mailer mailer;

	/***
	 * Creates EmailSender instance using default SmtpConfig
	 */
	public EmailSender(){
//		mailer = Mailer.getDefaultMailer();
	}

	/***
	 * Sends email in both html and text format
	 * @param toEmail receiver email address
	 * @param toName receiver name
	 * @param subject email subject
	 * @param bodyHtml html content of the email
	 * @param bodyText text content of the email
	 */

	/**
	 * Sends email in both html and text format
	 * @param iSubject
	 * @param iBodyHtml
	 * @param iBodyText
	 * @param iRecipients receiver email address, comma separated string
	 */
	public void send(String iSubject, String iBodyHtml, String iBodyText,
			String iRecipients) {
		send(iSubject,iBodyHtml,iBodyText,iRecipients.split(","));
	}

	/***
	 * Sends email in both html and text format
	 * @param toEmail receiver email address
	 * @param toName receiver name
	 * @param subject email subject
	 * @param bodyHtml html content of the email
	 * @param bodyText text content of the email
	 */
	public void send(String iSubject, String iBodyHtml, String iBodyText,
			String[] iRecipients) {

		logger.info("Sending mail to {}", Arrays.toString(iRecipients));

//		Body body = new Body(iBodyText, iBodyHtml);
//		Mail mail = new Mail(iSubject, body, iRecipients);
		// sends html
		// mail.sendHtml("<html>html</html>" );
		// sends text/text
		// mail.send( "text" );
		// sends both text and html
		try{
//			mailer.sendMail(mail);
		}
		catch(Exception ex){
			logger.error("Failed to send email", ex);
		}
	}

	/***
	 * Sends templated email.
	 * This method loads and processes email template body from templates directory using FreemarkerTemplateProcessor.
	 * It loads email subject from the translation file using MultiLanguageUtil
	 * @param userProfile receiver actor
	 * @param negotiation Negotiation related to the email message
	 * @param templateSet name of the email template
	 * @param otherData Data to fill the template
	 */
	public void send(Negotiator userProfile, Negotiation negotiation, String templateSet, Map<String, Object> otherData) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("user", userProfile);
		dataMap.put("negotiation", negotiation);
		if(otherData != null && otherData.size() > 0)
			dataMap.putAll(otherData);

		send(templateSet, userProfile.getUser().getLanguage(), dataMap, new String[] { userProfile.getUser().getEmail() });
	}

	public void send(String templateSet, String templateLanguage, Map<String, Object> otherData, String[] iRecipients) {
		String locale = templateLanguage;
		if(Strings.isNullOrEmpty(locale))
			//todo commented
//			locale = play.mvc.Http.Context.current().lang().code();
		try {
			// Name of user for now in not used in the email, a template placeholder should be built in order to support it
			//userProfile.getName();
			String bodyHtml = getBody(otherData, templateSet, locale, "html-template.ftl");
			String bodyText = getBody(otherData, templateSet, locale, "text-template.ftl");
			String subject = getBody(otherData, templateSet, locale, "subject-template.ftl");
			this.send(subject, bodyHtml, bodyText, iRecipients);
		} catch (Exception e) {
			logger.warn("Failed sending templated email >> " + templateSet + " because of: "
					+ e.getMessage() + e.getStackTrace());
		}
	}


	private String getBody(Map<String, Object> data, String templateSet, String locale, String templateFile) throws Exception{
		ITemplateProcessor templateProcessor = new FreemarkerTemplateProcessor();

		return new String(templateProcessor.processTemplate(data, TemplateUtil.TAMPLATE_BASE_DIR, templateSet, templateFile, locale));

	}
}
