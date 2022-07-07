package com.exrade.util;

import com.exrade.core.ExLogger;
import com.exrade.runtime.conf.ExConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class PaypalUtil {
    private static final String PAYPAL_CLIENT_ID = ExConfiguration.getStringProperty("paypal.CLIENT_ID");
    private static final String PAYPAL_SECRET = ExConfiguration.getStringProperty("paypal.SECRET");
    private static final String PAYPAL_ACCESS_TOKEN_URL = ExConfiguration.getStringProperty("paypal.ACCESS_TOKEN_URL");
    private static final String PAYPAL_MERCHANT_SIGN_UP_URL = ExConfiguration.getStringProperty("paypal.MERCHANT_SIGN_UP_URL");
    private static final String LOGO_URL = ExConfiguration.getStringProperty("paypal.LOGO_URL");
    private static final String RETURN_BASE_URL = ExConfiguration.getStringProperty("paypal.RETURN_BASE_URL");
    private static final String RETURN_PATH = ExConfiguration.getStringProperty("paypal.RETURN_PATH");

    public static String getPaypalMerchantURl(String lang) {
        //get access token
        String token = getAccessToken();
        //do url request
        if (token != null) {
            try {
                PaypalMerchantPostURL url = WebClient.builder()
                        .baseUrl(PAYPAL_MERCHANT_SIGN_UP_URL)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build()
                        .post()
//                    .accept(MediaType.APPLICATION_JSON )
                        .headers(h -> {
                            h.setBearerAuth(token);
                        })
                        .bodyValue("{\n" +
                                "    \"tracking_id\": \"" + ContextHelper.getMembershipUUID() + "\",\n" +
                                "    \"preferred_language_code\": \"" + lang + "\",\n" +
                                "    \"partner_config_override\": {\n" +
                                "        \"partner_logo_url\": \"" + LOGO_URL + "\",\n" +
                                "        \"return_url\": \"" + RETURN_BASE_URL + lang + RETURN_PATH + "\",\n" +
                                "        \"return_url_description\": \"the url to return the merchant after the paypal onboarding process.\",\n" +
//                                "        \"action_renewal_url\": \"https://testenterprises.com/renew-exprired-url\",\n" +
                                "        \"show_add_credit_card\": true\n" +
                                "    },\n" +
                                "    \"operations\": [\n" +
                                "      {\n" +
                                "        \"operation\": \"API_INTEGRATION\",\n" +
                                "        \"api_integration_preference\": {\n" +
                                "          \"rest_api_integration\": {\n" +
                                "            \"integration_method\": \"PAYPAL\",\n" +
                                "            \"integration_type\": \"THIRD_PARTY\",\n" +
                                "            \"third_party_details\": {\n" +
                                "              \"features\": [\n" +
                                "                \"PAYMENT\",\n" +
                                "                \"REFUND\"\n" +
                                "             ]\n" +
                                "            }\n" +
                                "          }\n" +
                                "        }\n" +
                                "      }\n" +
                                "    ],\n" +
                                "    \"products\": [\n" +
                                "      \"EXPRESS_CHECKOUT\"\n" +
                                "    ],\n" +
                                "    \"legal_consents\": [\n" +
                                "      {\n" +
                                "        \"type\": \"SHARE_DATA_CONSENT\",\n" +
                                "        \"granted\": true\n" +
                                "      }\n" +
                                "    ]\n" +
                                "}")
                        .retrieve()
                        .bodyToMono(PaypalMerchantPostURL.class)
                        .block();

                if (url != null && url.getLinks() != null)
                    for (PaypalMerchantPostURL.URLResponse link : url.getLinks()) {
                        if (link.getRel().equals("action_url"))
                            return link.getHref();
                    }
            } catch (Exception e) {
                ExLogger.get().warn("Failed to get url for paypal merchant", e);
            }


//            try {
//                Map<String, String> header = new HashMap<>();
//                header.put("Content-Type", "application/json");
//                header.put("Authorization", String.format("Bearer %s", token));
//                JsonNode response = RESTUtil.doRestPOST(
//                        PAYPAL_MERCHANT_SIGN_UP_URL,
//                        header,
//                        JSONUtil.toJsonNode(new PaypalMerchantUrlGenerateBody(ContextHelper.getMembershipUUID(), lang)));
//                JsonNode links = response.get("links");
//                for (JsonNode link : links) {
//                    String redirectUrl = link.path("href").asText(null);
//                    String rel = link.path("rel").asText(null);
//                    link.path("method").asText(null);
//                    link.path("description").asText(null);
//                    if (rel != null && rel.equals("action_url")) return redirectUrl;
//                }
//            } catch (Exception ex) {
//                ExLogger.get().warn("Failed to get url", ex);
//            }
        }

        return null;
    }

    public static String getAccessToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");

        try {
            TokenResponse tokenResponse = WebClient.builder()
                    .baseUrl(PAYPAL_ACCESS_TOKEN_URL)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .build()
                    .post()
                    .headers(h -> {
                        h.set("Accept", "application/json");
                        h.set("Accept-Language", "en_US");
                        h.setBasicAuth(PAYPAL_CLIENT_ID, PAYPAL_SECRET);
                    })
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(TokenResponse.class)
                    .block();
            if (tokenResponse != null && tokenResponse.access_token != null)
                return tokenResponse.access_token;
        } catch (Exception e) {
            ExLogger.get().warn("Failed to get token for paypal merchant", e);
        }

//        try {
//            Map<String, String> header = new HashMap<>();
//            header.put("Accept", "application/json");
//            header.put("Content-Type", "application/x-www-form-urlencoded");
//            header.put("Accept-Language", "en_US");
//            header.put("Authorization", String.format("Basic %s", RESTUtil
//                    .getBase64EncodedCredential(PAYPAL_CLIENT_ID, PAYPAL_SECRET)));
//
//            Map<String, String> body = new HashMap<>();
//            body.put("grant_type", "client_credentials");
//            JsonNode response = RESTUtil.doRestPOST(
//                    PAYPAL_ACCESS_TOKEN_URL,
//                    header,
//                    JSONUtil.toJsonNode(body));
//            //scope access_token token_type app_id expires_in (int) nonce
//            token = response.path("access_token").asText(null);
//        } catch (Exception ex) {
//            ExLogger.get().warn("Failed to get access token", ex);
//        }
        return null;
    }

    private static class TokenResponse {
        private String access_token;
        private String token_type;
        private String scope;
        private String app_id;
        private Long expires_in;
        private String nonce;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getToken_type() {
            return token_type;
        }

        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public Long getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(Long expires_in) {
            this.expires_in = expires_in;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }
    }

    private static class PaypalMerchantPostURL {

        private URLResponse[] links;

        public URLResponse[] getLinks() {
            return links;
        }

        public void setLinks(URLResponse[] links) {
            this.links = links;
        }

        private static class URLResponse {
            private String href;
            private String rel;
            private String method;
            private String description;

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }

            public String getRel() {
                return rel;
            }

            public void setRel(String rel) {
                this.rel = rel;
            }

            public String getMethod() {
                return method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }

}
