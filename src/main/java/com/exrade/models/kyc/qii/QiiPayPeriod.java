package com.exrade.models.kyc.qii;

import com.exrade.Messages;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum QiiPayPeriod {
	Unknown(0),
	Annually(1),
	Monthly(2),
	EveryFourWeeks(3);

	int value;

	private QiiPayPeriod(int value) {
		this.value = value;
	}

	public static QiiPayPeriod fromValue(int iValue) {
		for (QiiPayPeriod type : QiiPayPeriod.values()) {
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
