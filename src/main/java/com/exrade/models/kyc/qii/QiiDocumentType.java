package com.exrade.models.kyc.qii;

import com.exrade.Messages;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum QiiDocumentType {
	Unknown(0),
	DriversLicense(2),
	Passport(3),
	IdentityCard(4);

	int value;

	private QiiDocumentType(int value) {
		this.value = value;
	}

	public static QiiDocumentType fromValue(int iValue) {
		for (QiiDocumentType type : QiiDocumentType.values()) {
			if(type.value == iValue)
				return type;
		}

		return null;
	}

	public int getValue() {
		return value;
	}

	public String getName() {
		return name();
	}

	public String getDisplayName() {
      String messageKey = getClass().getSimpleName() + '.' + name();
      return Messages.get(messageKey);
   }
}
