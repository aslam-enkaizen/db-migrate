package com.exrade.runtime.imports;

import com.exrade.models.imports.Import;
import com.exrade.models.imports.ImportStatus;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExAuthorizationException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.filemanagement.FileStorageProvider;
import com.exrade.runtime.filemanagement.IFileStorageController;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.timer.ImportsScheduler;
import com.exrade.util.ContextHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ImportManager implements IImportManager {

    private final PersistentManager persistentManager = new PersistentManager();
    private final IFileStorageController fileManager = FileStorageProvider.getFileStorageController();

    @Override
    public Import read(String importUUID) {
        Import result = persistentManager.readObjectByUUID(Import.class, importUUID);
        if (result == null)
            throw new ExNotFoundException("Import data not found for Id " + importUUID);
        return result;
    }

    @Override
    public List<Import> list(Map<String, String> iFilters) {
        //checking permissiom
        Security.hasImportPermission();

        QueryFilters filters = QueryFilters.create(iFilters);
        filters.putIfNotEmpty(RestParameters.UUID, iFilters.get(RestParameters.UUID));
        if (Security.isPlatformAdministrator()) {
            filters.putIfNotEmpty(RestParameters.ImportRequestFields.USER_PROFILE_UUID, iFilters.get(RestParameters.ImportRequestFields.USER_PROFILE_UUID));
            filters.putIfNotEmpty(RestParameters.ImportRequestFields.USER_UUID, iFilters.get(RestParameters.ImportRequestFields.USER_UUID));
        }
        if (Security.isProfileAdministrator())
            filters.put(RestParameters.ImportRequestFields.USER_PROFILE_UUID, ContextHelper.getMembership().getProfile().getUuid());

        filters.putIfNotEmpty(RestParameters.ImportRequestFields.IMPORT_STATUS, iFilters.get(RestParameters.ImportRequestFields.IMPORT_STATUS));
        filters.putIfNotEmpty(RestParameters.CREATION_DATE, iFilters.get(RestParameters.CREATION_DATE));
        filters.putIfNotEmpty(RestParameters.KEYWORDS, iFilters.get(RestParameters.KEYWORDS));
        filters.putIfNotEmpty(OrientSqlBuilder.QueryParameters.SORT, iFilters.get(OrientSqlBuilder.QueryParameters.SORT));

        return persistentManager.listObjects(new ImportQuery(), filters);
    }

    @Override
    public Import create(Import data) {
        //checking permission
        Security.hasImportPermission();
        Import result = persistentManager.create(data);
        ImportsScheduler.schedule(result.getUuid());
        return result;
    }

    @Override
    public Import update(String uuid, Import data) {
        //checking permission
        Security.hasImportPermission();
        //first checking object exist or not
        Import old = read(uuid);
        //checking the job is already in progress or not
        if (old.getStatus() == ImportStatus.IN_PROGRESS)
            throw new ExAuthorizationException(ErrorKeys.IMPORT_ALREADY_IN_PROGRESS);
        bindImportObject(old, data);
        return persistentManager.update(old);
    }

    @Override
    public Import update(Import data) {
        return persistentManager.update(data);
    }

    @Override
    public void delete(String importUUID) {
        Security.hasImportPermission();
        //checking object exist or not
        read(importUUID);
        persistentManager.delete(importUUID);
    }

    @Override
    public void deleteOldImports() {
        //first getting 30day old data which
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -30);
        QueryFilters filters = new QueryFilters();

        filters.put(RestParameters.CREATION_DATE, c.getTime().getTime() + "");

        for (Object importData : persistentManager.listObjects(new ImportQuery(), filters)) {
            //now deleting all the respective file's from database
            Import data = (Import) importData;
            //first delete import file
            fileManager.deleteFile(data.getImportFile());
            if (!StringUtils.isBlank(data.getAttachedFile()))
                fileManager.deleteFile(data.getAttachedFile());
//            persistentManager.delete(data.getId());
        }
    }

    private void bindImportObject(Import old, Import current) {
//        old.setProfile(current.getProfile());
//        old.setRequestor(current.getRequestor());
        old.setStatus(current.getStatus());
        old.setImportFile(current.getImportFile());
        old.setObjectType(current.getObjectType());
        old.setAttachedFile(current.getAttachedFile());
        old.setPropertyMap(current.getPropertyMap());
//        old.setResults(current.getResults());
    }
}
