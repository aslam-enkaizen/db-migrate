package com.exrade.models.kyc.qii;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QiiRequest {
	private int id;
	private boolean activateLink;
	private String linkName;
	private String name;
	private String redirect;

	@JsonProperty(value="isUsed")
	private boolean used;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isActivateLink() {
		return activateLink;
	}
	public void setActivateLink(boolean activateLink) {
		this.activateLink = activateLink;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRedirect() {
		return redirect;
	}
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
}
