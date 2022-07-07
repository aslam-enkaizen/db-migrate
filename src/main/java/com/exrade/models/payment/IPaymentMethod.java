package com.exrade.models.payment;

import com.exrade.platform.persistence.IPersistenceUUID;

public interface IPaymentMethod extends IPersistenceUUID {

	PaymentType getPaymentType();
	
	String getIdentifier(); // Payment method identifier, e.g. email (for paypal), credit card number, iban etc.
	
	String getNote();
	
	void setNote(String note);
}
