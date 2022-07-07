package com.exrade.runtime.review;

import com.exrade.core.ExLogger;
import com.exrade.models.review.Review;
import com.exrade.models.review.ReviewRequest;
import com.exrade.models.review.ReviewStatus;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExException;
import com.exrade.runtime.common.ISyncOperationController;
import com.exrade.runtime.review.persistence.ReviewPersistenceManager;
import com.exrade.runtime.timer.TimeProvider;
import com.exrade.util.ContextHelper;

public class ReviewSyncOperationController implements ISyncOperationController {
	
	public Review updateReview(Review iReview) {
		ReviewPersistenceManager reviewPersistentManager = new ReviewPersistenceManager();
		
		synchronized (this) {
			ExLogger.get().info("Updating review: " + iReview.getUuid());
			Review existingReview = reviewPersistentManager.readObjectByUUID(Review.class, iReview.getUuid());
			
			if(existingReview.getStatus() == ReviewStatus.COMPLETED 
					|| !existingReview.getReviewer().equals(iReview.getReviewer())
					|| !existingReview.getReviewer().equals(ContextHelper.getMembership()))
				throw new ExException(ErrorKeys.NOT_AUTHORIZED);
			
			existingReview.setComment(iReview.getComment());
			existingReview.setScore(iReview.getScore());
			existingReview.setFiles(iReview.getFiles());
			if(iReview.getStatus() != null)
				existingReview.setStatus(iReview.getStatus());
			existingReview.setUpdateDate(TimeProvider.now());
			existingReview = reviewPersistentManager.update(existingReview);
			ExLogger.get().info("Updated review: " + iReview.getUuid());
			
			ReviewRequest reviewRequest = existingReview.getReviewRequest();
			
			if(reviewRequest.getStatus() != ReviewStatus.COMPLETED) {
				
				boolean isPending = false;
				for(Review review : reviewRequest.getReviews()){
					if(review.getStatus() == ReviewStatus.PENDING) {
						isPending = true;
						break;
					}
				}
				
				if(!isPending) {
					ExLogger.get().info("Updating reviewrequest for review: " + iReview.getUuid());
					reviewRequest.setStatus(ReviewStatus.COMPLETED);
					reviewPersistentManager.update(reviewRequest);
					ExLogger.get().info("Updated reviewrequest for review: " + iReview.getUuid());
				}
				
			}
			
			return existingReview;
		}
	}
}
