package com.exrade.runtime.message.persistence;

import com.exrade.models.messaging.NegotiationMessage;
import com.exrade.platform.persistence.IConnectionManager;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.rest.RestParameters.MessageFields;

import java.util.List;

public class MessagePersistenceManager extends PersistentManager {

	public MessagePersistenceManager() {
		super();
	}

	public MessagePersistenceManager(IConnectionManager iConnectionManager) {
		super(iConnectionManager);
	}

	public NegotiationMessage read(QueryFilters iFilters) {
		MessageQuery messageQuery = new MessageQuery();
		return readObject(messageQuery, iFilters);
	}

	public <T> List<T> listMessages(QueryFilters iFilters) {

		// add default query order
		if (iFilters.isNull(QueryParameters.SORT)) {
			iFilters.put(QueryParameters.SORT,
					reverse(MessageFields.MESSAGE_CREATION_TIME));
		}

		return listObjects(new MessageBoxQuery().createQuery(iFilters));
	}
}
