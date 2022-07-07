package com.exrade.runtime.trak.persistence;

import com.exrade.models.trak.Trak;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Rhidoy
 * @created 13/10/2021
 * @package com.exrade.runtime.trak.persistence
 */
public class TrakQuery extends OrientSqlBuilder {

    @Override
    protected String buildQuery(QueryFilters iFilters) {
        StringBuilder query = new StringBuilder("select from " + Trak.class.getSimpleName() + " where 1 = 1 ");

        if (iFilters.isNotNull(QueryParameters.UUID)) {
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

        if (!iFilters.isNullOrEmpty(RestParameters.TrakFields.CREATOR_UUID))
            query.append(andEq(RestParameters.TrakFields.CREATOR_ID,
                    iFilters.get(RestParameters.TrakFields.CREATOR_UUID)));
        if (!iFilters.isNullOrEmpty(RestParameters.TrakFields.ASSIGNEE_UUID))
            query.append(andEq(RestParameters.TrakFields.ASSIGNEE_ID,
                    iFilters.get(RestParameters.TrakFields.ASSIGNEE_UUID)));
        if (!iFilters.isNullOrEmpty(RestParameters.TrakFields.CONTRACT_UUID))
            query.append(andEq(RestParameters.TrakFields.CONTRACT_ID,
                    iFilters.get(RestParameters.TrakFields.CONTRACT_UUID)));

        if (iFilters.isNotNull(RestParameters.TrakFields.START_DATE))
            query.append(and(condition(RestParameters.TrakFields.START_DATE,
                    iFilters.get(RestParameters.TrakFields.START_DATE),
                    Operator.GTEQ)));
        if (iFilters.isNotNull(RestParameters.TrakFields.DUE_DATE))
            query.append(and(condition(RestParameters.TrakFields.DUE_DATE,
                    iFilters.get(RestParameters.TrakFields.DUE_DATE),
                    Operator.LTEQ)));
        
        if (iFilters.isNotNull(RestParameters.TrakFields.EXTERNAL_ID)){
			query.append(andEq(RestParameters.TrakFields.EXTERNAL_ID, 
					iFilters.get(RestParameters.TrakFields.EXTERNAL_ID)));
		}


        if (!iFilters.isNullOrEmpty(RestParameters.TrakFields.STATUS))
            query.append(andEq(RestParameters.TrakFields.STATUS,
                    iFilters.get(RestParameters.TrakFields.STATUS)));

        if (!iFilters.isNullOrEmpty(RestParameters.TrakFields.TYPE))
            query.append(andEq(RestParameters.TrakFields.TYPE,
                    iFilters.get(RestParameters.TrakFields.TYPE)));
        
        if(iFilters.containsKey(RestParameters.TrakFields.PARENT_UUID)) {
	        if (iFilters.isNullOrEmpty(RestParameters.TrakFields.PARENT_UUID))
	            query.append(andEq(RestParameters.TrakFields.PARENT, null));
	        else query.append(andEq(RestParameters.TrakFields.PARENT_ID,
	                iFilters.get(RestParameters.TrakFields.PARENT_UUID)));
        }
        return query.toString();
    }
}
