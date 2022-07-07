package com.exrade.models.kyc.qii;

import com.exrade.platform.persistence.BaseEntityUUID;
import com.exrade.runtime.kyc.qii.DateStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class QiiIncomeEmployerMonth extends BaseEntityUUID {

	private Integer incomeEmployerMonthId;

	private String startDate;

	private String endDate;

	private Double income;

	private Double income2;

	private Integer hours;

	public Integer getIncomeEmployerMonthId() {
		return incomeEmployerMonthId;
	}

	public void setIncomeEmployerMonthId(Integer incomeEmployerMonthId) {
		this.incomeEmployerMonthId = incomeEmployerMonthId;
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

	public Double getIncome() {
		return income;
	}

	public void setIncome(Double income) {
		this.income = income;
	}

	public Double getIncome2() {
		return income2;
	}

	public void setIncome2(Double income2) {
		this.income2 = income2;
	}

	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}

}
