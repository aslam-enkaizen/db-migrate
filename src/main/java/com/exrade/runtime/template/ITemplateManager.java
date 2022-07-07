package com.exrade.runtime.template;

import com.exrade.models.template.Template;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface ITemplateManager {

	public Template createTemplate(Template iTemplate);
	
	public Template updateTemplate(Template iTemplate);
	
	public void deleteTemplate(String uuid);
	
	Template getTemplateByUUID(String iTemplateUUID);
	
	List<Template> listTemplates(QueryFilters iFilters);
	
}
