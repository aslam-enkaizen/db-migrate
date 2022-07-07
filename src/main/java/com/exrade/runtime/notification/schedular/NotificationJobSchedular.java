package com.exrade.runtime.notification.schedular;

import com.exrade.core.ExLogger;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExScheduleJobException;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.notification.job.NotificationSchedularJob;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.timer.ExScheduler;
import com.exrade.util.ContextHelper;
import org.quartz.*;
import org.slf4j.Logger;

public class NotificationJobSchedular extends ExScheduler {

	private static Logger logger = ExLogger.get();

	public <T> void schedule(NotificationEvent<T> event){
		logger.info("Scheduling NotificationEvent {}", event.getNotificationType());

		if(!ExConfiguration.getPropertyAsBoolean("scheduler.NotificationJobSchedular.enabled")) {
			logger.warn("Scheduling NotificationEvent  is Disabled!");
			return;
		}

		try {
			Scheduler scheduler = getScheduler();

			JobDataMap dataMap = new JobDataMap();
			dataMap.put(RestParameters.NOTIFICATION_EVENT, event);
			dataMap.put(ContextHelper.MEMBERSHIP_UUID, ContextHelper.getMembershipUUID());

			JobDetail job = JobBuilder.newJob(NotificationSchedularJob.class)
					.usingJobData(dataMap)
					.build();

			SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger().startNow().build();

			scheduler.scheduleJob(job, trigger);
			scheduler.start();

			logger.info("Scheduler started for the job  {} with trigger {}", job.getDescription(), trigger.getDescription());
		} catch (Exception e) {
			logger.error("Failed to schedule job because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}", e.getMessage(),
					e.getStackTrace());
			throw new ExScheduleJobException(ErrorKeys.JOB_SCHEDULE_FAIL, e);
		}

	}

}
