package com.exrade.runtime.imports;

import com.exrade.models.imports.Import;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class ImportQuery extends OrientSqlBuilder {
    @Override
    protected String buildQuery(QueryFilters iFilters) {
        StringBuilder query = new StringBuilder("select from " + Import.class.getSimpleName() + " where 1 = 1 ");

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

        if (!iFilters.isNullOrEmpty(RestParameters.ImportRequestFields.IMPORT_STATUS))
            query.append(andEq(RestParameters.ImportRequestFields.IMPORT_STATUS,
                    iFilters.get(RestParameters.ImportRequestFields.IMPORT_STATUS)));

        if (!iFilters.isNullOrEmpty(RestParameters.ImportRequestFields.USER_UUID))
            query.append(andEq(RestParameters.ImportRequestFields.USER_UUID,
                    iFilters.get(RestParameters.ImportRequestFields.USER_UUID)));

        if (!iFilters.isNullOrEmpty(RestParameters.ImportRequestFields.USER_PROFILE_UUID))
            query.append(andEq(RestParameters.ImportRequestFields.USER_PROFILE_UUID,
                    iFilters.get(RestParameters.ImportRequestFields.USER_PROFILE_UUID)));

        if (iFilters.isNotNull(RestParameters.CREATION_DATE))
            query.append(and(condition(RestParameters.CREATION_DATE,
                    iFilters.get(RestParameters.CREATION_DATE),
                    Operator.LTEQ)));

        return query.toString();
    }
}
