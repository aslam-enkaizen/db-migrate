package com.exrade.models.atm;

import com.exrade.platform.persistence.BaseEntity;

public class ATMContent extends BaseEntity {
	private String file;
	
	private String hash;
	
	private String transaction;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}
}
