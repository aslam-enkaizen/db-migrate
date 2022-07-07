package com.exrade.models.blockchain;

import com.exrade.models.activity.ObjectType;
import com.exrade.platform.persistence.BaseEntityUUID;
import com.exrade.platform.persistence.TimeStampable;
import com.exrade.runtime.timer.TimeProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BlockchainTransaction extends BaseEntityUUID implements TimeStampable {

	private ObjectType objectType;
	private String objectId;
	private String transactionId;
	private String contractAddress;
	private String membershipUUID;
	private String status;
	private Date creationDate = TimeProvider.now();
	private Date updateDate;
	private Map<String, Object> customFields = new HashMap<>();
	
	
	public BlockchainTransaction(){}
	
	public ObjectType getObjectType() {
		return objectType;
	}


	public void setObjectType(ObjectType objectType) {
		this.objectType = objectType;
	}


	public String getObjectId() {
		return objectId;
	}


	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}


	public String getTransactionId() {
		return transactionId;
	}


	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}


	public String getContractAddress() {
		return contractAddress;
	}


	public void setContractAddress(String contractAddress) {
		this.contractAddress = contractAddress;
	}


	public Map<String, Object> getCustomFields() {
		return customFields;
	}


	public void setCustomFields(Map<String, Object> customFields) {
		this.customFields = customFields;
	}


	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	public Date getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getMembershipUUID() {
		return membershipUUID;
	}

	public void setMembershipUUID(String membershipUUID) {
		this.membershipUUID = membershipUUID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
