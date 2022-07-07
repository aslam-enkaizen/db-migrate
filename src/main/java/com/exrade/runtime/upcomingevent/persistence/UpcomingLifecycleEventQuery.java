package com.exrade.runtime.upcomingevent.persistence;

import com.exrade.models.upcomingevent.UpcomingLifecycleEvent;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;

import static com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters.UUID;
import static com.exrade.runtime.rest.RestParameters.UpcomingLifecycleEventFields.*;

/**
 * @author Rhidoy
 * @created 29/10/2021
 * @package com.exrade.runtime.upcomingevent.persistence
 */
public class UpcomingLifecycleEventQuery extends OrientSqlBuilder {

    @Override
    protected String buildQuery(QueryFilters iFilters) {
        StringBuilder query = new StringBuilder("select from " +
                UpcomingLifecycleEvent.
                        class.getSimpleName() +
                " where 1 = 1 ");

        if (iFilters.isNotNull(UUID)) {
            query.append(andEq(UUID,
                    iFilters.get(UUID)));
        }

        if (!iFilters.isNullOrEmpty(EVENT_UUID))
            query.append(andEq(EVENT_UUID,
                    iFilters.get(EVENT_UUID)));

        if (!iFilters.isNullOrEmpty(EVENT_TYPE))
            query.append(andEq(EVENT_TYPE,
                    iFilters.get(EVENT_TYPE)));

        if (!iFilters.isNullOrEmpty(DATE))
            query.append(andEq(DATE,
                    iFilters.get(DATE)));

        //getting data from list
        if (!iFilters.isNullOrEmpty(MEMBERS))
            query.append("  and (" + MEMBERS + " contains (uuid = '")
                    .append(iFilters.get(MEMBERS))
                    .append("'))");
        return query.toString();
    }
}
