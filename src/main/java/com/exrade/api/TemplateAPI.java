package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.template.Template;

import java.util.List;
import java.util.Map;

public interface TemplateAPI {

	Template createTemplate(ExRequestEnvelope request, Template iTemplate);
	
	void deleteTemplate(ExRequestEnvelope request, String iTemplateUUID);
	
	Template updateTemplate(ExRequestEnvelope request, Template iTemplate);
	
	List<Template> listTemplates(ExRequestEnvelope request, Map<String, String> iFilters);
	
	Template getTemplateByUUID(ExRequestEnvelope request, String iTemplateUUID);
}
