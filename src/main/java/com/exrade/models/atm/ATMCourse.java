package com.exrade.models.atm;

import com.exrade.platform.persistence.BaseEntityUUID;
import com.exrade.platform.persistence.TimeStampable;
import com.exrade.runtime.timer.TimeProvider;

import java.util.Date;

public class ATMCourse extends BaseEntityUUID implements TimeStampable {

	private Date creationDate = TimeProvider.now();
	
	private Date updateDate;
	
	private String assetId;
	
	private String partNumber;
	
	private String title;
	
	private String description;
	
	private String tutorId;
	
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

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTutorId() {
		return tutorId;
	}

	public void setTutorId(String tutorId) {
		this.tutorId = tutorId;
	}

}
