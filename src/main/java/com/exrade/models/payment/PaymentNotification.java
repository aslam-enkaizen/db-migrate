package com.exrade.models.payment;

import com.exrade.models.userprofile.Negotiator;

import java.util.List;

/**
 * @author Rhidoy
 * @created 08/06/2022
 * <p>
 * This immutable class use to hold data for sending payment notification from PaymentNotificationEvent class.
 */
public class PaymentNotification {
    private final Negotiator actor;
    private final List<Negotiator> receiver;
    private final String negotiationUuid;
    private final String negotiationTitle;

    public PaymentNotification(Negotiator actor, List<Negotiator> receiver, String negotiationUuid, String negotiationTitle) {
        this.actor = actor;
        this.receiver = receiver;
        this.negotiationUuid = negotiationUuid;
        this.negotiationTitle = negotiationTitle;
    }

    public Negotiator getActor() {
        return actor;
    }

    public List<Negotiator> getReceiver() {
        return receiver;
    }

    public String getNegotiationUuid() {
        return negotiationUuid;
    }

    public String getNegotiationTitle() {
        return negotiationTitle;
    }
}
