package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.runtime.conf.ExConfiguration;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import java.util.Calendar;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class PeriodicAlertScheduler extends ExScheduler {
	static final Logger logger = ExLogger.get();

	public static void schedule() {
		logger.info("Scheduling PeriodicAlertScheduler");

		if(!ExConfiguration.getPropertyAsBoolean("scheduler.PeriodicAlertScheduler.enabled")) {
			logger.warn("Scheduling PeriodicAlertScheduler  is Disabled!");
			return;
		}

		SchedulerFactory sf = new StdSchedulerFactory();
		try {

			Calendar startTimeCal = Calendar.getInstance();
			startTimeCal.setTime(TimeProvider.now());
			//startTimeCal.add(Calendar.MINUTE, 2);
			startTimeCal.add(Calendar.DAY_OF_MONTH, 1);
			startTimeCal.set(Calendar.HOUR_OF_DAY, 0);
			startTimeCal.set(Calendar.MINUTE, 10);
			startTimeCal.set(Calendar.SECOND, 0);
			startTimeCal.set(Calendar.MILLISECOND, 0);

			Scheduler scheduler = sf.getScheduler();

			JobDetail job = newJob(PeriodicAlertGeneratorJob.class)
					.withIdentity("job:PeriodicAlertGenerator")
					.build();

			SimpleTrigger trigger = (SimpleTrigger) newTrigger()
					.withIdentity("trigger:PeriodicAlertGenerator")
					//.startAt(startTimeCal.getTime())
					.startAt(startTimeCal.getTime())
					.withSchedule(
					    SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInMilliseconds(ExConfiguration.getLongProperty("scheduler.PeriodicAlertScheduler.interval"))
						.repeatForever())
					.build();

			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (Exception e) {
			logger.error(
					"Failed to schedule PeriodicAlertScheduler because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}",
					e.getMessage(), e.getStackTrace());
		}
	}

}
