package com.exrade.runtime.clause;

import com.exrade.core.ExLogger;
import com.exrade.models.activity.Verb;
import com.exrade.models.informationmodel.Clause;
import com.exrade.models.negotiation.PublishStatus;
import com.exrade.models.notification.NotificationType;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.SearchResultSummary;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.security.Security;
import com.exrade.runtime.activity.ActivityLogger;
import com.exrade.runtime.clause.persistence.ClausePersistentManager;
import com.exrade.runtime.clause.persistence.query.ClauseQuery;
import com.exrade.runtime.clause.persistence.query.ClauseSearchSummaryQuery;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.ClauseNotificationEvent;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author: Md. Aslam Hossain
 *
 */
public class ClauseManager implements IClauseManager {
	private PersistentManager persistenceManager = new PersistentManager();
	private ClausePersistentManager clausePersistentManager = new ClausePersistentManager();
	private final NotificationManager notificationManager = new NotificationManager();

	@Override
	public Clause createClause(Clause iClause) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES_CLAUSEREPO);

		iClause = persistenceManager.create(iClause);
		ActivityLogger.log(ContextHelper.getMembership(), Verb.CREATE, iClause, Arrays.asList(iClause.getCreator()));
		ExLogger.get().info("Created Clause: {}", iClause.getUuid());
		return iClause;
	}

	@Override
	public Clause getClause(String iClauseUUID) {
		ExLogger.get().info("Get Clause: {}", iClauseUUID);
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES_CLAUSEREPO);

		return persistenceManager.readObjectByUUID(Clause.class, iClauseUUID);
	}

	@Override
	public List<Clause> listClauses(QueryFilters iFilters) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES_CLAUSEREPO);

		return persistenceManager.listObjects(new ClauseQuery(), iFilters);
	}

	@Override
	public Clause updateClause(Clause iClause) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES_CLAUSEREPO);

		Clause clause = getClause(iClause.getUuid());
		Clause updatedClause = persistenceManager.update(iClause);
		if (!clause.getPublicationStatus().equals(updatedClause.getPublicationStatus())) {
			notificationManager
					.process(new ClauseNotificationEvent(NotificationType.CLAUSE_STATUS_UPDATED, updatedClause));
		}
		ActivityLogger.log(ContextHelper.getMembership(), Verb.UPDATED, updatedClause,
				Arrays.asList(updatedClause.getCreator()));
		ExLogger.get().info("Updated Clause: {}", iClause.getUuid());
		return updatedClause;
	}

	@Override
	public void deleteClause(String iClauseUUID) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES_CLAUSEREPO);

		Clause clause = getClause(iClauseUUID);
		if (!clause.getPublicationStatus().equals(PublishStatus.ACTIVE)) {
			persistenceManager.delete(getClause(iClauseUUID));
			ActivityLogger.log(ContextHelper.getMembership(), Verb.DELETE, clause, Arrays.asList(clause.getCreator()));
			ExLogger.get().info("Deleted Clause: {}", iClauseUUID);
		}
	}

	@Override
	public List<SearchResultSummary> listSearchResultSummary(Map<String, String> iFilters) {
		//checking profile permission
		Security.hasAccessPermission(Security.ProfilePermissions.TEMPLATES_CLAUSEREPO);

		List<SearchResultSummary> searchResultSummries = new ArrayList<>();
		QueryFilters filters = QueryFilters.create(iFilters);
		filters.put("profile.uuid", ContextHelper.getMembership().getProfile().getUuid());

		for (String field : ExCollections.commaSeparatedToList(iFilters.get(QueryParameters.FIELD))) {
			filters.put(QueryParameters.FIELD, field);
			searchResultSummries.addAll(getSearchResults(filters));
		}

		return searchResultSummries;
	}

	private List<SearchResultSummary> getSearchResults(QueryFilters filters) {
		List<SearchResultSummary> searchResultSummaries = new ArrayList<SearchResultSummary>();
		searchResultSummaries
				.add(clausePersistentManager.getSearchResultSummary(new ClauseSearchSummaryQuery(), filters));
		return searchResultSummaries;
	}

}
