package com.exrade.util;

import com.exrade.platform.exception.ExException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

/**
 * @author Rhidoy
 * @created 30/09/2021
 */
@Component
public class RESTUtilSpring {
    private static final Logger LOGGER = LoggerFactory.getLogger(RESTUtil.class);
    private static final String CLASS_NAME = " in " +
            RESTUtil.class.getSimpleName() + " class";
    @Autowired
    private WebClient webClient;

    public static String getBase64EncodedCredential(String userName, String password) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", userName, password).getBytes(StandardCharsets.UTF_8));
    }

    public JsonNode doRestGET(final String url, Map<String, String> headers, Map<String, String> queryParameters) {

        LOGGER.info("Enter doRestGET()" + CLASS_NAME);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (queryParameters != null)
            for (Map.Entry<String, String> parameter : queryParameters.entrySet()) {
                queryParams.add(parameter.getKey(), parameter.getValue());
            }

        return webClient.get()
                .uri(builder -> builder.path(url)
                        .queryParams(queryParams)
                        .build()
                )
                .headers(httpHeaders -> {
                    //set headers
                    if (headers != null) {
                        for (Map.Entry<String, String> p : headers.entrySet()) {
                            httpHeaders.set(p.getKey(),
                                    p.getValue());
                        }
                    }
                })
                .retrieve()
                .bodyToMono(JsonNode.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(100)))
                .doOnError(error -> {
                    throw new ExException(String.format("%s", error.getMessage()));
                })
                .block();
//                .subscribe(response -> {
//                    //todo on response
//                });
    }

    public JsonNode doRestPOST(final String url, Map<String, String> headers, JsonNode body) {

        LOGGER.info("Enter doRestGET()" + CLASS_NAME);
        LOGGER.debug("{}, {}", url, JSONUtil.toJson(body));

        return webClient.post()
                .uri(url)
                .headers(httpHeaders -> {
                    //set headers
                    if (headers != null) {
                        for (Map.Entry<String, String> p : headers.entrySet()) {
                            httpHeaders.set(p.getKey(),
                                    p.getValue());
                        }
                    }
                })
                .body(BodyInserters
                        .fromValue(body))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(100)))
                .doOnError(error -> {
                    throw new ExException(String.format("%s", error.getMessage()));
                })
                .block();
    }
}
