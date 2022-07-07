package com.exrade.models.contract;

import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;
import com.exrade.util.DateUtil;

import java.util.Date;

public class ContractLifecycleSetting extends BaseEntityUUIDTimeStampable {
	private Date startDate;

	private Date endDate;

	private boolean renewable = false;

	private Date renewStartDate;

	private Date renewEndDate;

	private int renewOccurrenceLimit;

	private int renewDurationValue;

	private DurationUnit renewDurationUnit;

	private long renewDurationInMilliseconds;

	private boolean terminationNoticeRequired = false;

	private int terminationNoticePeriodValue;

	private DurationUnit terminationNoticePeriodUnit;

	private long terminationNoticePeriodInMilliseconds;

	public ContractLifecycleSetting() {
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

	public void setRenewable(boolean isRenewable) {
		this.renewable = isRenewable;
	}

	public boolean isRenewable() {
		return renewable;
	}

	public Date getRenewStartDate() {
		return renewStartDate;
	}

	public void setRenewStartDate(Date renewStartDate) {
		//this.renewStartDate = DateUtil.toBeginningOfTheDay(renewStartDate);
		this.renewStartDate = renewStartDate;
	}

	public Date getRenewEndDate() {
		return renewEndDate;
	}

	public void setRenewEndDate(Date renewEndDate) {
		//this.renewEndDate = DateUtil.toEndOfTheDay(renewEndDate);
		this.renewEndDate = renewEndDate;
	}

	public int getRenewDurationValue() {
		return renewDurationValue;
	}

	public void setRenewDurationValue(int renewDurationValue) {
		this.renewDurationValue = renewDurationValue;
		computeRenewDurationInMilliseconds();
	}

	public DurationUnit getRenewDurationUnit() {
		return renewDurationUnit;
	}

	public void setRenewDurationUnit(DurationUnit renewDurationUnit) {
		this.renewDurationUnit = renewDurationUnit;
		computeRenewDurationInMilliseconds();
	}

	public long getRenewDurationInMilliseconds() {
		return renewDurationInMilliseconds;
	}

	public void setRenewDurationInMilliseconds(long renewDurationInMilliseconds) {
		this.renewDurationInMilliseconds = renewDurationInMilliseconds;
	}

	public int getRenewOccurrenceLimit() {
		return renewOccurrenceLimit;
	}

	public void setRenewOccurrenceLimit(int renewOccurrenceLimit) {
		this.renewOccurrenceLimit = renewOccurrenceLimit;
	}

	public boolean isTerminationNoticeRequired() {
		return terminationNoticeRequired;
	}

	public void setTerminationNoticeRequired(boolean terminationNoticeRequired) {
		this.terminationNoticeRequired = terminationNoticeRequired;
	}

	public int getTerminationNoticePeriodValue() {
		return terminationNoticePeriodValue;
	}

	public void setTerminationNoticePeriodValue(int terminationNoticePeriodValue) {
		this.terminationNoticePeriodValue = terminationNoticePeriodValue;
		computeTerminationNoticeDurationInMilliseconds();
	}

	public DurationUnit getTerminationNoticePeriodUnit() {
		return terminationNoticePeriodUnit;
	}

	public void setTerminationNoticePeriodUnit(DurationUnit terminationNoticePeriodUnit) {
		this.terminationNoticePeriodUnit = terminationNoticePeriodUnit;
		computeTerminationNoticeDurationInMilliseconds();
	}

	public long getTerminationNoticePeriodInMilliseconds() {
		return terminationNoticePeriodInMilliseconds;
	}

	public void setTerminationNoticePeriodInMilliseconds(long terminationNoticePeriodInMilliseconds) {
		this.terminationNoticePeriodInMilliseconds = terminationNoticePeriodInMilliseconds;
	}

	private void computeRenewDurationInMilliseconds() {
		if(getRenewDurationValue() > 0 && getRenewDurationUnit() != null)
			setRenewDurationInMilliseconds(DateUtil.durationInMilliseconds(getRenewDurationValue(), getRenewDurationUnit()));
	}

	private void computeTerminationNoticeDurationInMilliseconds() {
		if(getTerminationNoticePeriodValue() > 0 && getTerminationNoticePeriodUnit() != null)
			setTerminationNoticePeriodInMilliseconds(DateUtil.durationInMilliseconds(getTerminationNoticePeriodValue(), getTerminationNoticePeriodUnit()));
	}
}
