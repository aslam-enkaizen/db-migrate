package com.exrade.runtime.payment;

import com.exrade.core.ExLogger;
import com.exrade.models.activity.ObjectType;
import com.exrade.models.activity.Verb;
import com.exrade.models.contract.ContractingPartyType;
import com.exrade.models.informationmodel.PaymentPattern;
import com.exrade.models.integration.IntegrationServiceType;
import com.exrade.models.integration.IntegrationSetting;
import com.exrade.models.messaging.Agreement;
import com.exrade.models.messaging.Offer;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.payment.*;
import com.exrade.models.processmodel.ProcessAttribute;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.modeltemplate.processmodel.AbstractProcessModelFactory.CoreProcessAttribute;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.exception.ExValidationException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.integration.IntegrationSettingManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.negotiation.persistence.NegotiationQuery.NegotiationQFilters;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.PaymentNotificationEvent;
import com.exrade.util.ExCollections;
import com.exrade.util.JSONUtil;
import com.exrade.util.RESTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.*;

public class PaymentManager {

    private static final Logger logger = ExLogger.get();
    private static final NegotiationManager negotiationManager = new NegotiationManager();
    private static final PersistentManager persistenceManager = new PersistentManager();
    private static final IntegrationSettingManager integrationSettingManager = new IntegrationSettingManager();
    private static final NotificationManager notificationManager = new NotificationManager();
    private static final String NEGOTIATION_UUID = "negotiationUuid";
    private static final String SUBSCRIPTION_PAYMENT_COMPLETE = "PAYMENT.SALE.COMPLETED";
    private static final String SUBSCRIPTION_CREATED = "BILLING.SUBSCRIPTION.CREATED";
    //when reactive the subscription
    private static final String SUBSCRIPTION_ACTIVATED = "BILLING.SUBSCRIPTION.ACTIVATED";
    private static final String SUBSCRIPTION_CANCELLED = "BILLING.SUBSCRIPTION.CANCELLED";
    private static final String SUBSCRIPTION_SUSPENDED = "BILLING.SUBSCRIPTION.SUSPENDED";
    private static final String SUBSCRIPTION_FAILED = "BILLING.SUBSCRIPTION.PAYMENT.FAILED";

    public static void initiatePayment(Negotiation negotiation, List<Agreement> agreements) {
        if (negotiation.getPayment() != null && ExCollections.isNotEmpty(agreements)
                && negotiation.getPayment().getPaymentStatus() == PaymentStatus.PENDING) {
            Agreement agreement = agreements.get(0);
            Offer offer = agreement.getOffer() != null ? agreement.getOffer() : agreement.getOfferResponse().getOffer();
            String winningBidderUserName = offer.getSenderUUID();

            Payment payment = negotiation.getPayment();
            String paymentReceiverEmail = null;

            for (IPaymentMethod paymentMethod : payment.getAllowedPaymentMethods()) {
                if (paymentMethod.getPaymentType() == PaymentType.PAYPAL) {
                    paymentReceiverEmail = paymentMethod.getIdentifier();
                }
            }

            Double amount = InformationModelUtil.getFinalAmount(offer, negotiation.getFinalAmountAttribute());
            if (amount != null) {
                payment.setAmount(amount);
                payment.setCurrencyCode(negotiation.getCurrencyCode());
                payment.setPaymentKey(paymentReceiverEmail);
            	
            	/*String currency = negotiation.getCurrencyCode();
            	
            	String paymentKey = PayPalUtil.createPayment(winningBidderUserName, paymentReceiverEmail, amount, currency, negotiation);
            	if(!Strings.isNullOrEmpty(paymentKey)){
            		payment.setPaymentKey(paymentKey);
            	}
            	else{
            		logger.error("Cannot generate payment key for negotiation - " + negotiation.getUuid());
            	}*/
            } else {
                logger.error("Cannot find finalAmountAttribute for negotiation - " + negotiation.getUuid());
            }
        }
    }

    public static void refreshPaymentStatus(Negotiation negotiation) {
        try {
            if (negotiation.getPayment().getPaymentStatus() == PaymentStatus.COMPLETED)
                return;

            if (!Strings.isNullOrEmpty(negotiation.getPayment().getPaymentKey())) {
                String paymentStatus = PayPalUtil.getPaymentStatus(negotiation.getPayment().getPaymentKey());

                if (PayPalUtil.COMPLETED.equalsIgnoreCase(paymentStatus)) {
                    negotiation.getPayment().setPaymentStatus(PaymentStatus.COMPLETED);
                    createPaymentLogAndNotification(negotiation);
                    persistenceManager.update(negotiation.getPayment());
                } else if (PayPalUtil.EXPIRED.equalsIgnoreCase(paymentStatus)) {
                    String refreshedPaymentKey = PayPalUtil.refreshIfPaymentKeyExpired(negotiation);
                    if (!Strings.isNullOrEmpty(refreshedPaymentKey)) {
                        negotiation.getPayment().setPaymentKey(refreshedPaymentKey);
                        persistenceManager.update(negotiation.getPayment());
                    }
                } else if (!PayPalUtil.CREATED.equalsIgnoreCase(paymentStatus)) {
                    logger.error(String.format("Unknown/Unhandled PayPal payment status - %s for Negotiation - %s", paymentStatus, negotiation.getUuid()));
                }
            }
        } catch (Exception ex) {
            logger.error(String.format("Error refreshing PayPal payment status for Negotiation - %s", negotiation.getUuid()), ex);
        }
    }

    public static void updateWebhookPaypalPaymentStatus(String negotiationUuid, String paymentStatus) {
        Negotiation negotiation = null;
        try {
            negotiation = negotiationManager.getNegotiation(negotiationUuid);
            if (negotiation != null && negotiation.getPayment() != null) {
                switch (paymentStatus) {
                    case "PAYMENT.CAPTURE.COMPLETED":
                    case SUBSCRIPTION_PAYMENT_COMPLETE:
                        negotiation.getPayment().setPaymentStatus(PaymentStatus.COMPLETED);
                        createPaymentLogAndNotification(negotiation);
                        break;
                    case "PAYMENT.CAPTURE.PENDING":
                        negotiation.getPayment().setPaymentStatus(PaymentStatus.PENDING);
                        break;
                    case "PAYMENT.CAPTURE.DENIED":
                        negotiation.getPayment().setPaymentStatus(PaymentStatus.FAILED);
                        break;
                    case SUBSCRIPTION_CREATED:
                    case SUBSCRIPTION_ACTIVATED:
                        createPaymentLogAndNotification(negotiation, NotificationType.PAYMENT_SUBSCRIBED);
                        break;
                    case SUBSCRIPTION_CANCELLED:
                    case SUBSCRIPTION_SUSPENDED:
                        createPaymentLogAndNotification(negotiation, NotificationType.PAYMENT_UN_SUBSCRIBED);
                        break;
                    default:
                        break;
                }
                persistenceManager.update(negotiation);
            } else {
                logger.error(String.format("Null error to update PayPal payment status for Negotiation - %s", negotiation));
            }
        } catch (Exception ex) {
            logger.error(String.format("Error updating PayPal payment status for Negotiation - %s", negotiation), ex);
        }
    }

    public static void updatePaymentStatus(String objectId, String objectType, String status, String paymentProviderResponse, String planId) {
        switch (objectType) {
            case "NEGOTIATION":
                updatePaymentForNegotiation(objectId, status, paymentProviderResponse, planId);
                break;

            default:
                break;
        }
    }

    private static void updatePaymentForNegotiation(String objectId, String status, String paymentProviderResponse, String planId) {
        Negotiation negotiation;
        try {
            negotiation = negotiationManager.getNegotiation(objectId);
            if (negotiation != null && negotiation.getPayment() != null) {
                switch (status) {
                    case "COMPLETED":
                        negotiation.getPayment().setPaymentStatus(PaymentStatus.COMPLETED);
                        createPaymentLogAndNotification(negotiation);
                        break;
                    case "PENDING":
                        negotiation.getPayment().setPaymentStatus(PaymentStatus.PENDING);
                        break;
                    case "DENIED":
                        negotiation.getPayment().setPaymentStatus(PaymentStatus.FAILED);
                        break;
                    default:
                        break;
                }
                if (!Strings.isNullOrEmpty(paymentProviderResponse)) {
                    negotiation.getPayment().setPaymentProviderResponse(paymentProviderResponse);
                }
                if (!Strings.isNullOrEmpty(planId)) {
                    negotiation.getPayment().setPlanId(planId);
                }
                persistenceManager.update(negotiation);
            } else {
                logger.error(String.format("Null error to update PayPal payment status for Negotiation - %s", objectId));
            }
        } catch (Exception ex) {
            logger.error(String.format("Error updating PayPal payment status for Negotiation - %s", objectId), ex);
        }
    }

    public static void updatePaymentConfiguration(Negotiation negotiation, List<String> allowedPaymentMethodUUIDs) {
        Payment payment = negotiation.getPayment();
        if (payment != null) {
            List<IPaymentMethod> allowedPaymentMethods = new ArrayList<IPaymentMethod>();

            if (!ExCollections.isEmpty(allowedPaymentMethodUUIDs)) {
                for (IPaymentMethod paymentMethod : negotiation.getOwner().getProfile().getPaymentMethods()) {
                    for (String allowedPaymentMethodUUID : allowedPaymentMethodUUIDs) {
                        if (allowedPaymentMethodUUID.equals(paymentMethod.getUuid())) {
                            allowedPaymentMethods.add(paymentMethod);
                        }
                    }
                }
            }

            payment.setAllowedPaymentMethods(allowedPaymentMethods);
        } else {
            getPaymentConfiguration(negotiation, allowedPaymentMethodUUIDs);
        }
    }

    public static Payment getPaymentConfiguration(Negotiation negotiation, List<String> allowedPaymentMethodUUIDs) {
        Payment payment = null;

        if (!ExCollections.isEmpty(allowedPaymentMethodUUIDs)) {
            List<IPaymentMethod> allowedPaymentMethods = new ArrayList<IPaymentMethod>();

            for (IPaymentMethod paymentMethod : negotiation.getOwner().getProfile().getPaymentMethods()) {
                for (String allowedPaymentMethodUUID : allowedPaymentMethodUUIDs) {
                    if (allowedPaymentMethodUUID.equals(paymentMethod.getUuid())) {
                        allowedPaymentMethods.add(paymentMethod);
                    }
                }
            }

            payment = new Payment();
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setAllowedPaymentMethods(allowedPaymentMethods);

            ProcessAttribute paymentEnabledAttribute = negotiation.getProcessAttribute(CoreProcessAttribute.PAYER);
            if (paymentEnabledAttribute != null
                    && (PayerType.OWNER.name().equals(paymentEnabledAttribute.getValue())
                    || PayerType.PARTICIPANT.name().equals(paymentEnabledAttribute.getValue()))
            ) {

                payment.setPayerType(PayerType.valueOf(paymentEnabledAttribute.getValue()));
            } else {

                try {
                    PaymentPattern paymentPattern = InformationModelUtil.getPaymentPatternForTrak(negotiation.getInformationModelDocument().getTemplate());
                    if (paymentPattern != null) {
                        if (paymentPattern.getReceiver() == ContractingPartyType.OWNER) {
                            payment.setPayerType(PayerType.PARTICIPANT);
                        } else {
                            payment.setPayerType(PayerType.OWNER);
                        }
                    } else {
                        payment.setPayerType(PayerType.PARTICIPANT);
                    }
                } catch (Exception ex) {
                    payment.setPayerType(PayerType.PARTICIPANT); // TODO: set from PaymentPattern of contract model
                }
            }

            negotiation.setPayment(payment);
        }

        return payment;
    }

    public static boolean hasAssociatedNegotiation(String paymentMethodUUID) {
        QueryFilters filters = QueryFilters.create(NegotiationQFilters.SUPPORTS_PAYMENT_UUID, paymentMethodUUID);

        List<Negotiation> negotiations = new NegotiationManager().listNegotiations(filters);

        return !ExCollections.isEmpty(negotiations);
    }

    public static boolean isValidPaypalEvent(JsonNode jsonHeader, JsonNode jsonBody) {
        boolean isVerificationStatus = false;
        String negotiationUuid = null;
        PayerType payerType = null;
        IntegrationSetting integrationSetting = null;
        String clientId = null;
        String clientSecret = null;
        String webhookId = null;
        String tokenUrl = "https://api-m.sandbox.paypal.com/v1/oauth2/token";
        String verifyWebhookSignatureUrl = "https://api-m.sandbox.paypal.com/v1/notifications/verify-webhook-signature";
        String profileUuid = null;
        if (!jsonBody.path(NEGOTIATION_UUID).isMissingNode()) {
            negotiationUuid = jsonBody.path(NEGOTIATION_UUID).asText(null);
        }
        Negotiation negotiation = negotiationManager.getNegotiation(negotiationUuid);
        if (negotiation != null && negotiation.getPayment() != null) {
            payerType = negotiation.getPayment().getPayerType();
        }

        if (payerType != null) {
            switch (payerType) {
                case OWNER:
                    // : TODO
                    // Need to improve
                    profileUuid = negotiation.getParticipants().get(0).getProfile().getUuid();
                    integrationSetting = integrationSettingManager.getIntegrationSetting(profileUuid,
                            IntegrationServiceType.PAYPAL);
                    if (isSettingsEmpty(integrationSetting)) {
                        if (isPaypalSettingsValues(integrationSetting)) {
                            clientId = integrationSetting.getSettings().get("clientId").toString();
                            clientSecret = integrationSetting.getSettings().get("clientSecret").toString();
                            webhookId = integrationSetting.getSettings().get("webhookId").toString();
                            isVerificationStatus = paypalVerificationStatus(jsonHeader, clientId, clientSecret, webhookId,
                                    verifyWebhookSignatureUrl, tokenUrl);
                        }
                    }
                    break;

                case PARTICIPANT:
                    profileUuid = negotiation.getOwner().getProfile().getUuid();
                    integrationSetting = integrationSettingManager.getIntegrationSetting(profileUuid,
                            IntegrationServiceType.PAYPAL);
                    if (isSettingsEmpty(integrationSetting)) {
                        if (isPaypalSettingsValues(integrationSetting)) {
                            clientId = integrationSetting.getSettings().get("clientId").toString();
                            clientSecret = integrationSetting.getSettings().get("clientSecret").toString();
                            webhookId = integrationSetting.getSettings().get("webhookId").toString();
                            isVerificationStatus = paypalVerificationStatus(jsonHeader, clientId, clientSecret, webhookId,
                                    verifyWebhookSignatureUrl, tokenUrl);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
        return isVerificationStatus;
    }

    private static boolean isSettingsEmpty(IntegrationSetting integrationSetting) {
        return integrationSetting != null && !integrationSetting.getSettings().isEmpty();
    }

    private static boolean isPaypalSettingsValues(IntegrationSetting integrationSetting) {
        return integrationSetting.getSettings().get("clientId") != null
                && integrationSetting.getSettings().get("clientSecret") != null
                && integrationSetting.getSettings().get("webhookId") != null;
    }

    private static boolean paypalVerificationStatus(JsonNode jsonHeader, String clientId, String clientSecret, String webhookId,
                                                    String verifyWebhookSignatureUrl, String tokenUrl) {
        boolean isVerificationStatus = false;
        JsonNode jsonNode = RESTUtil.doRestPOST(verifyWebhookSignatureUrl,
                getVerifyWebhookSignatureHeaders(tokenUrl, clientId, clientSecret),
                JSONUtil.toJsonNode(getVerifyWebhookSignatureBody(jsonHeader, webhookId)));
        if (!jsonNode.path("verification_status").isMissingNode()) {
            if (jsonNode.path("verification_status").asText(null).equalsIgnoreCase("SUCCESS")) {
                isVerificationStatus = true;
            } else {
                isVerificationStatus = false;
            }
        }
        return isVerificationStatus;
    }

    private static Map<String, String> getVerifyWebhookSignatureHeaders(String url, String clientId,
                                                                        String clientSecret) {
        Map<String, String> headerParameters = new HashMap<>();
        headerParameters.put("Authorization", String.format("Bearer %s", getPaypalToken(url, clientId, clientSecret)));
        return headerParameters;
    }

    private static Map<String, Object> getVerifyWebhookSignatureBody(JsonNode jsonHeader, String webhookId) {
        Map<String, Object> bodyParameters = new HashMap<>();
        //todo
        bodyParameters.put("transmission_id", jsonHeader.get("HTTP_PAYPAL_TRANSMISSION_ID"));
        bodyParameters.put("transmission_time", jsonHeader.get("HTTP_PAYPAL_TRANSMISSION_TIME"));
        bodyParameters.put("transmission_sig", jsonHeader.get("HTTP_PAYPAL_TRANSMISSION_SIG"));
        bodyParameters.put("cert_url", jsonHeader.get("HTTP_PAYPAL_CERT_URL"));
        bodyParameters.put("auth_algo", jsonHeader.get("HTTP_PAYPAL_AUTH_ALGO"));
        bodyParameters.put("webhook_id", webhookId);
        return bodyParameters;
    }

    private static String getPaypalToken(String url, String clientId, String clientSecret) {
        String access_token = null;
        Map<String, String> headerParameters = new HashMap<>();
        headerParameters.put("Username", clientId);
        headerParameters.put("Password", clientSecret);
        Map<String, Object> bodyParameters = new HashMap<>();
        bodyParameters.put("grant_type", "client_credentials");
        JsonNode jsonNode = RESTUtil.doRestPOST(url, headerParameters, JSONUtil.toJsonNode(bodyParameters));
        if (!jsonNode.path("access_token").isMissingNode()) {
            access_token = jsonNode.path("access_token").asText(null);
        }
        return access_token;
    }

    public static void createPaymentLogAndNotification(Negotiation negotiation) {
        createPaymentLogAndNotification(negotiation, NotificationType.PAYMENT_COMPLETE, new HashMap<>());
    }

    public static void createPaymentLogAndNotification(Negotiation negotiation, NotificationType notificationType) {
        createPaymentLogAndNotification(negotiation, notificationType, new HashMap<>());
    }

    public static void createPaymentLogAndNotification(Negotiation negotiation, NotificationType notificationType, Map<String, String> extraContextParams) {
        Negotiator actor = null;
        List<Negotiator> receivers = new ArrayList<>();
        PayerType payerType = negotiation.getPayment().getPayerType();
        List<Agreement> agreements = negotiation.getMessageBox().getAgreements(negotiation.getOwner());
        for (Agreement agreement : agreements) {
            receivers = agreement.getAgreedParticipants();
            if (payerType.equals(PayerType.PARTICIPANT)) {
                //comparing owner id
                for (Negotiator receiver : receivers)
                    if (!receiver.getIdentifier().equalsIgnoreCase(negotiation.getOwnerUUID())) {
                        actor = receiver;
                        break;
                    }
            } else if (payerType.equals(PayerType.OWNER)) {
                //comparing owner id
                for (Negotiator receiver : receivers)
                    if (receiver.getIdentifier().equalsIgnoreCase(negotiation.getOwnerUUID())) {
                        actor = receiver;
                        break;
                    }
            }
        }

        if (notificationType.equals(NotificationType.PAYMENT_PENDING))
            ActivityLogger.log(ObjectType.SYSTEM, Verb.REMIND, actor, Collections.singletonList(actor), extraContextParams);
        else {
            extraContextParams.put(ObjectType.PAYMENT.toString(), notificationType.toString());
            ActivityLogger.log(actor, Verb.PAY, negotiation.getPayment(), receivers, negotiation, extraContextParams);
        }

        //sent notification
        notificationManager
                .process(new PaymentNotificationEvent(
                        notificationType,
                        new PaymentNotification(actor, receivers, negotiation.getUuid(), negotiation.getTitle())));
    }

    public static Payment getPaymentById(String uuid) {
        Payment payment = persistenceManager.readObjectByUUID(Payment.class, uuid);
        if (payment == null)
            throw new ExNotFoundException("Payment not found for Id " + uuid);
        return payment;
    }

    public static Payment updatePaymentPlanId(String uuid, String planId) {
        if (StringUtils.isBlank(planId))
            throw new ExValidationException(ErrorKeys.PLAN_ID_CAN_NOT_BE_NULL);
        Payment payment = getPaymentById(uuid);
        payment.setPlanId(planId);
        persistenceManager.update(payment);
        return payment;
    }

}
