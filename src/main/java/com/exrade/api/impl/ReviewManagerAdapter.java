package com.exrade.api.impl;

import com.exrade.api.ReviewAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.review.Review;
import com.exrade.models.review.ReviewRequest;
import com.exrade.models.review.Reviewer;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.ReviewFilters;
import com.exrade.runtime.rest.RestParameters.ReviewRequestFilters;
import com.exrade.runtime.review.IReviewManager;
import com.exrade.runtime.review.ReviewManager;
import com.exrade.util.ContextHelper;

import java.util.List;
import java.util.Map;

public class ReviewManagerAdapter implements ReviewAPI {

	private IReviewManager manager = new ReviewManager();

	@Override
	public ReviewRequest createReviewRequest(ExRequestEnvelope request, ReviewRequest iReviewRequest) {
		ContextHelper.initContext(request);
		iReviewRequest.setCreator(ContextHelper.getMembership());
		return manager.createReviewRequest(iReviewRequest);
	}

	@Override
	public List<ReviewRequest> listReviewRequest(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters qFilters = QueryFilters.create(iFilters);
		qFilters.put(ReviewRequestFilters.CREATOR_PROFILE_UUID, ContextHelper.getMembership().getProfile().getUuid());
		qFilters.putIfNotNull(ReviewRequestFilters.NEGOTIATION_UUID, iFilters.get(ReviewRequestFilters.NEGOTIATION_UUID));
		qFilters.putIfNotNull(ReviewRequestFilters.CREATOR_UUID, iFilters.get(ReviewRequestFilters.CREATOR_UUID));
		qFilters.putIfNotNull(ReviewRequestFilters.STATUS, iFilters.get(ReviewRequestFilters.STATUS));
		qFilters.putIfNotNull(ReviewRequestFilters.OFFER_UUID, iFilters.get(ReviewRequestFilters.OFFER_UUID));

		if(!iFilters.containsKey(ReviewRequestFilters.NEGOTIATION_UUID))
			qFilters.put(ReviewRequestFilters.REVIEWER_UUID, ContextHelper.getMembershipUUID());

		if (qFilters.isNullOrEmpty(QueryParameters.SORT)){
			qFilters.put(QueryParameters.SORT, OrientSqlBuilder.DESC_SORT+RestParameters.CREATION_DATE);
		}

		return manager.listReviewRequests(qFilters);
	}

	@Override
	public ReviewRequest getReviewRequestByUUID(ExRequestEnvelope request, String iReviewRequestUUID) {
		ContextHelper.initContext(request);
		return manager.getReviewRequestByUUID(iReviewRequestUUID);
	}

	@Override
	public Review createReview(ExRequestEnvelope request, String iReviewRequestUUID, Review iReview) {
		ContextHelper.initContext(request);
		iReview.setReviewer(ContextHelper.getMembership());
		return manager.createReview(iReviewRequestUUID, iReview);
	}

	@Override
	public List<Review> listReviews(ExRequestEnvelope request, String iReviewRequestUUID,
			Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters qFilters = getReviewQueryFilters(iFilters);
		qFilters.put(ReviewFilters.REVIEW_REQUEST_UUID, iReviewRequestUUID);
		return manager.listReviews(iReviewRequestUUID, qFilters);
	}

	@Override
	public List<Review> listReviews(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters qFilters = getReviewQueryFilters(iFilters);
		return manager.listReviews(qFilters);
	}

	@Override
	public Review updateReview(ExRequestEnvelope request, Review iReview) {
		ContextHelper.initContext(request);
		return manager.updateReview(iReview);
	}

	@Override
	public Review getReviewByUUID(ExRequestEnvelope request, String iReviewUUID) {
		ContextHelper.initContext(request);
		return manager.getReviewByUUID(iReviewUUID);
	}

	@Override
	public void updateReviewers(ExRequestEnvelope request, String negotiationID, List<Reviewer> reviewers) {
		ContextHelper.initContext(request);
		manager.updateReviewers(negotiationID, reviewers);
	}

	private QueryFilters getReviewQueryFilters(Map<String, String> iFilters){
		QueryFilters qFilters = QueryFilters.create(iFilters);
		qFilters.put(ReviewFilters.REQUESTOR_PROFILE_UUID, ContextHelper.getMembership().getProfile().getUuid());
		qFilters.putIfNotEmpty(ReviewFilters.OFFER_UUID, iFilters.get(ReviewFilters.OFFER_UUID));
		qFilters.putIfNotEmpty(ReviewFilters.REVIEW_REQUEST_UUID, iFilters.get(ReviewFilters.REVIEW_REQUEST_UUID));
		qFilters.putIfNotNull(ReviewFilters.STATUS, iFilters.get(ReviewFilters.STATUS));
		qFilters.putIfNotEmpty(ReviewFilters.REVIEWER_UUID, iFilters.get(ReviewFilters.REVIEWER_UUID));
		if (iFilters.containsKey(ReviewFilters.OWN_REVIEW_ONLY)){
			qFilters.put(ReviewFilters.REVIEWER_UUID, ContextHelper.getMembershipUUID());
		}

		return qFilters;
	}
}
