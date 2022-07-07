package com.exrade.runtime.clause;

import com.exrade.models.informationmodel.Clause;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;
import java.util.Map;

/**
 *
 * @author: Md. Aslam Hossain
 *
 */
public interface IClauseManager {
	public Clause createClause(Clause iClause);

	public Clause getClause(String iClauseUUID);

	public List<Clause> listClauses(QueryFilters iFilters);

	public Clause updateClause(Clause iClause);

	public void deleteClause(String iClauseUUID);

	List<SearchResultSummary> listSearchResultSummary(Map<String, String> iFilters);
}
