package com.exrade.runtime.webhook;

import com.exrade.models.webhook.WebHook;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.webhook.persistence.WebHookQuery;

import java.util.List;

public class WebHookManager implements IWebHookManager {

	private final PersistentManager persistenceManager = new PersistentManager();

	@Override
	public WebHook create(WebHook webhook) {
		WebHook existingWebHook = findByProfileUUID(webhook.getProfile().getUuid());
		if(existingWebHook == null)
			return persistenceManager.create(webhook);
		
		return existingWebHook;
	}

	@Override
	public WebHook findByUUID(String uuid) {
		return persistenceManager.readObjectByUUID(WebHook.class, uuid);
	}

	@Override
	public WebHook findByProfileUUID(String uuid) {
		QueryFilters filters = QueryFilters.create(RestParameters.WebHookFields.PROFILE, uuid);
		List<WebHook> listObjects = this.listWebHooks(filters);
		if (!listObjects.isEmpty()) {
			return listObjects.get(0);
		}
		return null;
	}

	@Override
	public WebHook update(String uuid, WebHook webHook) {
		return persistenceManager.update(webHook);
	}

	@Override
	public void delete(String uuid) {
		persistenceManager.delete(uuid);
	}

	@Override
	public List<WebHook> listWebHooks(QueryFilters iFilters) {
		return persistenceManager.listObjects(new WebHookQuery(), iFilters);
	}

}
