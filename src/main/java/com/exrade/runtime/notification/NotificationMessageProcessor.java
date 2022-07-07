package com.exrade.runtime.notification;

import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.processmodel.protocol.events.TimeEvent;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.mail.EmailSender;
import com.exrade.runtime.negotiation.persistence.NegotiationPersistenceManager;
import com.exrade.runtime.notification.NotificationConstants.NotificationType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotificationMessageProcessor {

	private String notificationType;
	private String notificationName;
	private static final String EMAIL_TEMPLATE = "email-negotiation-deadlines";
	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/negotiation/";

	public NotificationMessageProcessor(String notificationType,
			String notificationName) {
		this.notificationName = notificationName;
		this.notificationType = notificationType;
	}

	public void process(String negotiationUUID) {
		NegotiationPersistenceManager negotiationPM = new NegotiationPersistenceManager();
		Negotiation negotiation = negotiationPM.readByUUID(negotiationUUID);

		if (!negotiation.isClosed()) {

			EmailSender emailSender = new EmailSender();
			switch (notificationType) {
				case NotificationType.END_DATE:
					if(negotiation.getEndDate() != null){
						Map<String, Object> data = getTemplateData(negotiation, negotiation.getEndDate());
						
						emailSender.send(negotiation.getOwner(), negotiation, EMAIL_TEMPLATE, data);
						
						for (Negotiator actor : negotiation.getParticipants())
							emailSender.send(actor, negotiation, EMAIL_TEMPLATE, data);
						break;
					}
	
				case NotificationType.PROCESS_TIME_EVENT:
					for (TimeEvent event : negotiation.getOwnerTimeEvents()) {
						if (event.getName().equals(notificationName))
							emailSender.send(negotiation.getOwner(), negotiation, EMAIL_TEMPLATE, getTemplateData(negotiation, event.getTime()));
					}
					for (TimeEvent event : negotiation.getParticipantTimeEvents()) {
						if (event.getName().equals(notificationName))
							for (Negotiator actor : negotiation.getParticipants())
								emailSender.send(actor, negotiation, EMAIL_TEMPLATE, getTemplateData(negotiation, event.getTime()));
					}
					break;

			}
		}
	}

	public Map<String, Object> getTemplateData(Negotiation negotiation, Date date){
		Map<String, Object> data = new HashMap<String, Object>();
		
		data.put("deadline_name", notificationName);
		data.put("deadline_date", date.toString());
		data.put("url", BASE_URL + negotiation.getUuid());
		
		return data;
	}
}
