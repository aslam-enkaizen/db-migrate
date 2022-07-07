package com.exrade.models.kyc.qii;

import com.exrade.Messages;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum QiiMaritialStatus {
	Unknown(0),
	Undefined(1),
	Single(2),
	Married(3),
	MarriedWithPrenup(4),
	MarriedForeign(5),
	RegisteredPartner(6),
	RegisteredPartnerWithConditions(7),
	Cohabiting(8),
	CohabitingWithContract(9),
	Divorced(10);

	int value;

	private QiiMaritialStatus(int value) {
		this.value = value;
	}

	public static QiiMaritialStatus fromValue(int iValue) {
		for (QiiMaritialStatus type : QiiMaritialStatus.values()) {
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
