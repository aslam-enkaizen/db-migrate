package com.exrade.models.common;

import com.exrade.platform.persistence.BaseEntityUUID;

public class Counter extends BaseEntityUUID {

	private String name;
	
	private long value;
	
	public Counter() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
}
