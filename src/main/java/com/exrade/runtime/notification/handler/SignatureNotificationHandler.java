package com.exrade.runtime.notification.handler;

import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.signatures.RegisteredSignedDocument;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.event.SignatureNotificationEvent;
import com.exrade.runtime.userprofile.IMembershipManager;
import com.exrade.runtime.userprofile.MembershipManager;
import com.exrade.runtime.userprofile.TraktiJwtManager;

import java.util.*;

public class SignatureNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/negotiation/";
	private IMembershipManager membershipManager = new MembershipManager();
	private INegotiationManager negManager=new NegotiationManager();

	@Override
	public <T> void handle(NotificationEvent<T> event) {
		SignatureNotificationEvent notificationEvent = (SignatureNotificationEvent) event;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<Negotiator> receivers = new ArrayList<>();
		Negotiator actor = null;
		Negotiation negotiation = null;

		if(NotificationType.SIGNATURE_COMPLETED == notificationEvent.getNotificationType()){
			Set<String> agreedProfiles = new HashSet<String>();

			negotiation = notificationEvent.getNegotiation();
			dataMap.put("target_name", notificationEvent.getNegotiation().getTitle());

			for(RegisteredSignedDocument signed : notificationEvent.getNegotiationSignatureContainer().getAllUploaded()) {
				Negotiator receiver = membershipManager.findByUUID(signed.getSignerUUID(), false);
				if(receiver != null) {
					agreedProfiles.add(receiver.getProfile().getUuid());

					if(receiver.isGuest()) {
						dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid() + "/view-agreement/" + "?token=" + TraktiJwtManager.getInstance().generateToken(receiver.getIdentifier()));
						sendNotification(receiver, receiver.getUser().getEmail(), notificationEvent.getNotificationType(), dataMap, notificationEvent.getNegotiation().getLanguage());
					}
					else {
						receivers.add(receiver);
					}
				}
			}

			for(String profile : agreedProfiles) {
				Negotiator negotiator = negotiation.getInvolvedNegotiatorsMembershipForProfile(profile);

				if(!receivers.contains(negotiator))
					receivers.add(negotiator);
			}

			dataMap.put("url", BASE_URL + notificationEvent.getNegotiation().getUuid());
		}
		else if(NotificationType.SIGNATURE_PENDING_REMINDER == notificationEvent.getNotificationType()){
			if(notificationEvent.getNegotiationSignatureContainer().getNextToSign() == null)
				return;

			negotiation = notificationEvent.getNegotiation();
			if(negotiation == null)
				negotiation = negManager.getNegotiation(notificationEvent.getNegotiationSignatureContainer().getNegotiationID());

			dataMap.put("target_name", negotiation.getTitle());
			Negotiator nextSigner = membershipManager.findByUUID(notificationEvent.getNegotiationSignatureContainer().getNextToSign().getSignerUUID(), false);

			if(nextSigner != null) {
				dataMap.put("secret_key", notificationEvent.getNegotiationSignatureContainer().getNextToSign().getSecretSignkey());

				if(nextSigner.isGuest()) {
					dataMap.put("url", BASE_URL + notificationEvent.getNegotiationSignatureContainer().getNegotiationID()
												+ "/view-agreement/"
												+ "?token=" + TraktiJwtManager.getInstance().generateToken(nextSigner.getIdentifier()));
					sendNotification(nextSigner, nextSigner.getUser().getEmail(), notificationEvent.getNotificationType(), dataMap, negotiation.getLanguage());
				}
				else
					receivers.add(nextSigner);
			}

			dataMap.put("url", BASE_URL + notificationEvent.getNegotiationSignatureContainer().getNegotiationID());
		}

		sendWebHookNotification(notificationEvent, receivers);
		sendNotification(actor, receivers, notificationEvent.getNotificationType(), dataMap, negotiation.getLanguage());
	}

}
