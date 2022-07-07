package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExScheduleJobException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import java.util.Date;

import static org.quartz.TriggerBuilder.newTrigger;

public abstract class ExScheduler {
	
	private static Logger logger = ExLogger.get();
	
	protected Scheduler getScheduler(){
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler scheduler = null;
		try {
			scheduler = sf.getScheduler();
		} catch (SchedulerException e) {
			throw new ExScheduleJobException("Failed to create new scheduler");
		}
		return scheduler;
	}
	
	public void updateScheduledJob(JobDetail jobDetail,TriggerKey triggerKey, Date eventTime){
		try {
			Scheduler scheduler = getScheduler();
			
			//scheduler.isStarted()
			
			SimpleTrigger newTrigger = (SimpleTrigger) newTrigger()
					.withIdentity(triggerKey)
					.startAt(eventTime)
					.build();
			
			scheduler.rescheduleJob(triggerKey, newTrigger);
			
			logger.info("Rescheduling job {} with trigger {}", jobDetail.getDescription(), newTrigger.getDescription());
			
			//scheduler.start();

			logger.info("Scheduler started for the job  {} with trigger {}", jobDetail.getDescription(), newTrigger.getDescription());
		} catch (Exception e) {
			logger.error("Failed to schedule job because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}", e.getMessage(),
					e.getStackTrace());
			throw new ExScheduleJobException(ErrorKeys.JOB_SCHEDULE_FAIL, e);
		}
	}
	
}
