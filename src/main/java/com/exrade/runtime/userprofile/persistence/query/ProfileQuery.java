package com.exrade.runtime.userprofile.persistence.query;

import com.exrade.models.userprofile.Profile;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.ProfileFields;
import com.exrade.runtime.rest.RestParameters.ProfileFilters;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class ProfileQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters filters) {
		
		String nquery = "select from " + Profile.class.getSimpleName() +" where 1 = 1 ";
		
		if (filters.containsKey(QueryParameters.UUID)){
			nquery += andEq(QueryParameters.UUID, filters.get(QueryParameters.UUID));
		}
		
		if (filters.isNotNull(QueryParameters.KEYWORDS)){
			 List<String> keywords = Lists.newArrayList(Splitter.on(" ").trimResults()
					 .omitEmptyStrings().split((String) filters.get(QueryParameters.KEYWORDS)));
			 for (String keyword : keywords) {
				 nquery += and(contains(QueryKeywords.ANY + ".toLowerCase()", keyword.toLowerCase()));
			 }
		}
		
		if (!filters.isNullOrEmpty(ProfileFields.COUNTRY)){
			nquery += andEq(ProfileFields.COUNTRY, filters.get(ProfileFields.COUNTRY));
		}
		
		if (!filters.isNullOrEmpty(ProfileFields.NAME)){
			nquery += andEq(ProfileFields.NAME, filters.get(ProfileFields.NAME));
		}
		
		if (!filters.isNullOrEmpty(ProfileFields.CITY)){
			nquery += andEq(ProfileFields.CITY, filters.get(ProfileFields.CITY));
		}
		
		if (!filters.isNullOrEmpty(ProfileFields.CITY)){
			nquery += andEq(ProfileFields.CITY, filters.get(ProfileFields.CITY));
		}
		
		if (!filters.isNullOrEmpty(ProfileFields.COMPETENCES)){
			nquery += orLike(ProfileFields.COMPETENCES, (String) filters.get(ProfileFields.COMPETENCES));
		}
		
		if (!filters.isNullOrEmpty(ProfileFields.NACE)){
			nquery += orLike(ProfileFields.NACE, (String) filters.get(ProfileFields.NACE));
		}
		
		if (!filters.isNullOrEmpty(ProfileFilters.PROFILE_TYPE)){
			nquery += andEq(QueryParameters.CLASS,Profile.class.getSimpleName());
		}
		
		if (!filters.isNullOrEmpty(ProfileQFilters.INCOMPLETE)){
			if(filters.isTrue(ProfileQFilters.INCOMPLETE))
				nquery += " and (address is null or city is null or country is null or address = '' or city = '' or country = '')";
		}
		
		if (!filters.isNullOrEmpty(ProfileFields.SUBDOMAIN)){
			nquery += andEq(ProfileFields.SUBDOMAIN, filters.get(ProfileFields.SUBDOMAIN));
		}
		
		return nquery;
	}
	
	private String orLike(String field, String values){
		List<String> parts = Lists.newArrayList(Splitter.on(",").trimResults()
				.omitEmptyStrings().split(values));
		List<String> subQueries = new ArrayList<String>();

		for(String part : parts){
			if(!part.trim().isEmpty()){
				subQueries.add(field+".toLowerCase()" + " like '%" + part.toLowerCase() + "%'");
			}
		}
		return and("(" + Joiner.on(" or ").skipNulls().join(subQueries) + ")");
	}
	
	public static final class ProfileQFilters {
		public static final String INCOMPLETE = "incomplete";
	}
	
}
