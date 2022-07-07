package com.exrade.models.kyc.qii;

import com.exrade.Messages;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum QiiIncomeType {
	Unknown(0),
	Employed(1),
	Entrepreneur(2),
	Pension(3),
	UnemploymentBenefit(4),
	EmployedFixedTerm(26),
	EmployedPermanent(27),
	Other(28),
	Student(32),
	SelfEmployed(33),
	None(34);

	int value;

	private QiiIncomeType(int value) {
		this.value = value;
	}

	public static QiiIncomeType fromValue(int iValue) {
		for (QiiIncomeType type : QiiIncomeType.values()) {
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
