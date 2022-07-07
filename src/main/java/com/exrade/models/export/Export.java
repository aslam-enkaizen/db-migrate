package com.exrade.models.export;

import com.exrade.models.activity.ObjectType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Profile;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import java.util.HashMap;
import java.util.Map;

public class Export extends BaseEntityUUIDTimeStampable {

    private Profile profile;
    private Membership requestor;
    private ExportStatus status;
    private ObjectType objectType;

    private String exportedFileUrl;
    private Boolean includeFiles;
    private Map<String, Object> queryFilters = new HashMap<>();

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Membership getRequestor() {
        return requestor;
    }

    public void setRequestor(Membership requestor) {
        this.requestor = requestor;
    }

    public ExportStatus getStatus() {
        return status;
    }

    public void setStatus(ExportStatus status) {
        this.status = status;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public String getExportedFileUrl() {
        return exportedFileUrl;
    }

    public void setExportedFileUrl(String exportedFileUrl) {
        this.exportedFileUrl = exportedFileUrl;
    }

    public Boolean getIncludeFiles() {
        return includeFiles;
    }

    public void setIncludeFiles(Boolean includeFiles) {
        this.includeFiles = includeFiles;
    }

    public Map<String, Object> getQueryFilters() {
        return queryFilters;
    }

    public void setQueryFilters(Map<String, Object> queryFilters) {
        this.queryFilters = queryFilters;
    }
}
