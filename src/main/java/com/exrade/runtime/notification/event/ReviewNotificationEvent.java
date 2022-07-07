package com.exrade.runtime.notification.event;

import com.exrade.models.notification.NotificationType;
import com.exrade.models.review.ReviewRequest;

public class ReviewNotificationEvent extends NotificationEvent<ReviewRequest> {

	
	public ReviewNotificationEvent(NotificationType source, ReviewRequest payload) {
		super(source, payload);
	}
	
	public ReviewRequest getReviewRequest(){
		return getPayload();
	}
	
}
