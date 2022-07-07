package com.exrade.runtime.export;

import com.exrade.models.export.Export;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class ExportQuery extends OrientSqlBuilder {
    @Override
    protected String buildQuery(QueryFilters iFilters) {
        StringBuilder query = new StringBuilder("select from " + Export.class.getSimpleName() + " where 1 = 1 ");

        if (!iFilters.isNullOrEmpty(QueryParameters.UUID)) {
            query.append(andEq(QueryParameters.UUID,
                    iFilters.get(QueryParameters.UUID)));
        }

        if (!iFilters.isNullOrEmpty(RestParameters.KEYWORDS)) {
            List<String> keywords = Lists
                    .newArrayList(Splitter.on(" ")
                            .trimResults()
                            .omitEmptyStrings()
                            .split((String) iFilters.get(RestParameters.KEYWORDS))
                    );
            for (String keyword : keywords) {
                query.append(and(contains(QueryKeywords.ANY + ".toLowerCase()",
                        keyword.toLowerCase())));
            }
        }

        if (!iFilters.isNullOrEmpty(RestParameters.ExportRequestFields.EXPORT_STATUS))
            query.append(andEq(RestParameters.ExportRequestFields.EXPORT_STATUS,
                    iFilters.get(RestParameters.ExportRequestFields.EXPORT_STATUS)));

        return query.toString();
    }
}
