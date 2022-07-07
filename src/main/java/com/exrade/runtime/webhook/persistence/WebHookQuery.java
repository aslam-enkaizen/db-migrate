package com.exrade.runtime.webhook.persistence;

import com.exrade.models.webhook.WebHook;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;

public class WebHookQuery extends OrientSqlBuilder {

    @Override
    protected String buildQuery(QueryFilters iFilters) {
        String nquery = "select from " + WebHook.class.getSimpleName() + " where 1 = 1 ";

        if (iFilters.isNotNull(QueryParameters.UUID)) {
            nquery += andEq(QueryParameters.UUID, iFilters.get(QueryParameters.UUID));
        }
        
        if (iFilters.isNotNull(RestParameters.WebHookFields.PROFILE)) {
            nquery += andEq(RestParameters.WebHookFields.PROFILE + "." + QueryParameters.UUID, iFilters.get(RestParameters.WebHookFields.PROFILE));
        }

        return nquery;
    }
}
