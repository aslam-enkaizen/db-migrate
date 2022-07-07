package com.exrade.runtime.contract.persistence;

import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.QueryFilters;

import java.text.MessageFormat;

public class ContractSearchSummaryQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String profileUUID = iFilters.getOrDefault("profile.uuid", "").toString();
		String negotiatorQuery = "";
		if(!iFilters.isNullOrEmpty("negotiator.uuid"))
			negotiatorQuery = MessageFormat.format("and negotiator.uuid = ''{0}''", iFilters.getOrDefault("negotiator.uuid", "").toString());
		
		
		if(!iFilters.isNullOrEmpty(QueryParameters.FIELD)){
			if(iFilters.get(QueryParameters.FIELD).equals("tags")) {
				return MessageFormat.format("select value, count(*) from (select expand(tags) from (select expand(contractingParties) from Contract where 1 = 1  "
						+ "and contractingParties contains (members contains (negotiator.profile.uuid = ''{0}'' {1}) )) "
						+ "where members contains (negotiator.profile.uuid = ''{0}'' {1})) group by value order by count desc", profileUUID, negotiatorQuery);
			}
			else if(iFilters.get(QueryParameters.FIELD).equals("category")) {
				return MessageFormat.format("select category as value, count(*) from (select expand(contractingParties) from Contract where 1 = 1  "
						+ "and contractingParties contains (members contains (negotiator.profile.uuid = ''{0}'' {1}) )) "
						+ "where members contains (negotiator.profile.uuid = ''{0}'' {1}) group by category order by count desc", profileUUID, negotiatorQuery);
			}
			else if(iFilters.get(QueryParameters.FIELD).equals("otherParty")) {
				return MessageFormat.format("SELECT from (SELECT EXPAND( $c ) LET $a = ( select organization as value, count(*) from (select expand(value) from ("
						+ "select expand(contractingParties.members.contact) from Contract where 1 = 1  and contractingParties contains ("
						+ "members contains (negotiator.profile.uuid = ''{0}'' {1}) ))) group by organization ), "
						+ "$b = ( select name as value, count(*) from (select expand(contractingParties.profile) from Contract where 1 = 1  "
						+ "and contractingParties contains (members contains (negotiator.profile.uuid = ''{0}'' {1}) )) "
						+ "where uuid <> ''{0}'' and name is not null group by name ), $c = UNIONALL( $a, $b ))", profileUUID, negotiatorQuery);
			}
			
		}
		
		return null;
	}
}
