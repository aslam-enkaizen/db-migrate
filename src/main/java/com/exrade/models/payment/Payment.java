package com.exrade.models.payment;

import com.exrade.platform.persistence.BaseEntityUUID;
import com.exrade.util.ExCollections;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Md Mahfuzul Islam
 *
 */
public class Payment extends BaseEntityUUID {

	private List<IPaymentMethod> allowedPaymentMethods;
	private PaymentStatus paymentStatus;
	private String paymentKey;
	private double amount;
	private String currencyCode;
	private PayerType payerType;
	private String paymentProviderResponse;
	private String planId;

	public List<IPaymentMethod> getAllowedPaymentMethods() {
		return allowedPaymentMethods;
	}

	public void setAllowedPaymentMethods(List<IPaymentMethod> allowedPaymentMethods) {
		this.allowedPaymentMethods = allowedPaymentMethods;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public PayerType getPayerType() {
		return payerType;
	}

	public void setPayerType(PayerType payerType) {
		this.payerType = payerType;
	}

	public String getPaymentProviderResponse() {
		return paymentProviderResponse;
	}

	public void setPaymentProviderResponse(String paymentProviderResponse) {
		this.paymentProviderResponse = paymentProviderResponse;
	}

	public String getPaymentKey() {
		return paymentKey;
	}

	public void setPaymentKey(String paymentKey) {
		this.paymentKey = paymentKey;
	}

	public String getPaymentUrl() {
		StringBuilder stringBuilder=new StringBuilder("https://www.paypal.com/sdk/js?client-id=");
		stringBuilder.append(getPaymentKey());
		if (planId!=null)
			stringBuilder.append("&vault=true&intent=subscription");
		else {
			stringBuilder.append("&currency=");
			stringBuilder.append(getCurrencyCode());
		}
		return stringBuilder.toString();

//		return String.format("https://www.paypal.com/sdk/js?client-id=%s&currency=%s", getPaymentKey(),
//				getCurrencyCode());
	}

	public List<String> getAllowedPaymentMethodUUIDs() {
		List<String> allowedPaymentMethodUUIDs = new ArrayList<String>();
		if (ExCollections.isNotEmpty(getAllowedPaymentMethods())) {
			for (IPaymentMethod paymentMethod : getAllowedPaymentMethods())
				allowedPaymentMethodUUIDs.add(paymentMethod.getUuid());
		}
		return allowedPaymentMethodUUIDs;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}
}
