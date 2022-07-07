package com.exrade.api.impl;

import com.exrade.api.TemplateAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.template.Template;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.template.ITemplateManager;
import com.exrade.runtime.template.TemplateManager;
import com.exrade.runtime.template.persistence.TemplatePersistenceManager.TemplateQFilters;
import com.exrade.util.ContextHelper;

import java.util.List;
import java.util.Map;

public class TemplateManagerAdapter implements TemplateAPI {

	private ITemplateManager manager = new TemplateManager();

	@Override
	public Template createTemplate(ExRequestEnvelope request, Template iTemplate) {
		ContextHelper.initContext(request);
		return manager.createTemplate(iTemplate);
	}

	@Override
	public void deleteTemplate(ExRequestEnvelope request, String iTemplateUUID) {
		ContextHelper.initContext(request);
		manager.deleteTemplate(iTemplateUUID);
	}

	@Override
	public Template updateTemplate(ExRequestEnvelope request, Template iTemplate) {
		ContextHelper.initContext(request);
		return manager.updateTemplate(iTemplate);
	}

	@Override
	public List<Template> listTemplates(ExRequestEnvelope request, Map<String, String> iFilters) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.putIfNotEmpty(TemplateQFilters.TEMPLATE_TYPE, iFilters.get(TemplateQFilters.TEMPLATE_TYPE));

		filters.putIfNotEmpty(TemplateQFilters.OWNER_PROFILE, ContextHelper.getMembership().getProfile().getUuid());
		filters.putIfNotEmpty(TemplateQFilters.OWNER_MEMBERSHIP, iFilters.get(TemplateQFilters.OWNER_MEMBERSHIP));

		if (filters.isNullOrEmpty(QueryParameters.SORT)){
			filters.put(QueryParameters.SORT,"name");
		}

		return manager.listTemplates(filters);
	}

	@Override
	public Template getTemplateByUUID(ExRequestEnvelope request, String iTemplateUUID) {
		ContextHelper.initContext(request);
		return manager.getTemplateByUUID(iTemplateUUID);
	}

}
