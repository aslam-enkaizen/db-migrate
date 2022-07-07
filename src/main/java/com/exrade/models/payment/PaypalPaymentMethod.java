package com.exrade.models.payment;

import com.exrade.platform.persistence.BaseEntityUUID;

public class PaypalPaymentMethod extends BaseEntityUUID implements IPaymentMethod {

	private String email;
	private String firstName;
	private String lastName;
	private boolean isVerified;
	private String note;
	
	@Override
	public PaymentType getPaymentType() {
		return PaymentType.PAYPAL;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	@Override
	public String getNote() {
		return note;
	}

	@Override
	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String getIdentifier() {
		return getEmail();
	} 

}
