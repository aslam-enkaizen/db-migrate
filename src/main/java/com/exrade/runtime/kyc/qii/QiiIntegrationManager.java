package com.exrade.runtime.kyc.qii;

import com.exrade.core.ExLogger;
import com.exrade.models.invitations.NegotiationInvitation;
import com.exrade.models.kyc.qii.QiiData;
import com.exrade.models.kyc.qii.QiiRequest;
import com.exrade.models.kyc.qii.QiiRequestStatus;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.invitation.IInvitationManager;
import com.exrade.runtime.invitation.NegotiationInvitationManager;
import com.exrade.runtime.kyc.qii.persistence.QiiQuery;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.notification.NotificationManager;
import com.exrade.runtime.notification.event.NegotiationNotificationEvent;
import com.exrade.runtime.rest.RestParameters.QiiDataFields;
import com.exrade.runtime.rest.RestParameters.QiiDataFilters;
import com.exrade.util.ContextHelper;
import com.exrade.util.ExCollections;
import com.exrade.util.UrlUtil;
import com.google.common.base.Strings;

import java.util.List;

public class QiiIntegrationManager {
	private PersistentManager persistenceManager = new PersistentManager();
	private INegotiationManager negotiationManager = new NegotiationManager();
	private NotificationManager notificationManager = new NotificationManager();
	private IInvitationManager invitationManager = new NegotiationInvitationManager();

	public QiiIntegrationManager() {

	}

	public QiiData createQiiData(String redirectUrl, String negotiationUUID) {
		QueryFilters queryFilter = QueryFilters.create(QiiDataFilters.NEGOTIATION_UUID, negotiationUUID);
		queryFilter.putIfNotEmpty(QiiDataFilters.MEMBERSHIP_UUID, ContextHelper.getMembershipUUID());

		List<QiiData> qiiDataList = listQiiData(queryFilter);
		if(ExCollections.isEmpty(qiiDataList)) {
			Negotiation negotiation = negotiationManager.getNegotiation(negotiationUUID);
			String displayName = negotiation.getTitle();
			String linkName = String.format("%s-%s", negotiationUUID, ContextHelper.getMembershipUUID());
			QiiApiClient.getInstance().createRequestManagement(displayName, linkName, redirectUrl);
			String requestManagementId = QiiApiClient.getInstance().getRequestManagementIdFromLinkName(linkName);

			QiiData qiiData = new QiiData();
			qiiData.setMembership((Membership)ContextHelper.getMembership());
			qiiData.setNegotiation(negotiation);
			qiiData.setRequestManagementName(displayName);
			qiiData.setRequestManagementLinkName(linkName);
			qiiData.setRequestManagementId(requestManagementId);
			qiiData.setRequestManagementRedirect(redirectUrl);
			qiiData = persistenceManager.create(qiiData);

			ExLogger.get().info("Created QiiData: {} - {}", qiiData.getUuid(), qiiData.getMembership());

			return qiiData;
		}
		else {
			return qiiDataList.get(0);
		}
	}

	@Deprecated
	public void handleCallback(String householdId) {
		ExLogger.get().warn("Handling callback for QiiData household: {}", householdId);

		String requestManagementId = QiiApiClient.getInstance().getRequestManagementIdFromHouseholdId(householdId);

		QiiData qiiData = retrieveDataFromQii(householdId, requestManagementId);

		sendQiiCallbackNotificationToUser(qiiData);
	}

	public void handleCallback(String householdId, String requestManagementId) {
		ExLogger.get().warn("Handling callback for QiiData household: {}, requestManagementId: {}", householdId, requestManagementId);

		QiiData qiiData = retrieveDataFromQii(householdId, requestManagementId);

		sendQiiCallbackNotificationToUser(qiiData);
	}

	public QiiData retrieveDataFromQii(String householdId, String requestManagementId) {
		ExLogger.get().warn("Retrieving data from Qii: {}, requestManagementId: {}", householdId, requestManagementId);

		QiiData qiiData = null;

		List<QiiData> qiiDataList = listQiiData(QueryFilters.create(QiiDataFields.REQUEST_MANAGEMENT_ID, requestManagementId));
		if(ExCollections.isEmpty(qiiDataList)) {
			QiiRequest requestManagement = QiiApiClient.getInstance().getRequestManagement(requestManagementId);
			qiiDataList = listQiiData(QueryFilters.create(QiiDataFields.REQUEST_MANAGEMENT_LINK_NAME, requestManagement.getLinkName()));
		}

		if(ExCollections.isNotEmpty(qiiDataList)) {
			qiiData = qiiDataList.get(0);
			qiiData.setHouseholdId(householdId);
			qiiData.setRequestManagementId(requestManagementId);
			qiiData = updateQiiData(qiiData);

			qiiData.setMembers(QiiApiClient.getInstance().getMembersOfHousehold(householdId));
			qiiData = updateQiiData(qiiData);

			ExLogger.get().warn("Updated QiiData - household: {}, requestManagement: {}", householdId, qiiData.getRequestManagementId());
		}
		else {
			ExLogger.get().warn("Did not find QiiData household: {}", householdId);
		}

		return qiiData;
	}

	public QiiData updateQiiData(QiiData iQiiData) {
		iQiiData = persistenceManager.update(iQiiData);

		ExLogger.get().info("Updated QiiData: {} - {}", iQiiData.getUuid(), iQiiData.getMembership());

		return iQiiData;
	}

	public QiiData getQiiData(String qiiDataUUID) {
		return persistenceManager.readObjectByUUID(QiiData.class, qiiDataUUID);
	}

	public List<QiiData> listQiiData(QueryFilters iFilters){
		return persistenceManager.listObjects(new QiiQuery(), iFilters);
	}

	public void deleteQiiData(String iQiiDataUUID) {
		persistenceManager.delete(getQiiData(iQiiDataUUID));
		ExLogger.get().info("Deleted QiiData: {}", iQiiDataUUID);
	}

	public QiiRequestStatus getQiiDataRequestStatus(String qiiDataUUID) {
		QiiData qiiData = persistenceManager.readObjectByUUID(QiiData.class, qiiDataUUID);
		if(!Strings.isNullOrEmpty(qiiData.getHouseholdId()))
			return QiiRequestStatus.COMPLETED;

		QiiRequest requestManagement = QiiApiClient.getInstance().getRequestManagement(qiiData.getRequestManagementId());
		if(requestManagement != null && requestManagement.isUsed())
			return QiiRequestStatus.COMPLETED_MAIN_MEMBER;

		return QiiRequestStatus.PENDING;
	}

	private void sendQiiCallbackNotificationToUser(QiiData qiiData) {
		if(qiiData != null && !Strings.isNullOrEmpty(qiiData.getRequestManagementRedirect())) {
			String invitationUUID = UrlUtil.extractNegotiationInvitationUUID(qiiData.getRequestManagementRedirect());
			if(!Strings.isNullOrEmpty(invitationUUID)) {
				notificationManager.process(new NegotiationNotificationEvent(NotificationType.NEGOTIATION_QII_DATA_RECEIVED, qiiData.getNegotiation(), (NegotiationInvitation)invitationManager.getInvitation(invitationUUID)));
			}
		}
	}
}
