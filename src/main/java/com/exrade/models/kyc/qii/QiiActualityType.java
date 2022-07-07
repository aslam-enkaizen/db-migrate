package com.exrade.models.kyc.qii;

import com.exrade.Messages;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum QiiActualityType {
	Unknown(0),
	Previous(1),
	Present(2),
	Future(3);

	int value;

	private QiiActualityType(int value) {
		this.value = value;
	}

	public static QiiActualityType fromValue(int iValue) {
		for (QiiActualityType type : QiiActualityType.values()) {
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
