package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.models.contract.Contract;
import com.exrade.models.notification.NotificationType;
import com.exrade.models.trak.TrakApproval;
import com.exrade.models.trak.TrakResponse;
import com.exrade.platform.persistence.ConnectionManager;
import com.exrade.runtime.blockchain.OrderSmartContract;
import com.exrade.runtime.blockchain.TraktiSmartContractApiClient;
import com.exrade.runtime.contract.ContractManager;
import com.exrade.runtime.contract.IContractManager;
import com.exrade.runtime.notification.event.ContractNotificationEvent;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.event.TrakNotificationEvent;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.trak.ITrakManager;
import com.exrade.runtime.trak.TrakManager;
import com.exrade.runtime.trak.TrakOrchestrator;
import com.exrade.util.ContextHelper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public class TrakSmartContractJob extends ExradeJob implements Job {

	private static Logger logger = ExLogger.get();
	IContractManager contractManager;
	ITrakManager trakManager;
	TrakOrchestrator trakOrchestrator;
	OrderSmartContract orderSmartContract;
	TraktiSmartContractApiClient smartContractApiClient = new TraktiSmartContractApiClient();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		logger.info("Executing TrakSmartContractJob.");
		try {
			ConnectionManager.getInstance().getObjectConnection(); // Fix for
																	// "com.orientechnologies.orient.core.exception.ODatabaseException:
																	// Database instance is not set in current thread."
			JobDataMap data = context.getJobDetail().getJobDataMap();
			setupContext(data.getString(ContextHelper.MEMBERSHIP_UUID));

			NotificationEvent notificationEvent = (NotificationEvent) data.get(RestParameters.NOTIFICATION_EVENT);

			contractManager = new ContractManager();
			trakManager = new TrakManager((ContractManager) contractManager);
			smartContractApiClient = new TraktiSmartContractApiClient();
			trakOrchestrator = new TrakOrchestrator();

			if (notificationEvent.getNotificationType() == NotificationType.CONTRACT_CREATED) {
				ContractNotificationEvent contractNotificationEvent = (ContractNotificationEvent) notificationEvent;
				Contract contract = contractNotificationEvent.getContract();
				trakOrchestrator.buildTraks(contract);
				
				if(InformationModelUtil.hasSmartContract(contract.getAgreementInformationModel())) {
					smartContractApiClient.initSmartContract(contract.getUuid());
				}

			} else if (notificationEvent.getNotificationType() == NotificationType.TRAK_APPROVAL_UPDATED
					|| notificationEvent.getNotificationType() == NotificationType.TRAK_APPROVAL_CREATED) {
				TrakNotificationEvent trakNotificationEvent = (TrakNotificationEvent) notificationEvent;
				TrakApproval trakApproval = trakNotificationEvent.getTrakApproval();
				Contract contract = trakApproval.getTrakResponse().getTrak().getContract();
				
				trakOrchestrator.handleTrakUpdate(contract, trakApproval);
			} else if (notificationEvent.getNotificationType() == NotificationType.TRAK_RESPONSE_CREATED
					|| notificationEvent.getNotificationType() == NotificationType.TRAK_RESPONSE_UPDATED) {
				TrakNotificationEvent trakNotificationEvent = (TrakNotificationEvent) notificationEvent;
				TrakResponse trakResponse = trakNotificationEvent.getTrakResponse();
				Contract contract = trakResponse.getTrak().getContract();
				
				trakOrchestrator.handleTrakUpdate(contract, trakResponse);
			}

		} catch (Exception ex) {
			logger.warn("TrakSmartContractJob failed!", ex);
		}

		logger.info("TrakSmartContractJob finished.");
	}
}
