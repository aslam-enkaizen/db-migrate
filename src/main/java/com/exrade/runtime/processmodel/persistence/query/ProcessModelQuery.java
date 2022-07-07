package com.exrade.runtime.processmodel.persistence.query;

import com.exrade.models.processmodel.ProcessModel;
import com.exrade.platform.persistence.query.ModelVersionQueryUtil;
import com.exrade.platform.persistence.query.ModelVersionQueryUtil.ModelVersionQueryFilters;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.processmodel.persistence.ProcessModelPersistentManager.ProcessModelQFilters;
import com.exrade.runtime.rest.RestParameters.ProcessModelFields;
import com.exrade.runtime.rest.RestParameters.ProcessModelFilters;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class ProcessModelQuery extends OrientSqlBuilder{

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from "+ ProcessModel.class.getSimpleName() + " where 1 = 1 ";
		
		if (iFilters.isNotNull(ProcessModelQFilters.NAME)){
			nquery += andEq(ProcessModelQFilters.NAME, iFilters.get(ProcessModelQFilters.NAME));
		}
		if (iFilters.isNotNull(QueryParameters.UUID)){
			nquery += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		
		if (iFilters.isNotNull(ProcessModelFields.TAGS)){
			nquery += and("tags contains ("+in("value.toLowerCase()",iFilters.get(ProcessModelFields.TAGS))+")");
		}
		
		if (iFilters.isNotNull(ProcessModelFilters.KEYWORDS)){
			List<String> keywords = Lists.newArrayList(Splitter.on(" ").trimResults()
					.omitEmptyStrings().split((String) iFilters.get(ProcessModelFilters.KEYWORDS)));
			for (String keyword : keywords) {
				nquery += and(contains(QueryKeywords.ANY + ".toLowerCase()", keyword.toLowerCase()));
			}
		}
		
		if (iFilters.isNotNull(ProcessModelFields.PRIVACY_LEVEL)){
			nquery += andEq(ProcessModelFields.PRIVACY_LEVEL, iFilters.get(ProcessModelFields.PRIVACY_LEVEL));
		}
		
		if (iFilters.isNotNull(ModelVersionQueryFilters.MODEL_VERSION)){
			nquery += ModelVersionQueryUtil.generateModelVersionQuery(iFilters, ProcessModel.class.getSimpleName()) ;
		}
		
		
		return nquery;
	}
	

}
