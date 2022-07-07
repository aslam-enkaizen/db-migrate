package com.exrade.runtime.export;

import com.exrade.models.export.Export;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthorizationException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.rest.RestParameters;

import java.util.List;
import java.util.Map;

public class ExportManager implements IExportManager {

    PersistentManager persistentManager = new PersistentManager();

    @Override
    public Export read(String exportUUID) {
        Export result = persistentManager.readObjectByUUID(Export.class, exportUUID);
        if (result == null)
            throw new ExNotFoundException("Export data not found for Id " + exportUUID);
        return result;
    }

    @Override
    public List<Export> list(Map<String, String> iFilters) {

        QueryFilters filters = QueryFilters.create(iFilters);
        filters.putIfNotEmpty(RestParameters.UUID, iFilters.get(RestParameters.UUID));
        filters.putIfNotEmpty(RestParameters.KEYWORDS, iFilters.get(RestParameters.KEYWORDS));
        filters.putIfNotEmpty(OrientSqlBuilder.QueryParameters.SORT, iFilters.get(OrientSqlBuilder.QueryParameters.SORT));

        return persistentManager.listObjects(new ExportQuery(), filters);
    }

    @Override
    public Export create(Export export) {
        Export result= persistentManager.create(export);
        //todo process the data in service

        return result;
    }

    @Override
    public Export update(String uuid, Export export) {
        //first checking object exist or not
        Export old=read(uuid);
        bindExportObject(old, export);
        return persistentManager.update(old);
    }

    @Override
    public void delete(String exportUUID) {
        //checking object exist or not
        read(exportUUID);
        if (!Security.isProfileAdministrator())
            throw new ExAuthorizationException(ErrorKeys.NOT_AUTHORIZED);
        persistentManager.delete(exportUUID);
    }

    private void bindExportObject(Export old, Export current){
        old.setProfile(current.getProfile());
        old.setRequestor(current.getRequestor());
        old.setStatus(current.getStatus());
        old.setObjectType(current.getObjectType());
        old.setExportedFileUrl(current.getExportedFileUrl());
        old.setIncludeFiles(current.getIncludeFiles());
        old.setQueryFilters(current.getQueryFilters());
    }
}
