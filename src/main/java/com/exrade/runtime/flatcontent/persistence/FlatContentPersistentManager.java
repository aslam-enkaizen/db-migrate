package com.exrade.runtime.flatcontent.persistence;

import com.exrade.models.flatcontent.FlatContent;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.IQuery;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;

public class FlatContentPersistentManager extends PersistentManager{

	public OrientSqlBuilder flatQuery = new OrientSqlBuilder() {
		
		@Override
		public String buildQuery(QueryFilters iFilters) {
			String nquery = "select from "+ FlatContent.class.getSimpleName() + " where 1 = 1 ";

			if (iFilters.isNotNull(FlatContentQFilters.URL)){
				nquery += andEq(FlatContentQFilters.URL, iFilters.get(FlatContentQFilters.URL));
			}
			
			if (iFilters.isNotNull(QueryParameters.UUID)){
				nquery += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
			}
			return nquery;
		}
	};
	
	/**
	 * Search a FlatContent entity by UUID
	 * @param UUID
	 * @return FlatContent, null otherwise
	 */
	public FlatContent read(QueryFilters iFilters) {
		FlatContent flatContent = null;

		IQuery nquery = flatQuery.createQuery(iFilters);

		flatContent = readObject(nquery);
		
		return flatContent;
	}

	public static class FlatContentQFilters{
		public final static String URL = "url";
	}
	
	
	public void delete(String iUrl){
		QueryFilters filters = QueryFilters.create(FlatContentQFilters.URL,iUrl);
		super.delete(read(filters));
	}

}
