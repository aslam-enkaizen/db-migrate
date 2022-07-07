package com.exrade.models.blockchain;

import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.BaseEntityUUID;
import com.exrade.platform.persistence.TimeStampable;
import com.exrade.runtime.timer.TimeProvider;

import java.util.Date;

public class BlockchainWallet extends BaseEntityUUID implements TimeStampable {

	private String address;
	private String privateKey;
	private String password;
	private Date creationDate = TimeProvider.now();
	private Date updateDate;
	private Membership owner;
	private String blockchainType = "ETH";
	
	public BlockchainWallet(){}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Membership getOwner() {
		return owner;
	}

	public void setOwner(Membership owner) {
		this.owner = owner;
	}

	public String getBlockchainType() {
		return blockchainType;
	}

	public void setBlockchainType(String blockchainType) {
		this.blockchainType = blockchainType;
	}
	
	public Boolean isOwner(Membership iMembership) {
		if(getOwner().equals(iMembership))
			return true;
	
		return false;
	}
}
