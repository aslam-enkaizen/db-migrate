package com.exrade.runtime.trak.persistence;

import com.exrade.models.trak.TrakResponse;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;

/**
 * @author Rhidoy
 * @created 13/10/2021
 * @package com.exrade.runtime.trak.persistence
 */
public class TrakResponseQuery extends OrientSqlBuilder {

    @Override
    protected String buildQuery(QueryFilters iFilters) {
        StringBuilder query = new StringBuilder("select from " + TrakResponse.class.getSimpleName() + " where 1 = 1 ");

        if (iFilters.isNotNull(RestParameters.TrakResponseFilters.TRAK_UUID)) {
            query.append(andEq(RestParameters.TrakResponseFilters.TRAK_UUID,
                    iFilters.get(RestParameters.TrakResponseFilters.TRAK_UUID)));
        }

//        if (iFilters.isNotNull(RestParameters.TrakResponseFilters.TRAK_CREATOR_UUID)) {
//            query.append(and(or()));
//            query.append(andEq(RestParameters.TrakResponseFilters.TRAK_CREATOR_UUID,
//                    iFilters.get(RestParameters.TrakResponseFilters.TRAK_CREATOR_UUID)));
//        }
//
//        if (iFilters.isNotNull(RestParameters.TrakResponseFilters.TRAK_CREATOR_UUID)) {
//            query.append(andEq(RestParameters.TrakResponseFilters.TRAK_APPROVER_UUID,
//                    iFilters.get(RestParameters.TrakResponseFilters.TRAK_APPROVER_UUID)));
//        }
//
//        if (iFilters.isNotNull(RestParameters.TrakResponseFilters.TRAK_ASSIGNEE_UUID)) {
//            query.append(andEq(RestParameters.TrakResponseFilters.TRAK_ASSIGNEE_UUID,
//                    iFilters.get(RestParameters.TrakResponseFilters.TRAK_ASSIGNEE_UUID)));
//        }
        return query.toString();
    }
}
