package com.exrade.models.imports;

import com.exrade.models.activity.ObjectType;
import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Profile;
import com.exrade.platform.persistence.BaseEntityUUIDTimeStampable;

import java.util.HashMap;
import java.util.Map;

public class Import extends BaseEntityUUIDTimeStampable {

    private Profile profile;
    private Membership requestor;
    private ImportStatus status;
    private String importFile;
    private ObjectType objectType;
    private String attachedFile;
    private Map<String, String> propertyMap = new HashMap<>();
    private String columnSeparator = ",";
    private Map<String, String> results = new HashMap<>();

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

    public ImportStatus getStatus() {
        return status;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public String getImportFile() {
        return importFile;
    }

    public void setImportFile(String importFile) {
        this.importFile = importFile;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public String getAttachedFile() {
        return attachedFile;
    }

    public void setAttachedFile(String attachedFile) {
        this.attachedFile = attachedFile;
    }

    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public String getColumnSeparator() {
        return columnSeparator;
    }

    public void setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
    }

    public Map<String, String> getResults() {
        return results;
    }

    public void setResults(Map<String, String> results) {
        this.results = results;
    }

    public void updateResult(String key, String value){
        this.results.put(key, value);
    }
}
