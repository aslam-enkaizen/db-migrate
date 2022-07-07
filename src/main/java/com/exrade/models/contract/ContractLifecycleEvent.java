package com.exrade.models.contract;

import com.exrade.models.userprofile.Negotiator;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import java.util.Date;

public class ContractLifecycleEvent extends BaseEntityUUIDTimeStampable {
	private ContractLifecycleEventType eventType;

	private Date startDate;

	private Date endDate;

	private Negotiator creator;

	private Integer renewalNumber = 0;

	private Integer maxRenewal;

	private String trakUUID;

	public ContractLifecycleEvent() {}

	public ContractLifecycleEventType getEventType() {
		return eventType;
	}

	public void setEventType(ContractLifecycleEventType eventType) {
		this.eventType = eventType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		//this.startDate = DateUtil.toBeginningOfTheDay(startDate);
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		//this.endDate = DateUtil.toEndOfTheDay(endDate);
		this.endDate = endDate;
	}

	public Negotiator getCreator() {
		return creator;
	}

	public void setCreator(Negotiator creator) {
		this.creator = creator;
	}


	public Integer getRenewalNumber() {
		return renewalNumber;
	}

	public void setRenewalNumber(Integer renewalNumber) {
		this.renewalNumber = renewalNumber;
	}

	public Integer getMaxRenewal() {
		return maxRenewal;
	}

	public void setMaxRenewal(Integer maxRenewal) {
		this.maxRenewal = maxRenewal;
	}

	public String getTrakUUID() {
		return trakUUID;
	}

	public void setTrakUUID(String trakUUID) {
		this.trakUUID = trakUUID;
	}
}
