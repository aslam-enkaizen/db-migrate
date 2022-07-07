package com.exrade.models.payment;

public enum PaymentStatus {
	NO_PAYMENT, // Payment is not enabled in negotiation
	PENDING,    // Payment is pending
	COMPLETED,  // Payment is completed
	FAILED      // Failed to complete payment
}
