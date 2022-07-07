package com.exrade.models.kyc.qii;

import com.exrade.Messages;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum QiiMemberType {
	Owner(1),
	Cohabitant(2),
	Guarantor(3);

	int value;

	private QiiMemberType(int value) {
		this.value = value;
	}

	public static QiiMemberType fromValue(int iValue) {
		for (QiiMemberType type : QiiMemberType.values()) {
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
