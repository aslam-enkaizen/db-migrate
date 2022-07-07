package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.runtime.notification.event.NotificationEvent;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.util.ContextHelper;
import com.exrade.util.DateUtil;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import java.util.Calendar;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class TrakSmartContractScheduler extends ExScheduler {
	static final Logger logger = ExLogger.get();
	
	public static <T> void schedule(NotificationEvent<T> event) {
		logger.info("Scheduling TrakSmartContractScheduler");
		
		/*
		 * if(!ExConfiguration.getPropertyAsBoolean(
		 * "scheduler.TrakSmartContractScheduler.enabled")) {
		 * logger.warn("Scheduling TrakSmartContractScheduler is Disabled!"); return; }
		 */
		
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			JobDataMap dataMap = new JobDataMap();
			dataMap.put(ContextHelper.MEMBERSHIP_UUID, ContextHelper.getMembershipUUID());
			dataMap.put(RestParameters.NOTIFICATION_EVENT, event);
			
			Scheduler scheduler = sf.getScheduler();
			
			JobDetail job = newJob(TrakSmartContractJob.class)
					.usingJobData(dataMap)
					.build();

			SimpleTrigger trigger = (SimpleTrigger) newTrigger()
					.startAt(DateUtil.addWithCurrentDate(Calendar.SECOND, 20, true))
					.build();
			
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (Exception e) {
			logger.error(
					"Failed to schedule TrakSmartContractScheduler because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}",
					e.getMessage(), e.getStackTrace());
		}
	}
	
}
