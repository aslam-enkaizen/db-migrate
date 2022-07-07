package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.runtime.conf.ExConfiguration;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import java.util.Calendar;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class FileMetadataUpdateScheduler extends ExScheduler {
	static final Logger logger = ExLogger.get();
	
	public static void schedule() {
		logger.info("Scheduling FileMetadataUpdateScheduler");
		
		if(!ExConfiguration.getPropertyAsBoolean("scheduler.FileMetadataUpdateScheduler.enabled")) {
			logger.warn("Scheduling FileMetadataUpdateScheduler is Disabled!");
			return;
		}
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			
			Calendar startTimeCal = Calendar.getInstance();
			startTimeCal.setTime(TimeProvider.now());
			startTimeCal.add(Calendar.MINUTE, 1);
			
			Scheduler scheduler = sf.getScheduler();
			
			JobDetail job = newJob(FileMetadataUpdateJob.class)
					.withIdentity("job:FileMetadataUpdateScheduler")
					.build();

			SimpleTrigger trigger = (SimpleTrigger) newTrigger()
					.withIdentity("trigger:FileMetadataUpdateScheduler")
					.startAt(startTimeCal.getTime())
					.build();
			
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (Exception e) {
			logger.error(
					"Failed to schedule FileMetadataUpdateScheduler because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}",
					e.getMessage(), e.getStackTrace());
		}
	}
	
}
