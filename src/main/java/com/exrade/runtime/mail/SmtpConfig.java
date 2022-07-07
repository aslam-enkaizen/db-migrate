package com.exrade.runtime.mail;

import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.conf.ExConfiguration.configKeys;

import java.util.Properties;

public class SmtpConfig {

	private String smtpUsername = "";
	private String smtpPassword = "";
	private String fromEmail = "";
	private String fromName = "";
	private Properties smtpProperties;

	public SmtpConfig(){
		//TODO handle loading configuration according to services
		loadConfig();
	}
	
	public String getSmtpUsername() {
		return smtpUsername;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public String getFromName() {
		return fromName;
	}

	public Properties getSmtpProperties() {
		return smtpProperties;
	}
	
	private void loadConfig() {
		smtpUsername = ExConfiguration.getStringProperty(configKeys.MAIL_USERNAME);
		smtpPassword = ExConfiguration.getStringProperty(configKeys.MAIL_PASSWORD);
		fromEmail = ExConfiguration.getStringProperty(configKeys.MAIL_FROM);
		//fromName = smtpProperties.getProperty(configKeys.MAIL_FROM_NAME);
		smtpProperties = new Properties();
		smtpProperties.put(configKeys.MAIL_SMTP_AUTH, ExConfiguration.getStringProperty(configKeys.MAIL_SMTP_AUTH));
		smtpProperties.put(configKeys.MAIL_SMTP_HOST, ExConfiguration.getStringProperty(configKeys.MAIL_SMTP_HOST));
		smtpProperties.put(configKeys.MAIL_SMTP_PORT, ExConfiguration.getStringProperty(configKeys.MAIL_SMTP_PORT));
	}
}
