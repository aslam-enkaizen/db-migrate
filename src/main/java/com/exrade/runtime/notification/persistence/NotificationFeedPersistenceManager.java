package com.exrade.runtime.notification.persistence;

import com.exrade.platform.persistence.ConnectionManager;
import com.exrade.platform.persistence.IConnectionManager;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.Query;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.NotificationFeedFields;

public class NotificationFeedPersistenceManager extends PersistentManager {

	public NotificationFeedPersistenceManager() {
		this(ConnectionManager.getInstance());
	}

	public NotificationFeedPersistenceManager(IConnectionManager iConnectionManager) {
		super(iConnectionManager);
	}

	public void markFieldTrue(String membershipUUID, String field) {
		String negotiationTypeUpdateQuery = String.format(
				"update NotificationFeed set %1$s=true, %2$s=sysdate() where %3$s.uuid = '%4$s' and %1$s=false",
				field, RestParameters.UPDATE_DATE, NotificationFeedFields.MEMBERSHIP, membershipUUID);

		executeCommand(new Query(negotiationTypeUpdateQuery));
	}

}
