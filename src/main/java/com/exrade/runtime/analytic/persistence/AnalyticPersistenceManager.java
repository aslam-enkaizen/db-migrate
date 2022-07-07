package com.exrade.runtime.analytic.persistence;

import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExPersistentException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryAlias;
import com.exrade.platform.persistence.query.QueryFilters;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnalyticPersistenceManager extends PersistentManager {
	public Map<Object, Long> listGroupedCounts(OrientSqlBuilder iQueryBuilder, QueryFilters iQueryFilters){

		Map<Object, Long> resultMap = new LinkedHashMap<Object, Long>();
		try {
			List<ODocument> oDocuments = listObjects(iQueryBuilder, iQueryFilters);
			
			if (oDocuments != null && oDocuments.size() > 0){
				for(ODocument document : oDocuments){
					resultMap.put(document.field(QueryAlias.GROUP_BY_FIELD), (long)document.field("count"));
				}
			}
		} catch (Exception ex) {
			throw new ExPersistentException(ErrorKeys.DB_READ_GENERIC, ex);
		}
		
		return resultMap;
	}
}
