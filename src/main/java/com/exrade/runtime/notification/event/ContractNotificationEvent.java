package com.exrade.runtime.notification.event;

import com.exrade.models.contract.Contract;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;

public class ContractNotificationEvent extends NotificationEvent<Contract> {
	
	private Membership member;
	
	public ContractNotificationEvent(NotificationType source, Contract payload) {
		super(source, payload);
	}
	
	public ContractNotificationEvent(NotificationType source, Contract payload, Membership member) {
		super(source, payload);
		this.member = member;
	}
	
	public Contract getContract() {
		return getPayload();
	}
	
	public Membership getMember() {
		return member;
	}
}
