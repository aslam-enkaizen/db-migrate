package com.exrade.models.common;

import com.exrade.platform.persistence.BaseEntity;

public class Image extends BaseEntity {

	private String fileUUID;
	
	private Integer order = 0;

	public Image() {
	}

	public Image(String iFileUUID,Integer iOrder){
		fileUUID = iFileUUID;
		order = iOrder; 
	}
	
	public String getFileUUID() {
		return fileUUID;
	}
	public void setFileUUID(String fileUUID) {
		this.fileUUID = fileUUID;
	}
	
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	
}
