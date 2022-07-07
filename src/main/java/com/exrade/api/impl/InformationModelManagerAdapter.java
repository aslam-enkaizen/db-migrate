package com.exrade.api.impl;

import com.exrade.api.InformationModelAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.informationmodel.InformationModelCategory;
import com.exrade.models.informationmodel.InformationModelDocument;
import com.exrade.models.informationmodel.InformationModelTemplate;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.security.MemberRole;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.informationmodel.IInformationModelManager;
import com.exrade.runtime.informationmodel.InformationModelManager;
import com.exrade.runtime.informationmodel.persistence.InformationTemplatePersistentManager.InformationTemplateQueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.InformationTemplateFields;
import com.exrade.runtime.rest.RestParameters.InformationTemplateFilters;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InformationModelManagerAdapter implements InformationModelAPI {

	private IInformationModelManager manager = new InformationModelManager();

	@Override
	public InformationModelTemplate create(ExRequestEnvelope request,
			InformationModelTemplate infoModelTemplate) {
		ContextHelper.initContext(request);
		infoModelTemplate.setAuthorMembership((Membership)ContextHelper.getMembership());
		return manager.create(infoModelTemplate);
	}

	@Override
	public InformationModelTemplate readByName(ExRequestEnvelope request,
			String infoModelName) {
		ContextHelper.initContext(request);
		InformationModelTemplate template = manager.readByName(infoModelName);
		Security.checkInformationModelTemplateAccess(template);
		return template;
	}

	@Override
	public InformationModelTemplate update(ExRequestEnvelope request, String infoModelUUID,
			InformationModelTemplate informationModelTemplate) {
		ContextHelper.initContext(request);
		return manager.update(infoModelUUID, informationModelTemplate);
	}

	@Override
	public void delete(ExRequestEnvelope request, String infoModelName) {
		ContextHelper.initContext(request);
		manager.delete(infoModelName);
	}

	@Override
	public List<InformationModelTemplate> listModelsByCategory(
			ExRequestEnvelope request, String categoryPath) {
		ContextHelper.initContext(request);
		return manager.listModelsByCategory(categoryPath);
	}

	@Override
	public List<InformationModelCategory> listCategories(
			ExRequestEnvelope request, String categoryPath) {
		ContextHelper.initContext(request);
		return manager.listCategories(categoryPath);
	}

	@Override
	public List<InformationModelTemplate> listModels(ExRequestEnvelope request,
			Map<String, String> iFilters) {
		ContextHelper.initContext(request);

		Negotiator requestor = ContextHelper.getMembership();
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putIfNotEmpty(RestParameters.KEYWORDS, iFilters.get(RestParameters.KEYWORDS));
		filters.putIfNotEmpty(InformationTemplateQueryFilters.TAGS, iFilters.get(InformationTemplateQueryFilters.TAGS));
		filters.putIfNotEmpty(InformationTemplateQueryFilters.CATEGORY, iFilters.get(InformationTemplateQueryFilters.CATEGORY));
		filters.putIfNotEmpty(InformationTemplateQueryFilters.PRIVACY_LEVEL, iFilters.get(InformationTemplateQueryFilters.PRIVACY_LEVEL));
		filters.putIfNotEmpty(InformationTemplateQueryFilters.PUBLISH_STATUS, iFilters.get(InformationTemplateQueryFilters.PUBLISH_STATUS));
		filters.putIfNotEmpty(InformationTemplateQueryFilters.AUTHOR, iFilters.get(InformationTemplateQueryFilters.AUTHOR));
		filters.putIfNotEmpty(InformationTemplateFields.AUTHOR_MEMBERSHIP_UUID, iFilters.get(InformationTemplateFields.AUTHOR_MEMBERSHIP_UUID));

		if(!Strings.isNullOrEmpty(iFilters.get(InformationTemplateFilters.INCLUDE_ARCHIVED)))
			filters.putIfNotNull(InformationTemplateFilters.INCLUDE_ARCHIVED, Boolean.parseBoolean(iFilters.get(InformationTemplateFilters.INCLUDE_ARCHIVED)));

		if(Strings.isNullOrEmpty(iFilters.get(InformationTemplateQueryFilters.PROFILE))
				|| !Security.isMembership(iFilters.get(InformationTemplateQueryFilters.PROFILE), Arrays.asList(MemberRole.OWNER, MemberRole.ADMIN, MemberRole.MEMBER)))
			filters.putIfNotEmpty(InformationTemplateQueryFilters.PROFILE, requestor.getProfile().getUuid());
		else
			filters.putIfNotEmpty(InformationTemplateQueryFilters.PROFILE, iFilters.get(InformationTemplateQueryFilters.PROFILE));

		if (filters.isNullOrEmpty(QueryParameters.SORT)){
			filters.put(QueryParameters.SORT, OrientSqlBuilder.DESC_SORT+InformationTemplateFields.PULICATION_DATE);
		}

		return manager.listModels(filters);
	}

	@Override
	public InformationModelDocument createInformationDocument(
			ExRequestEnvelope request,
			InformationModelTemplate iInformationModelTemplate) {
		ContextHelper.initContext(request);
		return manager.createInformationDocument(iInformationModelTemplate);
	}

	@Override
	public InformationModelDocument createInformationDocument(
			ExRequestEnvelope request, String iInformationTemplateName) {
		ContextHelper.initContext(request);
		return manager.createInformationDocument(iInformationTemplateName);
	}

	@Override
	public InformationModelTemplate readByUUID(ExRequestEnvelope request,
			String informationTemplateUUID) {
		ContextHelper.initContext(request);
		return manager.readByUUID(informationTemplateUUID);
	}

	@Override
	public InformationModelDocument createInformationDocument(ExRequestEnvelope request, String iInformationTemplateUUID, String iProcessModelName) {
		ContextHelper.initContext(request);
		return manager.createInformationDocument(iInformationTemplateUUID, iProcessModelName);
	}

	@Override
	public List<SearchResultSummary> listSearchResultSummary(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		List<SearchResultSummary> searchResultSummries = new ArrayList<>();
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.put("profile.uuid", ContextHelper.getMembership().getProfile().getUuid());

		for(String field : ExCollections.commaSeparatedToList(iFilters.get(QueryParameters.FIELD))) {
			filters.put(QueryParameters.FIELD, field);
			searchResultSummries.addAll(manager.listSearchResultSummary(filters));
		}

		return searchResultSummries;
	}

}
