package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.runtime.conf.ExConfiguration;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import java.util.Calendar;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class ProfileDowngradeScheduler extends ExScheduler {
	static final Logger logger = ExLogger.get();
	
	public static void schedule() {
		logger.info("Scheduling ProfileDowngradeScheduler");
		
		if(!ExConfiguration.getPropertyAsBoolean("scheduler.ProfileDowngradeScheduler.enabled")) {
			logger.warn("Scheduling ProfileDowngradeScheduler is Disabled!");
			return;
		}
		
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			
			Calendar startTimeCal = Calendar.getInstance();
			startTimeCal.setTime(TimeProvider.now());
			startTimeCal.add(Calendar.MINUTE, 5);
			//cal.add(Calendar.DAY_OF_MONTH, 1);
			
			Scheduler scheduler = sf.getScheduler();
			
			JobDetail job = newJob(ProfileDowngradeJob.class)
					.withIdentity("job:ProfileDowngradeScheduler")
					.build();

			SimpleTrigger trigger = (SimpleTrigger) newTrigger()
					.withIdentity("trigger:ProfileDowngradeScheduler")
					.startAt(startTimeCal.getTime())
					.withSchedule(
					    SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInMilliseconds(ExConfiguration.getLongProperty("scheduler.ProfileDowngradeScheduler.interval"))
						.repeatForever())
					.build();
			
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (Exception e) {
			logger.error(
					"Failed to schedule ProfileDowngradeScheduler because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}",
					e.getMessage(), e.getStackTrace());
		}
	}
	
}
