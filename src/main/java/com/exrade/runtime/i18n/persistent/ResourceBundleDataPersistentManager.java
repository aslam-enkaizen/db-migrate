package com.exrade.runtime.i18n.persistent;

import com.exrade.models.i18n.ResourceBundleData;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;

public class ResourceBundleDataPersistentManager extends PersistentManager {

	public final static OrientSqlBuilder queryBuilder = new OrientSqlBuilder() {
		@Override
		protected String buildQuery(QueryFilters filters) {
			String nquery = "select from "+ ResourceBundleData.class.getSimpleName() + " where 1 = 1 ";

			if (filters.isNotNull(ResourceBundleQFilters.COUNTRY)){
				nquery += andEq(ResourceBundleQFilters.COUNTRY, filters.get(ResourceBundleQFilters.COUNTRY));
			}
			
			if (filters.isNotNull(ResourceBundleQFilters.LANGUAGE)){
				nquery += andEq(ResourceBundleQFilters.LANGUAGE, filters.get(ResourceBundleQFilters.LANGUAGE));
			}
			
			if (filters.isNotNull(ResourceBundleQFilters.VARIANT)){
				nquery += andEq(ResourceBundleQFilters.VARIANT, filters.get(ResourceBundleQFilters.VARIANT));
			}
			
			if (filters.isNotNull(ResourceBundleQFilters.LOCALIZEDENTITY_UUID)){
				nquery += andEq(ResourceBundleQFilters.LOCALIZEDENTITY_UUID, filters.get(ResourceBundleQFilters.LOCALIZEDENTITY_UUID));
			}
			
			if (filters.isNotNull(QueryParameters.UUID)){
				nquery += andEq(QueryParameters.UUID, filters.get(QueryParameters.UUID));
			}
			return nquery;
		}
	};
	
	
	public ResourceBundleData read(QueryFilters filters){
		ResourceBundleData resourceBundleData = null;
		queryBuilder.createQuery(filters);
		resourceBundleData = readObject(queryBuilder.createQuery(filters));
		return resourceBundleData;
	}

	public static class ResourceBundleQFilters{
		public final static String COUNTRY = "country";
		public final static String LANGUAGE = "language";
		public final static String VARIANT = "variant";
		public final static String LOCALIZEDENTITY_UUID = "localizedEntityUUID";
	}
	
	public void delete(String iUUID){
		QueryFilters filters = QueryFilters.create(QueryParameters.UUID,iUUID);
		ResourceBundleData resourceBundleData = read(filters);
		delete(resourceBundleData);
	}
	
}
