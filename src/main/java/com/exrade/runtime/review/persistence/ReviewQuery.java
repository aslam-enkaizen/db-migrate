package com.exrade.runtime.review.persistence;

import com.exrade.models.review.Review;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.ReviewFields;
import com.exrade.runtime.rest.RestParameters.ReviewFilters;

public class ReviewQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + Review.class.getSimpleName()+ " where 1 = 1 ";
		
		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		
		if (iFilters.isNotNull(ReviewFields.STATUS)){
			query += andEq(ReviewFields.STATUS, iFilters.get(ReviewFields.STATUS));
		}
		
		if (iFilters.isNotNull(ReviewFilters.REVIEW_REQUEST_UUID)){
			query += andEq("reviewRequest.uuid", iFilters.get(ReviewFilters.REVIEW_REQUEST_UUID));
		}
		
		if (iFilters.isNotNull(ReviewFilters.REQUESTOR_PROFILE_UUID)){
			query += andEq("reviewRequest.creator.profile.uuid", iFilters.get(ReviewFilters.REQUESTOR_PROFILE_UUID));
		}
		
		if (iFilters.isNotNull(ReviewFilters.NEGOTIATION_UUID)){
			query += andEq("negotiation.uuid", iFilters.get(ReviewFilters.NEGOTIATION_UUID));
		}
		
		if (iFilters.isNotNull(ReviewFilters.REVIEWER_UUID)){
			query += andEq("reviewer.uuid", iFilters.get(ReviewFilters.REVIEWER_UUID));
		}
		
		if (iFilters.isNotNull(ReviewFilters.OFFER_UUID)){
			query += andEq("offer.uuid", iFilters.get(ReviewFilters.OFFER_UUID));
		}
		
		return query;
	}
}
