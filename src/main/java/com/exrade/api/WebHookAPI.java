package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.webhook.WebHook;

import java.util.List;
import java.util.Map;

public interface WebHookAPI {

    WebHook create(ExRequestEnvelope request, WebHook webhook);

    WebHook findByUUID(ExRequestEnvelope request, String uuid);
    
    List<WebHook> find(ExRequestEnvelope request, Map<String, String> filterParams);

    WebHook update(ExRequestEnvelope request, String uuid, WebHook webHook);

    void delete(ExRequestEnvelope request, String uuid);

}
