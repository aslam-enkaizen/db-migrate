package com.exrade.runtime.informationmodel.persistence.query;

import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.platform.persistence.query.ModelVersionQueryUtil;
import com.exrade.platform.persistence.query.ModelVersionQueryUtil.ModelVersionQueryFilters;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.informationmodel.persistence.InformationTemplatePersistentManager.InformationTemplateQueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.InformationTemplateFields;
import com.exrade.runtime.rest.RestParameters.InformationTemplateFilters;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class InformationTemplateQuery  extends OrientSqlBuilder{

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String nquery = "select from "+ InformationModelTemplate.class.getSimpleName() + " where 1 = 1 ";

		if (!iFilters.containsKey(InformationTemplateFilters.INCLUDE_ARCHIVED) || !iFilters.isTrue(InformationTemplateFilters.INCLUDE_ARCHIVED)){
			nquery += and(" (archived is null or archived == false) ");
		}

		if (iFilters.isNotNull(InformationTemplateQueryFilters.NAME)){
			nquery += andEq(InformationTemplateQueryFilters.NAME, iFilters.get(InformationTemplateQueryFilters.NAME));
		}
		if (iFilters.isNotNull(QueryParameters.UUID)){
			nquery += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
		}
		if (iFilters.containsKey(InformationTemplateQueryFilters.TAGS) && iFilters.get(InformationTemplateQueryFilters.TAGS) != null){
			nquery += and(InformationTemplateQueryFilters.TAGS+" contains (value in ['" + iFilters.get(InformationTemplateQueryFilters.TAGS) + "'])");
		}
		if (iFilters.isNotNull(InformationTemplateQueryFilters.CATEGORY)){
			nquery += andEq(InformationTemplateQueryFilters.CATEGORY, iFilters.get(InformationTemplateQueryFilters.CATEGORY));
		}

		if (iFilters.isNotNull(ModelVersionQueryFilters.MODEL_VERSION)){
			nquery += ModelVersionQueryUtil.generateModelVersionQuery(iFilters, InformationModelTemplate.class.getSimpleName()) ;
		}
		if (iFilters.isNotNull(InformationTemplateQueryFilters.PRIVACY_LEVEL)){
			nquery += andEq(InformationTemplateQueryFilters.PRIVACY_LEVEL, iFilters.get(InformationTemplateQueryFilters.PRIVACY_LEVEL));
		}
		if (iFilters.isNotNull(InformationTemplateQueryFilters.PUBLISH_STATUS)){
			nquery += andEq(InformationTemplateQueryFilters.PUBLISH_STATUS, iFilters.get(InformationTemplateQueryFilters.PUBLISH_STATUS));
		}
		if (iFilters.isNotNull(InformationTemplateQueryFilters.AUTHOR)){
			nquery += andEq(InformationTemplateQueryFilters.AUTHOR, iFilters.get(InformationTemplateQueryFilters.AUTHOR));
		}
		if (iFilters.isNotNull(InformationTemplateFields.AUTHOR_MEMBERSHIP_UUID)){
			nquery += andEq("authorMembership.uuid", iFilters.get(InformationTemplateFields.AUTHOR_MEMBERSHIP_UUID));
		}
		if (iFilters.isNotNull(InformationTemplateQueryFilters.PROFILE)){
			nquery += andEq("authorMembership.profile.uuid", iFilters.get(InformationTemplateQueryFilters.PROFILE));
		}
		if (iFilters.isNotNull(InformationTemplateFields.CATEGORY)){
			nquery += andEq(InformationTemplateFields.CATEGORY, iFilters.get(InformationTemplateFields.CATEGORY));
		}

		if (!iFilters.isNullOrEmpty(RestParameters.KEYWORDS)){
			List<String> keywords = Lists.newArrayList(Splitter.on(" ").trimResults()
				       .omitEmptyStrings().split((String) iFilters.get(RestParameters.KEYWORDS)));
			for (String keyword : keywords) {
				nquery += and(contains(QueryKeywords.ANY + ".toLowerCase()", keyword.toLowerCase()));
			}
		}
		return nquery;
	}


}
