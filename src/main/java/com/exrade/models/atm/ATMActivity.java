package com.exrade.models.atm;

import com.exrade.platform.persistence.BaseEntityUUID;
import com.exrade.platform.persistence.TimeStampable;
import com.exrade.runtime.timer.TimeProvider;

import javax.persistence.Embedded;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ATMActivity extends BaseEntityUUID implements TimeStampable {

	private Date creationDate = TimeProvider.now();
	
	private Date updateDate;
	
	private String assetId;
	
	private String partNumber;
	
	private String serialNumber;
	
	private String title;
	
	private String description;
	
	private Date scheduledAt;
	
	private Date startedAt;
	
	private Date completedAt;
	
	private String tutorId;
	
	private String maintainerId;
	
	private String status;
	
	private String type;
	
	private String note;
	
	@Embedded
	private ATMContent content;
	
	@Embedded
	private ATMCertificate certificate;

	private Map<String, Object> customFields = new HashMap<>();
	
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

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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

	public Date getScheduledAt() {
		return scheduledAt;
	}

	public void setScheduledAt(Date scheduledAt) {
		this.scheduledAt = scheduledAt;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

	public Date getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Date completedAt) {
		this.completedAt = completedAt;
	}

	public String getTutorId() {
		return tutorId;
	}

	public void setTutorId(String tutorId) {
		this.tutorId = tutorId;
	}

	public String getMaintainerId() {
		return maintainerId;
	}

	public void setMaintainerId(String maintainerId) {
		this.maintainerId = maintainerId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public ATMContent getContent() {
		return content;
	}

	public void setContent(ATMContent content) {
		this.content = content;
	}

	public ATMCertificate getCertificate() {
		return certificate;
	}

	public void setCertificate(ATMCertificate certificate) {
		this.certificate = certificate;
	}
	
	public Map<String, Object> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, Object> customFields) {
		this.customFields = customFields;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
