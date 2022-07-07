package com.exrade.runtime.review;

import com.exrade.models.review.Review;
import com.exrade.models.review.ReviewRequest;
import com.exrade.models.review.Reviewer;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IReviewManager {
	
	ReviewRequest createReviewRequest(ReviewRequest iRequest);
	
	ReviewRequest getReviewRequestByUUID(String iRequestUUID);
	
	List<ReviewRequest> listReviewRequests(QueryFilters iFilters);
	
	Review createReview(String iReviewRequestUUID, Review iReview);
	
	List<Review> listReviews(String iReviewRequestUUID, QueryFilters iFilters);
	
	List<Review> listReviews(QueryFilters iFilters);

	Review updateReview(Review iReview);

	Review getReviewByUUID(String iReviewUUID);

	void updateReviewers(String negotiationID, List<Reviewer> reviewers);
}
