package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.informationmodel.InformationModelCategory;
import com.exrade.models.informationmodel.InformationModelDocument;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.platform.persistence.SearchResultSummary;

import java.util.List;
import java.util.Map;

public interface InformationModelAPI {

	InformationModelTemplate create(ExRequestEnvelope request, InformationModelTemplate infoModelTemplate);

	InformationModelTemplate readByName(ExRequestEnvelope request, String infoModelName);

	InformationModelTemplate update(ExRequestEnvelope request, String infoModelUUID, InformationModelTemplate informationModelTemplate);

	void delete(ExRequestEnvelope request, String infoModelName);

	List<InformationModelTemplate> listModelsByCategory(ExRequestEnvelope request, String categoryPath);

	List<InformationModelCategory> listCategories(ExRequestEnvelope request, String categoryPath);

	List<InformationModelTemplate> listModels(ExRequestEnvelope request, Map<String, String> iFilters);

	InformationModelDocument createInformationDocument(ExRequestEnvelope request,
			InformationModelTemplate iInformationModelTemplate);

	InformationModelDocument createInformationDocument(ExRequestEnvelope request,
			String iInformationTemplateName);

	InformationModelDocument createInformationDocument(ExRequestEnvelope request,
			String iInformationTemplateUUID, String iProcessModelName);

	InformationModelTemplate readByUUID(ExRequestEnvelope request, String informationTemplateUUID);

	List<SearchResultSummary> listSearchResultSummary(ExRequestEnvelope request, Map<String, String> iFilters);

}