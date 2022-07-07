package com.exrade.runtime.imports;

import com.exrade.models.imports.Import;

import java.util.List;
import java.util.Map;

public interface IImportManager {

    Import read(String importUUID);

    List<Import> list(Map<String, String> iFilters);

    Import create(Import data);

    Import update(String uuid, Import data);

    Import update(Import data);

    void delete(String importUUID);

    void deleteOldImports();
}
