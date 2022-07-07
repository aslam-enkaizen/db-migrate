package com.exrade.models.integration;

import com.exrade.models.userprofile.Negotiator;
import com.exrade.models.userprofile.Profile;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import java.util.HashMap;
import java.util.Map;

public class IntegrationSetting extends BaseEntityUUIDTimeStampable {

	private IntegrationServiceType integrationServiceType;
	private Profile profile;
	private Map<String, Object> settings = new HashMap<>();
	private boolean active = false;
	private Negotiator createddBy;
	private Negotiator updatedBy;

	public IntegrationServiceType getIntegrationServiceType() {
		return integrationServiceType;
	}

	public void setIntegrationServiceType(IntegrationServiceType integrationServiceType) {
		this.integrationServiceType = integrationServiceType;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Map<String, Object> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, Object> settings) {
		this.settings = settings;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Negotiator getCreateddBy() {
		return createddBy;
	}

	public void setCreateddBy(Negotiator createddBy) {
		this.createddBy = createddBy;
	}

	public Negotiator getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Negotiator updatedBy) {
		this.updatedBy = updatedBy;
	}
}
