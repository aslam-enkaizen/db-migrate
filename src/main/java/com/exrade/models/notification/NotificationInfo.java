package com.exrade.models.notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationInfo {

	private String fromUser;
	private String toUser;
	private List<String> sendToUserIds  = new ArrayList<String>();
	private ChannelType channelType;
	
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public List<String> getSendToUserIds() {
		return sendToUserIds;
	}
	public void setSendToUserIds(List<String> sendToUserIds) {
		this.sendToUserIds = sendToUserIds;
	}
	public ChannelType getChannelType() {
		return channelType;
	}
	public void setChannelType(ChannelType channelType) {
		this.channelType = channelType;
	}
}
