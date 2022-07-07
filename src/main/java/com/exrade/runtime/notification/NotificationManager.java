package com.exrade.runtime.notification;

import com.exrade.core.ExLogger;
import com.exrade.models.activity.Activity;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.schedular.NotificationJobSchedular;
import org.slf4j.Logger;

public class NotificationManager {

	private static final Logger LOGGER = ExLogger.get();
	
	public <T> void process(NotificationEvent<T> event){
		try{
			new NotificationJobSchedular().schedule(event);
		}
		catch(Exception ex){
			LOGGER.warn("Failed to process notification >> " + event.getNotificationType() + " because of: "
					+ ex.getMessage() + ex.getStackTrace());
		}
	}
	
	public void process(Activity activity){
		
	}
	
}
