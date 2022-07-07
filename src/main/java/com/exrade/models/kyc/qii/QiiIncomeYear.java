package com.exrade.models.kyc.qii;

import com.exrade.platform.persistence.BaseEntityUUID;

public class QiiIncomeYear extends BaseEntityUUID {

	private Integer incomeYearId;

	private Integer year;

	private Double value;

	public Integer getIncomeYearId() {
		return incomeYearId;
	}

	public void setIncomeYearId(Integer incomeYearId) {
		this.incomeYearId = incomeYearId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}


}
