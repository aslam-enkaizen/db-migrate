package com.exrade.runtime.processmodel.persistence;

import com.exrade.models.processmodel.ProcessModel;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.processmodel.persistence.query.ProcessModelQuery;

import java.util.List;


public class ProcessModelPersistentManager extends PersistentManager {

	public ProcessModel read(QueryFilters filters) {
		return readObject(new ProcessModelQuery(),filters);
	}

	public void delete(String iProcessModelUUID) {
		QueryFilters filters = QueryFilters.create(QueryParameters.UUID,iProcessModelUUID);
		ProcessModel processModel = read(filters);
		delete(processModel);
	}

	public List<ProcessModel> listProcessModels(QueryFilters filters) {
		List<ProcessModel> processModels = listObjects(new ProcessModelQuery(),filters);
		return processModels;
	}
	
	public static class ProcessModelQFilters{
		public final static String NAME = "name";
		//public final static String MODEL_VERSION = "modelVersion";
		//public final static String MAX = "max";
	}

}

