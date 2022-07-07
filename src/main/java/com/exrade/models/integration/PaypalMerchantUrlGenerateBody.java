package com.exrade.models.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rhidoy
 * @created 26/05/2022
 * @package com.exrade.models.integration
 * <p>
 * This class will use as body to make Paypal Url Generation call
 */
public class PaypalMerchantUrlGenerateBody {
    private final String tracking_id;
    private final String preferred_language_code;
    private final List<Map<String, Object>> operations = new ArrayList<>();
    private final List<String> products = new ArrayList<>();
    private final List<Map<String, Object>> legal_consents = new ArrayList<>();
    private final Map<String, Object> partner_config_override = new HashMap<>();

    public PaypalMerchantUrlGenerateBody(String tracking_id, String preferred_language_code) {
        this.tracking_id = tracking_id;
        this.preferred_language_code = preferred_language_code;

        //todo update those
        partner_config_override.put("partner_logo_url", "https://www.paypalobjects.com/webstatic/mktg/logo/pp_cc_mark_111x69.jpg");
        partner_config_override.put("return_url", "http://localhost:8000/profile-integration");
        partner_config_override.put("return_url_description", "the url to return the merchant after the paypal onboarding process.");
        partner_config_override.put("action_renewal_url", "http://localhost:8000/profile-integration");
        partner_config_override.put("show_add_credit_card", true);

        Map<String, Object> operation = new HashMap<>();
        operation.put("operation", "API_INTEGRATION");

        Map<String, Object> api_integration_preference = new HashMap<>();
        api_integration_preference.put("rest_api_integration", "integration_method");
        api_integration_preference.put("integration_type", "THIRD_PARTY");

        Map<String, Object> third_party_details = new HashMap<>();
        third_party_details.put("features", new String[]{"PAYMENT", "REFUND"});
        api_integration_preference.put("third_party_details", third_party_details);

        operation.put("api_integration_preference", api_integration_preference);
        operations.add(operation);

        products.add("EXPRESS_CHECKOUT");
        Map<String, Object> legal_consent = new HashMap<>();
        legal_consent.put("type", "SHARE_DATA_CONSENT");
        legal_consent.put("granted", true);
        legal_consents.add(legal_consent);
    }

    public String getTracking_id() {
        return tracking_id;
    }

    public List<Map<String, Object>> getOperations() {
        return operations;
    }

    public List<String> getProducts() {
        return products;
    }

    public List<Map<String, Object>> getLegal_consents() {
        return legal_consents;
    }

    public String getPreferred_language_code() {
        return preferred_language_code;
    }

    public Map<String, Object> getPartner_config_override() {
        return partner_config_override;
    }
}
