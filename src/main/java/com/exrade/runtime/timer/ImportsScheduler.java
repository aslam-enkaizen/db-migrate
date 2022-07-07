package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.runtime.rest.RestParameters;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import java.util.Calendar;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Rhidoy
 * @created 15/04/2022
 * @package com.exrade.runtime.timer
 * <p>
 * This class schedule job for doing imports task from the import data provided.
 */
public class ImportsScheduler extends ExScheduler {
	static final Logger logger = ExLogger.get();

	public static <T> void schedule(String importUuid) {
		logger.info("Scheduling Import scheduler");

		SchedulerFactory sf = new StdSchedulerFactory();
		try {

			JobDataMap dataMap = new JobDataMap();
			dataMap.put(RestParameters.UUID, importUuid);

			Calendar startTimeCal = Calendar.getInstance();
			startTimeCal.setTime(TimeProvider.now());
			startTimeCal.add(Calendar.MINUTE, 1);
			
			Scheduler scheduler = sf.getScheduler();
			
			JobDetail job = newJob(ImportsJob.class)
					.withIdentity("job:ImportsScheduler")
					.usingJobData(dataMap)
					.build();

			SimpleTrigger trigger = (SimpleTrigger) newTrigger()
					.withIdentity("trigger:ImportsScheduler")
					.startAt(startTimeCal.getTime())
					.build();
			
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (Exception e) {
			logger.error(
					"Failed to schedule ImportsScheduler because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}",
					e.getMessage(), e.getStackTrace());
		}
	}
	
}
