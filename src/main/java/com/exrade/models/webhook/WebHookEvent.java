package com.exrade.models.webhook;

import com.exrade.models.notification.NotificationType;
import com.exrade.runtime.timer.TimeProvider;

import java.util.Date;

public class WebHookEvent {
	private NotificationType eventType;
	
	private Date creationDate = TimeProvider.now();
	
	private String objectId;

	private Object data;
	
	private int responseCode;
	
	private String responseBody;

	public NotificationType getEventType() {
		return eventType;
	}

	public void setEventType(NotificationType eventType) {
		this.eventType = eventType;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
}
