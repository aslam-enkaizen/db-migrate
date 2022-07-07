package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExScheduleJobException;
import com.exrade.runtime.negotiation.INegotiationManager;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.notification.NotificationConstants;
import com.exrade.runtime.notification.NotificationMessageProcessor;
import com.exrade.runtime.rest.RestParameters.NegotiationFilters;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public class NotificationJob extends ExradeJob implements Job {

	private static Logger logger = ExLogger.get();

	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();

		String negotiationUUID = data
				.getString(NegotiationFilters.NEGOTIATION_UUID);
		String notificationName = data
				.getString(NotificationConstants.NOTIFICATION_NAME);
		String notificationType = data
				.getString(NotificationConstants.NOTIFICATION_TYPE);
		
		INegotiationManager negotiationManager = new NegotiationManager();
		Negotiation negotiation = negotiationManager.getNegotiation(negotiationUUID);
		setupContext(negotiation.getOwner().getIdentifier());
		
		logger.info(
				"Sending notification with params - NegotiationID: {}, NotificationType: {}, NotificationName: {}.",
				negotiationUUID, notificationType, notificationName);

		try {
			NotificationMessageProcessor notificationProcessor = new NotificationMessageProcessor(
					notificationType, notificationName);
			notificationProcessor.process(negotiationUUID);
		} catch (Exception e) {
			logger.error(
					"Sending notification failed because of the error - {}",
					e.getMessage(), e);
			throw new ExScheduleJobException(ErrorKeys.JOB_EXECUTION_FAIL, e);
		}

		logger.info("Sending notification succedded.");
	}
}
