package com.exrade.models.kyc.qii;

import com.exrade.platform.persistence.BaseEntityUUID;

public class QiiEmployerHistoryYear extends BaseEntityUUID {

	private Integer employerHistoryYearId;

	private Integer year;

	private Integer hours;

	private String employer;

	public Integer getEmployerHistoryYearId() {
		return employerHistoryYearId;
	}

	public void setEmployerHistoryYearId(Integer employerHistoryYearId) {
		this.employerHistoryYearId = employerHistoryYearId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

}
