package com.exrade.runtime.workgroup.persistence;

import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExPersistentException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkGroupPersistenceManager extends PersistentManager {

	public SearchResultSummary getSearchResultSummary(OrientSqlBuilder iQueryBuilder, QueryFilters iQueryFilters){

		SearchResultSummary searchResultSummary = null;
		try {
			List<ODocument> result = listObjects(iQueryBuilder, iQueryFilters);
			Map<String, Long> resultMap = new HashMap<String, Long>();
			
			if (result != null && result.size() > 0){
				for(ODocument document : result){
					resultMap.put((String)document.field("value"), (long)document.field("count"));
				}
			}
			
			searchResultSummary = new SearchResultSummary();
			searchResultSummary.setName((String)iQueryFilters.get(QueryParameters.FIELD));
			searchResultSummary.setValues(resultMap);
		} catch (Exception ex) {
			throw new ExPersistentException(ErrorKeys.DB_READ_GENERIC, ex);
		}
		
		return searchResultSummary;
	}
	
	public static class WorkGroupQFilters{
		public final static String TAGS = "tags";
		public final static String NAME = "name";
		public final static String SUBJECT = "subject";
		public final static String OWNER_PROFILE = "owner.profile";
		public final static String OWNER = "owner";
	}
	
}
