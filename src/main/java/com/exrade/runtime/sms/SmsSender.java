package com.exrade.runtime.sms;

import com.exrade.core.ExLogger;
import com.exrade.models.integration.IntegrationServiceType;
import com.exrade.models.integration.IntegrationSetting;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.integration.IntegrationSettingManager;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;

public class SmsSender {
	private static Logger logger = ExLogger.get();

	private IntegrationSettingManager integrationSettingManager = new IntegrationSettingManager();

	public static String ACCOUNT_SID;
	public static String AUTH_TOKEN;
	public static PhoneNumber FROM_PHONE;

	private static final SmsSender INSTANCE = new SmsSender();

	private SmsSender() {
		ACCOUNT_SID = ExConfiguration.getStringProperty("sms.api.id");
		AUTH_TOKEN = ExConfiguration.getStringProperty("sms.api.key");
		FROM_PHONE = new PhoneNumber(ExConfiguration.getStringProperty("sms.from.phone"));

		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
	}

	public static SmsSender getInstance() {
		return INSTANCE;
	}

	public void send(String messageBody, String toPhone) {
		try {
			Message message = Message.creator(new PhoneNumber(toPhone), FROM_PHONE, messageBody).create();

			logger.info("SMS sent - SID: {}, Status: {}", message.getSid(), message.getStatus());
		} catch (Exception ex) {
			logger.error("Failed to send SMS", ex);
		}
	}

	public void send(String messageBody, String toPhone, String senderProfileUUID) {
		try {
			IntegrationSetting profileKycSettings = integrationSettingManager.getIntegrationSetting(senderProfileUUID,
					IntegrationServiceType.TWILIO_SMS);
			if (profileKycSettings.isActive()) {
				Twilio.setAccountSid(profileKycSettings.getSettings().get("apiKey").toString());
				Message message = Message.creator(new PhoneNumber(toPhone), new PhoneNumber(profileKycSettings.getSettings().get("phoneNumber").toString()), messageBody).create();

				logger.info("SMS sent - SID: {}, Status: {}", message.getSid(), message.getStatus());
			}
		} catch (Exception ex) {
			logger.error("Failed to send SMS for sub-account profile uuid: " + senderProfileUUID, ex);
			send(messageBody, toPhone);
		}

	}
}
