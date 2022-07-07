package com.exrade.runtime.notification.event;

import com.exrade.models.notification.NotificationType;
import com.exrade.models.payment.PaymentNotification;

/**
 * @author Rhidoy
 * @created 07/06/2022
 * <p>
 * This class use to hold data for sending payment notification from PaymentNotificationHandler class.
 */
public class PaymentNotificationEvent extends NotificationEvent<PaymentNotification> {

	public PaymentNotificationEvent(NotificationType source, PaymentNotification payload) {
		super(source, payload);
	}
}
