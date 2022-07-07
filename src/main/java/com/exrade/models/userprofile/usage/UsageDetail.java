package com.exrade.models.userprofile.usage;


public class UsageDetail {
		
	private String name;

	private String code;

	private String limit;
	
	private String currentlyUsed;
	
	public UsageDetail(){}
	
	public UsageDetail(String name, String code, String limit, String currentlyUsed) {
		super();
		this.name = name;
		this.code = code;
		this.limit = limit;
		this.currentlyUsed = currentlyUsed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getCurrentlyUsed() {
		return currentlyUsed;
	}

	public void setCurrentlyUsed(String currentlyUsed) {
		this.currentlyUsed = currentlyUsed;
	}
	
}
