package com.exrade.runtime.template;

import com.exrade.core.ExLogger;
import com.exrade.models.i18n.ExLang;
import com.exrade.runtime.conf.ExConfiguration;
import com.google.common.base.Strings;
import freemarker.cache.*;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FreemarkerTemplateProcessor implements ITemplateProcessor {

	public static final String DEFAULT_TEMPLATE_FILE = "default.ftl";

	public FreemarkerTemplateProcessor(){
	}

	@Override
	public byte[] process(Map<String, Object> dataMap, String templateContent)
			throws Exception {
		ByteArrayOutputStream ios = new ByteArrayOutputStream();
		byte[] outputData = null;

		Configuration cfg = new Configuration();

		// add template into template loader
		StringTemplateLoader stringLoader = new StringTemplateLoader();
		stringLoader.putTemplate(DEFAULT_TEMPLATE_FILE, templateContent);

		cfg.setTemplateLoader(stringLoader);

		Template template = cfg.getTemplate(DEFAULT_TEMPLATE_FILE);

		includeAppName(dataMap);
		try {
			// process html template
			template.process(dataMap, new OutputStreamWriter(ios));
			outputData = ios.toByteArray();
		} finally {
			try {
				ios.close();
			} catch (IOException e) {
				ExLogger.get().error("Error in closing output stream",e);
			}
		}
		return outputData;
	}

	/***
	 * This method loads the specified template from a the base directory templateSet.
	 */
	@Override
	public byte[] processTemplate(Map<String, Object> dataMap, String templateBaseDir, String templateSet, String templateName,
			String locale) throws Exception {
		ByteArrayOutputStream ios = new ByteArrayOutputStream();
		byte[] outputData = null;

		Configuration cfg = new Configuration();

		List<TemplateLoader> templateLoaders = new ArrayList<>();
		File templateDir = new File(getTemplateDir(templateSet, locale));

		// Set Directory for templates
		if(templateDir.exists())
			templateLoaders.add(new FileTemplateLoader(new File(getTemplateDir(templateSet, locale))));

		templateLoaders.add(new ClassTemplateLoader(this.getClass(), templateBaseDir));
		templateLoaders.add(new ClassTemplateLoader(this.getClass(), getTemplateDir(templateBaseDir + templateSet, locale)));

		MultiTemplateLoader mtl = new MultiTemplateLoader(templateLoaders.toArray(new TemplateLoader[templateLoaders.size()]));

		cfg.setTemplateLoader(mtl);

		// load template
		Template template = cfg.getTemplate(Strings.isNullOrEmpty(templateName) ? DEFAULT_TEMPLATE_FILE : templateName ,new Locale(locale),"UTF-8");

		includeAppName(dataMap);
		try {
			// process html template
			template.process(dataMap, new OutputStreamWriter(ios,"UTF-8"));
			outputData = ios.toByteArray();
		} finally {
			try {
				ios.close();
			} catch (IOException e) {
				ExLogger.get().error("Error in closing output stream",e);
			}
		}
		return outputData;
	}

	private String getTemplateDir(String templateSet, String locale) {
		String templateBaseDir = templateSet;
		if(!templateBaseDir.endsWith(File.separator))
			templateBaseDir = templateSet + File.separator;

		if (Strings.isNullOrEmpty(locale))
			return templateBaseDir + ExLang.ENGLISH.getCode();
		else
			return templateBaseDir + locale.toLowerCase();
	}

	private void includeAppName(Map<String, Object> dataMap) {
		if(dataMap != null && !dataMap.containsKey("app_name")) {
			dataMap.put("app_name", ExConfiguration.getStringProperty("app.name"));
		}
	}

	@Override
	public byte[] processTemplateForPdf(Map<String, Object> dataMap, String templateBaseDir, String templateSet,
			String templateFile, String locale) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
