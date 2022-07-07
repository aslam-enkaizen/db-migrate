package com.exrade.runtime.contract.persistence;

import com.exrade.models.contract.Contract;
import com.exrade.models.contract.ContractType;
import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.PlainSql;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.ContractFields;
import com.exrade.runtime.rest.RestParameters.ContractFilters;
import com.exrade.runtime.rest.RestParameters.ContractLifecycleSettingFields;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.text.MessageFormat;
import java.util.List;

public class ContractQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {
		String query = "select from " + Contract.class.getSimpleName()+ " where 1 = 1 ";

		query += addEqFilter(iFilters, QueryParameters.UUID);

		if (!iFilters.isNullOrEmpty("profile.uuid")){
			String partySubQuery = "";
			if(!iFilters.isNullOrEmpty("negotiator.uuid"))
				partySubQuery = MessageFormat.format("members contains (negotiator.profile.uuid = ''{0}'' and negotiator.uuid = ''{1}'')", iFilters.get("profile.uuid"), iFilters.get("negotiator.uuid"));
			else
				partySubQuery = MessageFormat.format("members contains (negotiator.profile.uuid = ''{0}'')", iFilters.get("profile.uuid"));

			if(!iFilters.isNullOrEmpty(ContractFields.CATEGORY))
				partySubQuery += andEq(ContractFields.CATEGORY, iFilters.get(ContractFields.CATEGORY));

			if(!iFilters.isNullOrEmpty(ContractFields.RISK))
				partySubQuery += andEq(ContractFields.RISK, iFilters.get(ContractFields.RISK));

			if(!iFilters.isNullOrEmpty(ContractFields.REFERENCE_ID))
				partySubQuery += andEq(ContractFields.REFERENCE_ID, iFilters.get(ContractFields.REFERENCE_ID));

			if(!iFilters.isNullOrEmpty(ContractFields.TAGS))
				partySubQuery += andIn(ContractFields.TAGS, iFilters.get(ContractFields.TAGS).toString().toLowerCase());

			if (!iFilters.containsKey(QueryParameters.UUID)){
				if (!iFilters.containsKey(ContractFilters.INCLUDE_ARCHIVED) || !iFilters.isTrue(ContractFilters.INCLUDE_ARCHIVED)){
					partySubQuery += andEq(ContractFields.ARCHIVED, false);
				}
			}

			query += and(MessageFormat.format("contractingParties contains ({0})", partySubQuery));

			if(!iFilters.isNullOrEmpty(ContractFilters.OTHER_PARTY))
				query += and(MessageFormat.format("contractingParties contains (members contains (negotiator.profile.name = ''{0}'' or contact.organization = ''{0}''))", iFilters.get(ContractFilters.OTHER_PARTY)));

			if(!iFilters.isNullOrEmpty(ContractFields.OTHER_PARTY_MEMBERS))
				query += and(MessageFormat.format("contractingParties contains (members contains (negotiator.uuid = ''{0}'' or contact.uuid = ''{0}''))", iFilters.get(ContractFields.OTHER_PARTY_MEMBERS)));
		}

		if(!iFilters.isNullOrEmpty(ContractFields.NEGOTIATION_UUID))
			query += andEq("negotiation.uuid", iFilters.get(ContractFields.NEGOTIATION_UUID));

		if(!iFilters.isNullOrEmpty(ContractFilters.LINKED_CONTRACT_WITH_NEGOTIATION_TEMPLATE_UUIDS)) {
			query += andIn("negotiation.negotiationTemplateUUID", iFilters.get(ContractFilters.LINKED_CONTRACT_WITH_NEGOTIATION_TEMPLATE_UUIDS));
			query += andEq(ContractFields.CONTRACT_TYPE, ContractType.LINKED);
		}

		if(!iFilters.isNullOrEmpty(ContractFields.CREATED_FROM_NEGOTIATION_TEMPLATE_UUID)) {
			query += andIn("negotiation.negotiationTemplateUUID", iFilters.get(ContractFields.CREATED_FROM_NEGOTIATION_TEMPLATE_UUID));
		}

		if(!iFilters.isNullOrEmpty(ContractFilters.NEGOTIATION_TEMPLATE_UUID))
			query += and(MessageFormat.format("negotiationTemplates contains (uuid = ''{0}'')", iFilters.get(ContractFilters.NEGOTIATION_TEMPLATE_UUID)));

		if(!iFilters.isNullOrEmpty(ContractFilters.SUPPORTING_DOCUMENT_NEGOTIATION_UUID))
			query += and(MessageFormat.format("supportingDocumentNegotiations contains (uuid = ''{0}'')", iFilters.get(ContractFilters.SUPPORTING_DOCUMENT_NEGOTIATION_UUID)));

		if(!iFilters.isNullOrEmpty(ContractFields.CONTRACT_TYPE))
			query += andEq(ContractFields.CONTRACT_TYPE, iFilters.get(ContractFields.CONTRACT_TYPE));

		if(!iFilters.isNullOrEmpty(ContractFields.STATUS))
			query += andEq(ContractFields.STATUS, iFilters.get(ContractFields.STATUS));

		if(!iFilters.isNullOrEmpty(ContractFields.PARENT_CONTRACT_UUID))
			query += andEq(ContractFields.PARENT_CONTRACT_UUID, iFilters.get(ContractFields.PARENT_CONTRACT_UUID));
		
		if(!iFilters.isNullOrEmpty(ContractFields.SOURCE_CONTRACT_UUID))
			query += andEq(ContractFields.SOURCE_CONTRACT_UUID, iFilters.get(ContractFields.SOURCE_CONTRACT_UUID));

		if (iFilters.containsKey(ContractFields.TERMINATION_NOTICE_REQUIRED) && iFilters.isTrue(ContractFields.TERMINATION_NOTICE_REQUIRED)){
			query += andEq(MessageFormat.format("{0}.{1}", ContractFields.LIFECYCLE_SETTING, ContractFields.TERMINATION_NOTICE_REQUIRED), true);
		}

		if (iFilters.containsKey(ContractFields.EXPIRY_DATE)){
			query += andEq("expiryDate" + ".format('" + dateFormatPattern + "')",
					PlainSql.get("date('" + dateFormatter.format(iFilters.get(ContractFields.EXPIRY_DATE)) + "', '"	+ dateFormatPattern + "')") );
		}

		if (iFilters.containsKey(ContractFields.EFFECTIVE_DATE)){
			query += andEq("effectiveDate" + ".format('" + dateFormatPattern + "')",
					PlainSql.get("date('" + dateFormatter.format(iFilters.get(ContractFields.EFFECTIVE_DATE)) + "', '"	+ dateFormatPattern + "')") );
		}

		if (iFilters.containsKey(ContractFilters.TERMINATION_NOTICE_DATE)){
			String terminationNoticeDateQuery = String.format(" eval('%s.format(\"%s\").asDate() - %s.%s')"
					, ContractFields.EXPIRY_DATE, dateFormatPattern, ContractFields.LIFECYCLE_SETTING, ContractLifecycleSettingFields.TERMINATION_NOTICE_PERIOD_IN_MILLISECONDS);
			String toCompareWithDateQuery = String.format("date('%s', '%s')", dateFormatter.format(iFilters.get(ContractFilters.TERMINATION_NOTICE_DATE)), dateFormatPattern);
			query += andEq(terminationNoticeDateQuery, PlainSql.get(toCompareWithDateQuery));
		}

		if (!iFilters.isNullOrEmpty(RestParameters.KEYWORDS)){
			List<String> keywords = Lists.newArrayList(Splitter.on(" ").trimResults()
				       .omitEmptyStrings().split((String) iFilters.get(RestParameters.KEYWORDS)));
			for (String keyword : keywords) {
				query += and(contains(QueryKeywords.ANY + ".toLowerCase()", keyword.toLowerCase()));
			}
		}

		return query;
	}
}
