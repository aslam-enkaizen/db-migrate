package com.exrade.runtime.notification.event;

import com.exrade.models.informationmodel.IInformationModel;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;

public class InformationModelNotificationEvent extends NotificationEvent<IInformationModel> {

	private Membership member;

	public InformationModelNotificationEvent(NotificationType source, IInformationModel payload) {
		super(source, payload);
	}

	public InformationModelNotificationEvent(NotificationType source, IInformationModel payload, Membership member) {
		super(source, payload);
		this.member = member;
	}

	public IInformationModel getInformationModel(){
		return getPayload();
	}

	public Membership getMember() {
		return member;
	}
}
