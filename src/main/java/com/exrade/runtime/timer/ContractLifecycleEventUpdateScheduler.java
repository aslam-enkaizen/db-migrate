package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.util.DateUtil;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import java.util.Calendar;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class ContractLifecycleEventUpdateScheduler extends ExScheduler {
	static final Logger logger = ExLogger.get();

	public static void schedule() {
		logger.info("Scheduling ContractLifecycleEventUpdateScheduler");

		if(!ExConfiguration.getPropertyAsBoolean("scheduler.ContractLifecycleEventUpdateScheduler.enabled")) {
			logger.warn("Scheduling ContractLifecycleEventUpdateScheduler  is Disabled!");
			return;
		}

		SchedulerFactory sf = new StdSchedulerFactory();
		try {

			Calendar startTimeCal = Calendar.getInstance();
			startTimeCal.setTime(TimeProvider.now());
			//startTimeCal.add(Calendar.MINUTE, 1);
			startTimeCal.add(Calendar.DAY_OF_MONTH, 1);

			Scheduler scheduler = sf.getScheduler();

			JobDetail job = newJob(ContractLifecycleEventUpdateJob.class)
					.withIdentity("job:ContractLifecycleEventUpdateScheduler")
					.build();

			SimpleTrigger trigger = (SimpleTrigger) newTrigger()
					.withIdentity("trigger:ContractLifecycleEventUpdateScheduler")
					.startAt(DateUtil.toBeginningOfTheDay(startTimeCal.getTime()))
					//.startAt(startTimeCal.getTime())
					.withSchedule(
					    SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInMilliseconds(ExConfiguration.getLongProperty("scheduler.ContractLifecycleEventUpdateScheduler.interval"))
						.repeatForever())
					.build();

			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (Exception e) {
			logger.error(
					"Failed to schedule ContractLifecycleEventUpdateScheduler because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}",
					e.getMessage(), e.getStackTrace());
		}
	}

}
