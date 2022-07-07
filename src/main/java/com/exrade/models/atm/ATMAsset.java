package com.exrade.models.atm;

import com.exrade.platform.persistence.BaseEntityUUID;
import com.exrade.platform.persistence.TimeStampable;
import com.exrade.runtime.timer.TimeProvider;

import java.util.Date;

public class ATMAsset extends BaseEntityUUID implements TimeStampable {

	private Date creationDate = TimeProvider.now();
	
	private Date updateDate;
	
	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public Date getUpdateDate() {
		return updateDate;
	}

	@Override
	public void setCreationDate(Date iDate) {
		creationDate = iDate;
	}

	@Override
	public void setUpdateDate(Date iDate) {
		updateDate = iDate;
	}

}
