package com.exrade.runtime.flatcontent;

import com.exrade.models.flatcontent.FlatContent;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.flatcontent.persistence.FlatContentPersistentManager;
import com.exrade.runtime.flatcontent.persistence.FlatContentPersistentManager.FlatContentQFilters;
import com.exrade.util.i18n.resourcebundle.ExResourceBundleControl;

import java.util.Locale;
import java.util.ResourceBundle;

public class FlatContentManager implements IFlatContentManager {

	private FlatContentPersistentManager flatContentPersistentManager;

	public FlatContentManager() {
		this(new FlatContentPersistentManager());
	}

	public FlatContentManager(FlatContentPersistentManager iFlatContentPersistentManager) {
		this.flatContentPersistentManager = iFlatContentPersistentManager;
	}

	@Override
	public FlatContent getFlatContentByUrl(String iUrl) {
		QueryFilters filters = QueryFilters.create(FlatContentQFilters.URL,iUrl);
		FlatContent flatContent = flatContentPersistentManager.read(filters);
		return flatContent;
	}

	@Override
	public FlatContent getFlatContentByUrl(String iUrl, String iLanguageTag) {

		FlatContent flatContent = getFlatContentByUrl(iUrl);
		if (iLanguageTag != null){
			ResourceBundle resourceBundle = ResourceBundle.getBundle(flatContent.getUuid(),Locale.forLanguageTag(iLanguageTag),new ExResourceBundleControl());
			flatContent.localize(resourceBundle);
		}
		return flatContent;
	}

	@Override
	public String createFlatContent(FlatContent iFlatContent) {
		flatContentPersistentManager.create(iFlatContent);
		return iFlatContent.getUuid();
	}

	@Override
	public FlatContent getFlatContentByUUID(String iUUID) {
		QueryFilters filters = QueryFilters.create(QueryParameters.UUID,iUUID);
		FlatContent flatContent = flatContentPersistentManager.read(filters);
		return flatContent;
	}

	@Override
	public void update(FlatContent flatContent) {
		flatContentPersistentManager.update(flatContent);
	}

	@Override
	public void delete(String uuid) {
		flatContentPersistentManager.delete(uuid);
	}


}
