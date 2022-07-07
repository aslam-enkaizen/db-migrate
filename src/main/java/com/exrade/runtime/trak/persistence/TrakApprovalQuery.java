package com.exrade.runtime.trak.persistence;

import com.exrade.models.trak.TrakApproval;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;

/**
 * @author Rhidoy
 * @created 13/10/2021
 * @package com.exrade.runtime.trak.persistence
 */
public class TrakApprovalQuery extends OrientSqlBuilder {
    @Override
    protected String buildQuery(QueryFilters iFilters) {
        StringBuilder query = new StringBuilder("select from " + TrakApproval.class.getSimpleName() + " where 1 = 1 ");

        if (iFilters.isNotNull(RestParameters.TrakApprovalFilters.TRAK_RESPONSE_UUID)) {
            query.append(andEq(RestParameters.TrakApprovalFilters.TRAK_RESPONSE_UUID,
                    iFilters.get(RestParameters.TrakApprovalFilters.TRAK_RESPONSE_UUID)));
        }
        if (iFilters.isNotNull(RestParameters.TrakApprovalFilters.TRAK_UUID)) {
            query.append(andEq(RestParameters.TrakApprovalFilters.TRAK_UUID,
                    iFilters.get(RestParameters.TrakApprovalFilters.TRAK_UUID)));
        }
//        if (iFilters.isNotNull(RestParameters.TrakApprovalFilters.TRAK_APPROVER_UUID)) {
//            query.append(andEq(RestParameters.TrakApprovalFilters.TRAK_APPROVER_UUID,
//                    iFilters.get(RestParameters.TrakApprovalFilters.TRAK_APPROVER_UUID)));
//        }

        return query.toString();
    }
}
