package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.review.Review;
import com.exrade.models.review.ReviewRequest;
import com.exrade.models.review.Reviewer;

import java.util.List;
import java.util.Map;

public interface ReviewAPI {

	ReviewRequest createReviewRequest(ExRequestEnvelope request, ReviewRequest iReviewRequest);
	
	List<ReviewRequest> listReviewRequest(ExRequestEnvelope request, Map<String, String> iFilters);
	
	ReviewRequest getReviewRequestByUUID(ExRequestEnvelope request, String iReviewRequest);
	
	Review createReview(ExRequestEnvelope request, String iReviewRequestUUID, Review iReview);
	
	Review updateReview(ExRequestEnvelope request, Review iReview);
	
	Review getReviewByUUID(ExRequestEnvelope request, String iReviewUUID);
	
	List<Review> listReviews(ExRequestEnvelope request, String iReviewRequestUUID, Map<String, String> iFilters);
	
	List<Review> listReviews(ExRequestEnvelope request, Map<String, String> iFilters);

	void updateReviewers(ExRequestEnvelope request, String negotiationID, List<Reviewer> reviewers);
}
