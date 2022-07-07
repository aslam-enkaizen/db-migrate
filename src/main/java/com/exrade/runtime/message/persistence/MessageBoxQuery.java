package com.exrade.runtime.message.persistence;

import com.exrade.platform.persistence.query.OrientSqlBuilder;
import com.exrade.platform.persistence.query.PlainSql;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.InformationMessageFilters;
import com.exrade.runtime.rest.RestParameters.MessageFields;

public class MessageBoxQuery extends OrientSqlBuilder {

	@Override
	protected String buildQuery(QueryFilters iFilters) {

		if (iFilters.isNull(MessageBoxQFilters.NEGOTIATION_UUID)){
			throw new IllegalArgumentException("Negotiation ID is required to select messages");
		}

		String innerMessagesQuery = "(select flatten(unionall(messageBox.processedMessages,messageBox.enqueuedMessages,messageBox.infoMessages)) "
				+ "from (select messageBox from negotiation where uuid = '"+ iFilters.get(MessageBoxQFilters.NEGOTIATION_UUID) + "'))";
		
		String query = "select from " + innerMessagesQuery + " where 1 = 1 ";
		
		if (iFilters.isTrue(MessageBoxQFilters.SENT) || iFilters.isTrue(MessageBoxQFilters.RECEIVED)){
			query += and(applySenderReceiverConditions(iFilters));
		}
		
		if (iFilters.isNotNull(MessageFields.MESSAGE_TYPE)) {
			query += andEq(MessageFields.MESSAGE_TYPE,iFilters.get(MessageFields.MESSAGE_TYPE));
		}
		
		
		return query;
	}

	private String applySenderReceiverConditions(QueryFilters iFilters) {
		String condition = "";
		if (iFilters.isTrue(MessageBoxQFilters.SENT)) {
			condition += "(";
			if(iFilters.isNotNull(MessageBoxQFilters.SENDER_UUID))
				condition += eq(MessageBoxQFilters.SENDER_UUID, iFilters.get(MessageBoxQFilters.SENDER_UUID));
			else
				condition += " ( " + eq(MessageFields.SENDER, PlainSql.get(getActor().getId()))
							+ " or " + eq("sender.profile.uuid", getActor().getProfile().getUuid()) + " ) ";
			if (iFilters.isNotNull(InformationMessageFilters.PARTNER_UUID)) {
				condition += andEq(MessageFields.RECEIVER + ".uuid",
						iFilters.get(InformationMessageFilters.PARTNER_UUID));
			}
			condition += ")";
		}

		if (iFilters.isTrue(MessageBoxQFilters.RECEIVED)
				&& iFilters.isTrue(MessageBoxQFilters.SENT)) {
			condition += " or ";
		}

		if (iFilters.isTrue(MessageBoxQFilters.RECEIVED)) {
			condition += "(";
			if(iFilters.isNotNull(MessageBoxQFilters.RECEIVER_UUID))
				condition += eq(MessageBoxQFilters.RECEIVER_UUID, iFilters.get(MessageBoxQFilters.RECEIVER_UUID));
			else
				condition += " ( " + eq(MessageFields.RECEIVER, PlainSql.get(getActor().getId()))
						+ " or " + eq("receiver.profile.uuid", getActor().getProfile().getUuid()) + " ) ";

			if (iFilters.isNotNull(InformationMessageFilters.PARTNER_UUID)) {
				condition += andEq(MessageFields.SENDER + ".uuid",
						iFilters.get(InformationMessageFilters.PARTNER_UUID));
			}
			condition += ")";
		}
		return "(" + condition + ")";
	}
	
	public static class MessageBoxQFilters {
		public static final String NEGOTIATION_UUID = "negotiationUUID";
		public static final String SENDER_UUID = "sender.uuid";
		public static final String RECEIVER_UUID = "receiver.uuid";
		public static final String SENT = "sent";
		public static final String RECEIVED = "received";
	}

}


