package com.exrade.runtime.i18n;

import com.exrade.models.i18n.ResourceBundleData;

import java.util.Locale;

public interface IResourceBundleDataManager {

	public String create(ResourceBundleData iResourceBundleData);
	
	public void delete(String uuid);

	public ResourceBundleData read(String languageTag,String iLocalizedEntityUUID);

	public ResourceBundleData read(Locale iLocale, String iLocalizedEntityUUID);
	
}
