package com.exrade.models.notification;

import com.exrade.platform.persistence.BaseEntityUUID;

import java.util.Arrays;
import java.util.List;

public class NotificationSetting extends BaseEntityUUID {

	private ChannelType channelType;
	
	private NotificationType notificationType;
	
	private List<Frequency> allowedFrequencies;
	
	private boolean active = true;

	public static NotificationSetting create(NotificationType iNotificationType, ChannelType iChannelType) {
		return create(iNotificationType, iChannelType, Arrays.asList(Frequency.values()));
	}
	
	public static NotificationSetting create(NotificationType iNotificationType, ChannelType iChannelType, List<Frequency> iAllowedFrequencies) {
		NotificationSetting setting = new NotificationSetting();
		setting.setAllowedFrequencies(iAllowedFrequencies);
		setting.setChannelType(iChannelType);
		setting.setNotificationType(iNotificationType);
		setting.setActive(true);
		return setting;
	}
	
	public ChannelType getChannelType() {
		return channelType;
	}

	public void setChannelType(ChannelType channelType) {
		this.channelType = channelType;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Frequency> getAllowedFrequencies() {
		return allowedFrequencies;
	}

	public void setAllowedFrequencies(List<Frequency> allowedFrequencies) {
		this.allowedFrequencies = allowedFrequencies;
	}
	
}
