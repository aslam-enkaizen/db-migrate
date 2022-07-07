package com.exrade.util;

import com.exrade.core.ExLogger;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Map;

public class RESTUtil {

    private static final RESTUtilSpring REST_UTIL_SPRING = new RESTUtilSpring();
    public static final long TIMEOUT = 30l * 1000;
    private static Logger LOGGER = ExLogger.get();

    public static String getBase64EncodedCredential(String userName, String password) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", userName, password).getBytes("UTF-8"));
    }

    public static JsonNode doRestGET(final String url, Map<String, String> headers, Map<String, String> queryParameters) {
        final JsonNode result = REST_UTIL_SPRING.doRestGET(url, headers, queryParameters);
        LOGGER.debug(JSONUtil.toJson(result));
        return result;
    }

    public static JsonNode doRestPOST(final String url, Map<String, String> headers, JsonNode body) {
        final JsonNode result = REST_UTIL_SPRING.doRestPOST(url, headers, body);
        LOGGER.debug(JSONUtil.toJson(result));
        return result;
    }

}
