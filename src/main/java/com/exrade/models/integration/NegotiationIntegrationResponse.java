package com.exrade.models.integration;

public class NegotiationIntegrationResponse {
    private String serviceType;
    private Boolean integrated;

    public NegotiationIntegrationResponse() {
    }

    public NegotiationIntegrationResponse(String serviceType, Boolean integrated) {
        this.serviceType = serviceType;
        this.integrated = integrated;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Boolean getIntegrated() {
        return integrated;
    }

    public void setIntegrated(Boolean integrated) {
        this.integrated = integrated;
    }
}
