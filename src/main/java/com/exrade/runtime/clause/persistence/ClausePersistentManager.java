package com.exrade.runtime.clause.persistence;

import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExPersistentException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.google.common.base.Strings;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Md. Aslam Hossain
 * @Date Apr 21, 2022
 *
 */
public class ClausePersistentManager extends PersistentManager {
	public SearchResultSummary getSearchResultSummary(OrientSqlBuilder iQueryBuilder, QueryFilters iQueryFilters) {

		SearchResultSummary searchResultSummary = null;
		try {
			List<ODocument> result = listObjects(iQueryBuilder, iQueryFilters);
			Map<String, Long> resultMap = new HashMap<String, Long>();

			if (result != null && result.size() > 0) {
				for (ODocument document : result) {
					String value = (String) document.field("value");
					if (!Strings.isNullOrEmpty(value))
						resultMap.put(value, (long) document.field("count"));
				}
			}

			searchResultSummary = new SearchResultSummary();
			searchResultSummary.setName((String) iQueryFilters.get(QueryParameters.FIELD));
			searchResultSummary.setValues(resultMap);
		} catch (Exception ex) {
			throw new ExPersistentException(ErrorKeys.DB_READ_GENERIC, ex);
		}

		return searchResultSummary;
	}
}
