package com.exrade.models.kyc.qii;

import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import java.util.ArrayList;
import java.util.List;

public class QiiIncome extends BaseEntityUUIDTimeStampable {

	// $filter=memberId eq 39481
	private List<QiiIncomeEmployer> incomeEmployerList = new ArrayList<QiiIncomeEmployer>();// income type, start date, end date, part time percentage, emplyer, pay period

	// $filter=memberId eq 39481
	private List<QiiIncomeEmployerMonth> incomeEmployerMonthList = new ArrayList<QiiIncomeEmployerMonth>();// start date, end date, income, hours

	// $filter=memberId eq 39481
	private List<QiiIncomeYear> incomeYearList = new ArrayList<QiiIncomeYear>();// year, value/income

	public List<QiiIncomeEmployer> getIncomeEmployerList() {
		return incomeEmployerList;
	}

	public void setIncomeEmployerList(List<QiiIncomeEmployer> incomeEmployerList) {
		this.incomeEmployerList = incomeEmployerList;
	}

	public List<QiiIncomeEmployerMonth> getIncomeEmployerMonthList() {
		return incomeEmployerMonthList;
	}

	public void setIncomeEmployerMonthList(List<QiiIncomeEmployerMonth> incomeEmployerMonthList) {
		this.incomeEmployerMonthList = incomeEmployerMonthList;
	}

	public List<QiiIncomeYear> getIncomeYearList() {
		return incomeYearList;
	}

	public void setIncomeYearList(List<QiiIncomeYear> incomeYearList) {
		this.incomeYearList = incomeYearList;
	}
}
