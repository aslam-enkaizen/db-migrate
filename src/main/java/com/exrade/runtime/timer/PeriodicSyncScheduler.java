package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.runtime.conf.ExConfiguration;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import java.util.Calendar;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class PeriodicSyncScheduler extends ExScheduler {
	static final Logger logger = ExLogger.get();
	
	public static void schedule() {
		logger.info("Scheduling PeriodicSyncScheduler");
		
		if(!ExConfiguration.getPropertyAsBoolean("scheduler.PeriodicSyncScheduler.enabled")) {
			logger.warn("Scheduling PeriodicSyncScheduler  is Disabled!");
			return;
		}
		
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			
			Calendar startTimeCal = Calendar.getInstance();
			startTimeCal.setTime(TimeProvider.now());
			startTimeCal.add(Calendar.MINUTE, 2);
			//cal.add(Calendar.DAY_OF_MONTH, 1);
			
			Scheduler scheduler = sf.getScheduler();
			
			JobDetail job = newJob(PeriodicSyncJob.class)
					.withIdentity("job:PeriodicSync")
					.build();

			SimpleTrigger trigger = (SimpleTrigger) newTrigger()
					.withIdentity("trigger:PeriodicSync")
					.startAt(startTimeCal.getTime())
					.withSchedule(
					    SimpleScheduleBuilder.simpleSchedule()
					    .withIntervalInMilliseconds(ExConfiguration.getLongProperty("scheduler.PeriodicSyncScheduler.interval"))
						.repeatForever())
					.build();
			
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (Exception e) {
			logger.error(
					"Failed to schedule PeriodicSyncScheduler because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}",
					e.getMessage(), e.getStackTrace());
		}
	}
	
}
