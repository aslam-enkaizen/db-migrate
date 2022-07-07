package com.exrade.models.kyc.qii;

import com.exrade.platform.persistence.BaseEntityUUID;
import com.exrade.runtime.kyc.qii.DateStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class QiiIncomeEmployer extends BaseEntityUUID {

	private Integer incomeEmployerId;

	private QiiIncomeType type;

	private String startDate;

	private String endDate;

	private Integer partTimePercentage;

	private String employer;

	private QiiPayPeriod payPeriod;

	public Integer getIncomeEmployerId() {
		return incomeEmployerId;
	}

	public void setIncomeEmployerId(Integer incomeEmployerId) {
		this.incomeEmployerId = incomeEmployerId;
	}

	public QiiIncomeType getType() {
		return type;
	}

	public void setType(QiiIncomeType type) {
		this.type = type;
	}

	@JsonSerialize(using = DateStringSerializer.class, as=String.class)
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	@JsonSerialize(using = DateStringSerializer.class, as=String.class)
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getPartTimePercentage() {
		return partTimePercentage;
	}

	public void setPartTimePercentage(Integer partTimePercentage) {
		this.partTimePercentage = partTimePercentage;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public QiiPayPeriod getPayPeriod() {
		return payPeriod;
	}

	public void setPayPeriod(QiiPayPeriod payPeriod) {
		this.payPeriod = payPeriod;
	}

}
