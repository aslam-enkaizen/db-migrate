package com.exrade.runtime.informationmodel;

import com.exrade.models.informationmodel.InformationModelCategory;
import com.exrade.models.informationmodel.InformationModelDocument;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IInformationModelManager {

	InformationModelTemplate create(InformationModelTemplate infoModelTemplate);

	InformationModelTemplate readByName(String infoModelName);

	InformationModelTemplate update(String infoModelUUID, InformationModelTemplate informationModelTemplate);

	void delete(String infoModelUUID);

	List<InformationModelTemplate> listModelsByCategory(String categoryPath);

	List<InformationModelCategory> listCategories(String categoryPath);

	List<InformationModelTemplate> listModels(QueryFilters filters);

	InformationModelDocument createInformationDocument(
			InformationModelTemplate iInformationModelTemplate);

	InformationModelDocument createInformationDocument(
			String iInformationTemplateName);

	InformationModelDocument createInformationDocument(
			String iInformationTemplateUUID, String iProcessModelName);

	InformationModelDocument copyInformationDocument(InformationModelDocument iInformationModelDocument);

	InformationModelTemplate readByUUID(String informationTemplateUUID);

	List<SearchResultSummary> listSearchResultSummary(QueryFilters iFilters);
}