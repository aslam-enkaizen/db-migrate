package com.exrade.runtime.webhook;

import com.exrade.models.webhook.WebHook;
import com.exrade.platform.persistence.query.QueryFilters;

import java.util.List;

public interface IWebHookManager {
	WebHook create(WebHook webhook);

	WebHook findByUUID(String uuid);

	WebHook findByProfileUUID(String uuid);

	List<WebHook> listWebHooks(QueryFilters iFilters);
	
	WebHook update(String uuid, WebHook webHook);

	void delete(String uuid);

}
