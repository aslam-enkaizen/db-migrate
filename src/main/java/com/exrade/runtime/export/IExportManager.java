package com.exrade.runtime.export;

import com.exrade.models.export.Export;

import java.util.List;
import java.util.Map;

public interface IExportManager {

    Export read(String exportUUID);

    List<Export> list(Map<String, String> iFilters);

    Export create(Export export);

    Export update(String uuid, Export export);

    void delete(String exportUUID);
}
