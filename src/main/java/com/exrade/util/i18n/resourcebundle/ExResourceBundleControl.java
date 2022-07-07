package com.exrade.util.i18n.resourcebundle;

import com.exrade.models.i18n.ResourceBundleData;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExException;
import com.exrade.runtime.i18n.IResourceBundleDataManager;
import com.exrade.runtime.i18n.ResourceBundleDataManager;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class ExResourceBundleControl extends ResourceBundle.Control {

	private final static String EXRESOURCE = "exresource";

	@Override
	public ResourceBundle newBundle(String iLocalizedEntityUUID, Locale iLocale, String format,
			ClassLoader loader, boolean reload) throws IllegalAccessException,
			InstantiationException, IOException {

		if ((iLocalizedEntityUUID == null) || (iLocale == null) || (format == null) || (loader == null)) {
			throw new NullPointerException();
		}

		if (!format.equals(EXRESOURCE)) { 
			return null; 
		}

		Properties p   = new Properties();

		IResourceBundleDataManager resourceBundleDataManager = new ResourceBundleDataManager(); 

		ResourceBundleData resourceBundleData = resourceBundleDataManager.read(iLocale, iLocalizedEntityUUID);

		if (resourceBundleData == null ) {
			Map<String, String> replaceValues = new HashMap<String, String>();
			replaceValues.put("@@UUID@@", iLocalizedEntityUUID);
			replaceValues.put("@@LANGUAGE@@", iLocale.toLanguageTag());
			throw new ExException(ErrorKeys.RESOURCE_CANNOT_LOAD, replaceValues);
		}

		for (Entry<String,String> entry : resourceBundleData.getKeyToLocalizedValue().entrySet()) {
			p.setProperty(entry.getKey(),entry.getValue());
		}

		return new ExResourceBundle(p);
	}

	public List<String> getFormats(String baseName) {
		return Collections.singletonList(EXRESOURCE);
	}
	
	@Override
	public List<Locale> getCandidateLocales(String baseName, Locale locale) {
		List<Locale> locales = super.getCandidateLocales(baseName, locale);
		
		
		
		return locales;
	}

}
