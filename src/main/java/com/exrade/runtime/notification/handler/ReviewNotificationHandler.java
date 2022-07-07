package com.exrade.runtime.notification.handler;

import com.exrade.models.notification.NotificationType;
import com.exrade.models.review.Review;
import com.exrade.models.review.ReviewStatus;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.event.ReviewNotificationEvent;
import com.exrade.runtime.userprofile.TraktiJwtManager;
import com.exrade.util.ContextHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewNotificationHandler extends BaseNotificationHandler implements INotificationHandler {

	private static final String BASE_URL = ExConfiguration.getStringProperty("site.url") + "/negotiation/";

	@Override
	public <T> void handle(NotificationEvent<T> event) {
		ReviewNotificationEvent notificationEvent = (ReviewNotificationEvent) event;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<Negotiator> receivers = new ArrayList<>();

		if(NotificationType.REVIEW_REQUESTED == notificationEvent.getNotificationType()){
			dataMap.put("actor_name", ContextHelper.getMembership().getUser().getFullName());
			dataMap.put("target_name", notificationEvent.getReviewRequest().getNegotiation().getTitle());

			for(Negotiator receiver : notificationEvent.getReviewRequest().getReviewers()) {
				if(receiver != null) {
					if(receiver.isGuest()) {
						dataMap.put("url", BASE_URL + notificationEvent.getReviewRequest().getNegotiationUUID() + "/review-request/" + notificationEvent.getReviewRequest().getUuid() + "?token=" + TraktiJwtManager.getInstance().generateToken(receiver.getIdentifier()));
						sendNotification(receiver, receiver.getUser().getEmail(), notificationEvent.getNotificationType(), dataMap, notificationEvent.getReviewRequest().getNegotiation().getLanguage());
					}
					else
						receivers.add(receiver);
				}
			}
			dataMap.put("url", BASE_URL + notificationEvent.getReviewRequest().getNegotiationUUID() + "/review-request/" + notificationEvent.getReviewRequest().getUuid());
		}
		else if(NotificationType.REVIEW_PENDING_REMINDER == notificationEvent.getNotificationType()){
			dataMap.put("target_name", notificationEvent.getReviewRequest().getNegotiation().getTitle());

			if(ReviewStatus.PENDING == notificationEvent.getReviewRequest().getStatus() && !notificationEvent.getReviewRequest().getNegotiation().isClosed()) {
				for(Review review : notificationEvent.getReviewRequest().getReviews()) {
					if(review.getStatus() == ReviewStatus.PENDING) {
						if(review.getReviewer().isGuest()) {
							dataMap.put("url", BASE_URL + notificationEvent.getReviewRequest().getNegotiationUUID()
														+ "/review-request/" + notificationEvent.getReviewRequest().getUuid()
														+ "?token=" + TraktiJwtManager.getInstance().generateToken(review.getReviewer().getIdentifier()));
							sendNotification(review.getReviewer(), review.getReviewer().getUser().getEmail(), notificationEvent.getNotificationType(), dataMap, notificationEvent.getReviewRequest().getNegotiation().getLanguage());
						}
						else
							receivers.add(review.getReviewer());
					}
				}
			}

			dataMap.put("url", BASE_URL + notificationEvent.getReviewRequest().getNegotiationUUID() + "/review-request/" + notificationEvent.getReviewRequest().getUuid());
		}

		sendWebHookNotification(notificationEvent, receivers);
		sendNotification(ContextHelper.getMembership(), receivers, notificationEvent.getNotificationType(), dataMap, notificationEvent.getReviewRequest().getNegotiation().getLanguage());
	}

}
