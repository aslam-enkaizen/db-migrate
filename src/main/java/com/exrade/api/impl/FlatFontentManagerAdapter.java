package com.exrade.api.impl;

import com.exrade.api.FlatContentAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.flatcontent.FlatContent;
import com.exrade.runtime.flatcontent.FlatContentManager;
import com.exrade.runtime.flatcontent.IFlatContentManager;
import com.exrade.util.ContextHelper;

public class FlatFontentManagerAdapter implements FlatContentAPI {

	private IFlatContentManager manager = new FlatContentManager();
	
	@Override
	public String createFlatContent(ExRequestEnvelope request,
			FlatContent iFlatContent) {
		ContextHelper.initContext(request);
		return manager.createFlatContent(iFlatContent);
	}

	@Override
	public FlatContent getFlatContentByUrl(ExRequestEnvelope request,
			String iUrl) {
		ContextHelper.initContext(request);
		return manager.getFlatContentByUrl(iUrl);
	}

	@Override
	public FlatContent getFlatContentByUUID(ExRequestEnvelope request,
			String iUUID) {
		ContextHelper.initContext(request);
		return manager.getFlatContentByUUID(iUUID);
	}

	@Override
	public FlatContent getFlatContentByUrl(ExRequestEnvelope request,
			String iUrl, String iLanguageTag) {
		ContextHelper.initContext(request);
		return manager.getFlatContentByUrl(iUrl, iLanguageTag);
	}

	@Override
	public void update(ExRequestEnvelope request, FlatContent flatContent) {
		ContextHelper.initContext(request);
		manager.update(flatContent);
	}

	@Override
	public void delete(ExRequestEnvelope request, String uuid) {
		ContextHelper.initContext(request);
		manager.delete(uuid);
	}

}
