package com.exrade.api.impl;

import com.exrade.api.WebHookAPI;
import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.webhook.WebHook;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.webhook.IWebHookManager;
import com.exrade.runtime.webhook.WebHookManager;
import com.exrade.util.ContextHelper;

import java.util.List;
import java.util.Map;

public class WebHookManagerAdapter implements WebHookAPI {

	IWebHookManager webHookManager = new WebHookManager();

	@Override
	public WebHook create(ExRequestEnvelope request, WebHook webhook) {
		ContextHelper.initContext(request);
		return webHookManager.create(webhook);
	}

	@Override
	public WebHook findByUUID(ExRequestEnvelope request, String uuid) {
		ContextHelper.initContext(request);
		return webHookManager.findByUUID(uuid);
	}

	@Override
	public WebHook update(ExRequestEnvelope request, String uuid, WebHook webHook) {
		ContextHelper.initContext(request);
		return webHookManager.update(uuid, webHook);
	}

	@Override
	public void delete(ExRequestEnvelope request, String uuid) {
		ContextHelper.initContext(request);
		webHookManager.delete(uuid);
	}

	@Override
	public List<WebHook> find(ExRequestEnvelope request, Map<String, String> filterParams) {
		ContextHelper.initContext(request);
		QueryFilters filters = QueryFilters.create(RestParameters.WebHookFields.PROFILE,
				ContextHelper.getMembership().getProfile().getUuid());
		return webHookManager.listWebHooks(filters);
	}

}
