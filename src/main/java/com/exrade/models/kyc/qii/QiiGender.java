package com.exrade.models.kyc.qii;

import com.exrade.Messages;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum QiiGender {
	Unknown(0),
	Male(1),
	Female(2),
	Other(3);

	private int value;

	private QiiGender(int iValue) {
		this.value = iValue;
	}

	public static QiiGender fromValue(int iValue) {
		for (QiiGender gender : QiiGender.values()) {
			if(gender.value == iValue)
				return gender;
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
