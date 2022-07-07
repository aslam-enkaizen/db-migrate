package com.exrade.models.webhook;

import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Profile;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import java.util.ArrayList;
import java.util.List;

public class WebHook extends BaseEntityUUIDTimeStampable {

    private Profile profile;
    private Membership createdBy;
    private Membership updatedBy;
    private Boolean enabled;
    private String url;
    private List<String> events = new ArrayList<>(); // changed type from NotificationType to String as Orientdb caanot store list of enum


    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public Membership getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Membership createdBy) {
        this.createdBy = createdBy;
    }

	public Membership getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Membership updatedBy) {
		this.updatedBy = updatedBy;
	}
}
