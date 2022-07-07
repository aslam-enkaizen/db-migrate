package com.exrade.runtime.notification;

import com.exrade.models.activity.Activity;
import com.exrade.models.notification.ChannelType;
import com.exrade.models.notification.Frequency;
import com.exrade.models.userprofile.Membership;
import com.exrade.runtime.mail.EmailSender;
import com.exrade.runtime.notification.event.NotificationEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailChannel extends AbstractChannel {

	private IUserSettingsManager userSettingManager = new UserSettingsManager();
	private EmailSender emailSender = new EmailSender();
	
	@Override
	public ChannelType getChannelType() {
		return ChannelType.EMAIL;
	}

	@Override
	public <T> void dispatch(NotificationEvent<T> notificationEvent, Activity activity, List<Membership> toList) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("activity", activity);
		for(Membership to : toList){
			if(userSettingManager.isNotificationActive(to.getUuid(), getChannelType(), notificationEvent.getNotificationType(), Frequency.IMMEDIATELY)){
				dataMap.put("to", to);
				emailSender.send(notificationEvent.getNotificationType().toString(), to.getLanguage(), dataMap, new String[] { to.getUser().getEmail() });
			}
		}
	}

}
