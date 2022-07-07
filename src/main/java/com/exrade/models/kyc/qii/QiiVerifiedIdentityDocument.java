package com.exrade.models.kyc.qii;

import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.exrade.runtime.kyc.qii.DateStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class QiiVerifiedIdentityDocument extends BaseEntityUUIDTimeStampable {
	private String documentNumber;
	private String expirationDate;
	private QiiDocumentType type;

	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	@JsonSerialize(using = DateStringSerializer.class, as=String.class)
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public QiiDocumentType getType() {
		return type;
	}
	public void setType(QiiDocumentType type) {
		this.type = type;
	}
}
