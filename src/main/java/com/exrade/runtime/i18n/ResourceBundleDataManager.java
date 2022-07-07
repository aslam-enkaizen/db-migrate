package com.exrade.runtime.i18n;

import com.exrade.models.i18n.ResourceBundleData;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.i18n.persistent.ResourceBundleDataPersistentManager;
import com.exrade.runtime.i18n.persistent.ResourceBundleDataPersistentManager.ResourceBundleQFilters;

import java.util.Locale;
import java.util.Objects;

public class ResourceBundleDataManager implements IResourceBundleDataManager {

	private ResourceBundleDataPersistentManager resourceBundleDataPersistentManager;

	public ResourceBundleDataManager() {
		this(new ResourceBundleDataPersistentManager());
	}

	public ResourceBundleDataManager(ResourceBundleDataPersistentManager iExResourceBundlePersistentManager) {
		this.resourceBundleDataPersistentManager = iExResourceBundlePersistentManager;
	}
	
	@Override
	public String create(ResourceBundleData iResourceBundleData) {
		resourceBundleDataPersistentManager.create(iResourceBundleData);
		return iResourceBundleData.getUuid();
	}

	@Override
	public ResourceBundleData read(String iLanguageTag, String iLocalizedEntityUUID) {
		
		Objects.requireNonNull(iLanguageTag,"Parameter language tag required");
		Objects.requireNonNull(iLocalizedEntityUUID,"Parameter iLocalizedEntityUUID required");
		
		Locale locale = Locale.forLanguageTag(iLanguageTag);
		
		QueryFilters filters = new QueryFilters();
		
		filters.putIfNotNull(ResourceBundleQFilters.COUNTRY,locale.getCountry());
		filters.putIfNotNull(ResourceBundleQFilters.LANGUAGE,locale.getLanguage());
		filters.putIfNotNull(ResourceBundleQFilters.VARIANT,locale.getVariant());
		filters.putIfNotNull(ResourceBundleQFilters.LOCALIZEDENTITY_UUID,iLocalizedEntityUUID);
		
		return resourceBundleDataPersistentManager.read(filters);
	}
	
	@Override
	public ResourceBundleData read(Locale iLocale, String iLocalizedEntityUUID) {
		return read(iLocale.toLanguageTag(),iLocalizedEntityUUID);
	}

	@Override
	public void delete(String iUUID) {
		resourceBundleDataPersistentManager.delete(iUUID);
	}

	
	
}
