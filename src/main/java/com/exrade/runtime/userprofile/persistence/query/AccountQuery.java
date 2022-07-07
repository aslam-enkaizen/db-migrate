package com.exrade.runtime.userprofile.persistence.query;

import com.exrade.models.userprofile.User;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.LinkedAccountFields;
import com.exrade.runtime.rest.RestParameters.UserFields;
import com.exrade.runtime.rest.RestParameters.UserFilters;

public class AccountQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from " + User.class.getSimpleName() +" where 1 = 1 ";
		
		nquery += addEqFilter(iFilters, QueryParameters.UUID);

		if (!iFilters.isNullOrEmpty(RestParameters.KEYWORDS)){
			String keywordSearch = startWith(UserFields.FIRST_NAME+".append(' ').append("+UserFields.LAST_NAME+")",(String) iFilters.get(RestParameters.KEYWORDS));
			keywordSearch += or(startWith(UserFields.EMAIL, iFilters.get(RestParameters.KEYWORDS).toString()));
			keywordSearch += or(startWith(UserFields.PHONE, iFilters.get(RestParameters.KEYWORDS).toString()));
			nquery += and(keywordSearch);
		}
		
		nquery += addEqFilter(iFilters, UserFields.USER_NAME);
		nquery += addEqFilter(iFilters, UserFields.EMAIL);
		nquery += addEqFilter(iFilters, UserFields.PHONE);
		nquery += addEqFilter(iFilters, UserFields.ACCOUNT_STATUS);
		nquery += addEqFilter(iFilters, UserFields.FIRST_NAME);
		nquery += addEqFilter(iFilters, UserFields.LAST_NAME);
		nquery += addEqFilter(iFilters, UserFields.SUPERADMIN);
		
		if (!iFilters.isNullOrEmpty(UserFilters.NOT_ACCOUNT_STATUS))
			nquery += andNotEq(UserFields.ACCOUNT_STATUS, iFilters.get(UserFilters.NOT_ACCOUNT_STATUS));
		
		nquery += andInnerCollectionFilter(iFilters, LinkedAccountFields.PROVIDER_KEY, UserFields.LINKED_ACCOUNTS, LinkedAccountFields.PROVIDER_KEY);

		nquery += andInnerCollectionFilter(iFilters, LinkedAccountFields.PROVIDER_USER_ID, UserFields.LINKED_ACCOUNTS, LinkedAccountFields.PROVIDER_USER_ID);
				
		return nquery;
	}

}
