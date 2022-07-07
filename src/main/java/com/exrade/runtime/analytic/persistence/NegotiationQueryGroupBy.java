package com.exrade.runtime.analytic.persistence;

import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.negotiation.persistence.NegotiationQueryProfiled;

public class NegotiationQueryGroupBy extends NegotiationQueryProfiled {
	@Override
	public String buildQuery(QueryFilters iFilters) {
		// select record_date.format('YYYY') as mo, record_date.format('MM') as da from V group by da, mo
		// select record_date.format('YYYY') as mo, record_date.format('MM') as da group by da, mo from V
		// select md, count(*) from (select publicationDate.format('yyyy-MM-dd') as md, uuid from negotiation) group by md
		// select bundle.name, count(*) from negotiation group by bundle.name
		// select alias, count(*) from (select filed/field.format as alias from negotiation) group by alias
		String query = null;
		if(!iFilters.isNullOrEmpty(QueryParameters.FIELD)){
			String innerQuery = "select " + iFilters.get(QueryParameters.FIELD) + " as " + QueryAlias.GROUP_BY_FIELD + " from (" + super.buildQuery(iFilters) + ")";
			query = "select " + QueryAlias.GROUP_BY_FIELD + ", count(*) from ( " + innerQuery + " ) group by " + QueryAlias.GROUP_BY_FIELD + " order by grp";
		}
		
		return query;
	}
}
