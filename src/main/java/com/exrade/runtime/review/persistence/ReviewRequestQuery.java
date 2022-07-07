package com.exrade.runtime.review.persistence;

import com.exrade.models.review.ReviewRequest;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.ReviewFilters;
import com.exrade.runtime.rest.RestParameters.ReviewRequestFields;
import com.exrade.runtime.rest.RestParameters.ReviewRequestFilters;

public class ReviewRequestQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + ReviewRequest.class.getSimpleName()+ " where 1 = 1 ";

		if (iFilters.isNotNull(QueryParameters.UUID)){
			query += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}

		if (iFilters.isNotNull(ReviewRequestFilters.STATUS)){
			query += andEq(ReviewRequestFields.STATUS, iFilters.get(ReviewRequestFilters.STATUS));
		}

		if (iFilters.isNotNull(ReviewFilters.NEGOTIATION_UUID)){
			query += andEq("negotiation.uuid", iFilters.get(ReviewFilters.NEGOTIATION_UUID));
		}

		if (iFilters.isNotNull(ReviewRequestFilters.CREATOR_UUID)){
			query += andEq("creator.uuid", iFilters.get(ReviewRequestFilters.CREATOR_UUID));
		}

		if (iFilters.isNotNull(ReviewRequestFilters.CREATOR_PROFILE_UUID)){
			query += andEq("creator.profile.uuid", iFilters.get(ReviewRequestFilters.CREATOR_PROFILE_UUID));
		}

		if (iFilters.isNotNull(ReviewRequestFilters.REVIEW_TEMPLATE_UUID)){
			query += andEq(ReviewRequestFilters.REVIEW_TEMPLATE_UUID, iFilters.get(ReviewRequestFilters.REVIEW_TEMPLATE_UUID));
		}

		if (iFilters.isNotNull(ReviewRequestFilters.REVIEWER_UUID)){
			query += and(" reviewers contains (uuid = '" + iFilters.get(ReviewRequestFilters.REVIEWER_UUID) + "')");
		}

		if (iFilters.isNotNull(ReviewFilters.OFFER_UUID)){
			query += and(" offers contains (uuid = '" + iFilters.get(ReviewFilters.OFFER_UUID) + "')");
		}

		if (iFilters.isNotNull(ReviewFilters.NEGOTIATION_UUID)){
			query += andEq("negotiation.uuid", iFilters.get(ReviewFilters.NEGOTIATION_UUID));
		}

		if (iFilters.isNotNull(ReviewRequestFilters.CREATED_AFTER_INCLUSIVE)){
			query += and(condition(RestParameters.CREATION_DATE, iFilters.get(ReviewRequestFilters.CREATED_AFTER_INCLUSIVE),Operator.GTEQ));
		}

		return query;
	}
}
