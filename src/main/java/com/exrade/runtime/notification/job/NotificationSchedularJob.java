package com.exrade.runtime.notification.job;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.models.notification.NotificationType;
import com.exrade.platform.persistence.ConnectionManager;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.handler.*;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.util.ContextHelper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public class NotificationSchedularJob extends ExradeJob implements Job {

    private static Logger logger = ExLogger.get();

//	private INotificationSettingsManager notificationSettingsManager = new NotificationSettingsManager();
//	private IUserSettingsManager userSettingsManager = new UserSettingsManager();
//	private IActivityManager activityManager = new ActivityManager();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Executing NotificationSchedularJob.");
        ConnectionManager.getInstance().getObjectConnection(); // Fix for "com.orientechnologies.orient.core.exception.ODatabaseException: Database instance is not set in current thread."

        JobDataMap data = context.getJobDetail().getJobDataMap();
        setupContext(data.getString(ContextHelper.MEMBERSHIP_UUID));

        NotificationEvent notificationEvent = (NotificationEvent) data.get(RestParameters.NOTIFICATION_EVENT);
        INotificationHandler notificationHandler = null;

        if (NotificationType.NegotiationNotifications.contains(notificationEvent.getNotificationType())) {
            notificationHandler = new NegotiationNotificationHandler();
        } else if (NotificationType.WorkGroupNotifications.contains(notificationEvent.getNotificationType())) {
            notificationHandler = new WorkGroupNotificationHandler();
        } else if (NotificationType.ProfileNotifications.contains(notificationEvent.getNotificationType())) {
            notificationHandler = new ProfileNotificationHandler();
        } else if (NotificationType.AuthorisationNotifications.contains(notificationEvent.getNotificationType())) {
            notificationHandler = new AuthorisationNotificationHandler();
        } else if (NotificationType.ReviewNotifications.contains(notificationEvent.getNotificationType())) {
            notificationHandler = new ReviewNotificationHandler();
        } else if (NotificationType.SignatureNotifications.contains(notificationEvent.getNotificationType())) {
            notificationHandler = new SignatureNotificationHandler();
        } else if (NotificationType.ContractNotifications.contains(notificationEvent.getNotificationType())) {
            notificationHandler = new ContractNotificationHandler();
        } else if (NotificationType.UserNotifications.contains(notificationEvent.getNotificationType())) {
            notificationHandler = new UserNotificationHandler();
        } else if (NotificationType.InformationModelNotifications.contains(notificationEvent.getNotificationType())) {
            notificationHandler = new InformationModelNotificationHandler();
        } else if (NotificationType.TrakNotifications.contains(notificationEvent.getNotificationType())) {
            notificationHandler = new TrakNotificationHandler();
		} else if (NotificationType.ClauseNotifications.contains(notificationEvent.getNotificationType())) {
			notificationHandler = new ClauseNotificationHandler();
		} else if (NotificationType.IMPORT_FINISHED == notificationEvent.getNotificationType()) {
			notificationHandler = new ImportNotificationHandler();
		} else if (NotificationType.UPCOMING_LIFE_CYCLE_EVENTS == notificationEvent.getNotificationType()) {
			notificationHandler = new UpcomingLifeCycleEventNotificationHandler();
		} else if (NotificationType.PaymentNotifications.contains(notificationEvent.getNotificationType())) {
            notificationHandler = new PaymentNotificationHandler();
        }

        if (notificationHandler != null) {
            try {
                notificationHandler.handle(notificationEvent);
            } catch (Exception ex) {
                logger.warn("Sending notification failed!", ex);
                throw ex;
            }
        }

        logger.info("Sending notification succedded.");
    }

}
