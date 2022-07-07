package com.exrade.runtime.notification.event;

import com.exrade.models.notification.NotificationType;
import com.exrade.models.trak.Trak;
import com.exrade.models.trak.TrakApproval;
import com.exrade.models.trak.TrakResponse;

public class TrakNotificationEvent extends NotificationEvent<Trak> {

    private TrakResponse trakResponse;
    private TrakApproval trakApproval;

    public TrakNotificationEvent(NotificationType source, Trak payload) {
        super(source, payload);
    }

    public TrakNotificationEvent(NotificationType source, TrakResponse response) {
        super(source, response.getTrak());
        this.trakResponse = response;
    }

    public TrakNotificationEvent(NotificationType source, TrakApproval trakApproval) {
        super(source, trakApproval.getTrakResponse().getTrak());
        this.trakResponse = trakApproval.getTrakResponse();
        this.trakApproval = trakApproval;
    }

    public TrakResponse getTrakResponse() {
        return trakResponse;
    }

    public TrakApproval getTrakApproval() {
        return trakApproval;
    }
}
