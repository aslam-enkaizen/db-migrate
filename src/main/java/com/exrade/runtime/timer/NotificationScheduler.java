package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.models.negotiation.Negotiation;
import com.exrade.models.processmodel.protocol.events.TimeEvent;
import com.exrade.runtime.conf.ExConfiguration;
import com.exrade.runtime.notification.NotificationConstants;
import com.exrade.runtime.rest.RestParameters.NegotiationFilters;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import java.util.*;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class NotificationScheduler extends ExScheduler {
	static final Logger logger = ExLogger.get();

	private static List<Integer> timeIntervals = new ArrayList<Integer>();
	private static int timeUnit = Calendar.MINUTE;

	static {
		try {
			String intervals = ExConfiguration
					.getStringProperty(ExConfiguration.configKeys.MAIL_NOTIFICATION_INTERVAL);

			for (String interval : intervals.split(",")) {
				timeIntervals.add(Integer.parseInt(interval.trim()));
			}
		} catch (Exception e) {
			logger.error(
					"Failed to create NotificationScheduler instance because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}",
					e.getMessage(), e.getStackTrace());
		}
	}

	private static JobKey getJobJey(String negotiationUUID, String eventType, String eventName){
		return new JobKey("job:" + negotiationUUID + ":" + eventType + ":" + eventName,null);
	}
	
	private static TriggerKey getTriggerKey(String negotiationUUID, String eventType, String eventName, int timeInHr){
		return new TriggerKey("trigger:" + negotiationUUID + ":" + eventType + ":" + eventName + ":" + timeInHr,null);
	}
	
	private static JobDetail buildJobDetail(String negotiationUUID, String eventName, String eventType){
		JobDataMap dataMap = new JobDataMap();
		dataMap.put(NegotiationFilters.NEGOTIATION_UUID, negotiationUUID);
		dataMap.put(NotificationConstants.NOTIFICATION_NAME, eventName);
		dataMap.put(NotificationConstants.NOTIFICATION_TYPE, eventType);
		return newJob(NotificationJob.class)
				.withIdentity(getJobJey(negotiationUUID,eventType,eventName)).usingJobData(dataMap).build();
	}
	
	/**
	 * Schedule notifications for the given negotiation, if notifications are already present they will be replaced
	 * with the updated timings
	 * @param negotiation
	 */ 
	public static void schedule(Negotiation negotiation) {
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			Scheduler scheduler = sf.getScheduler();

			Map<JobDetail, List<Trigger>> jobTriggers = new HashMap<JobDetail, List<Trigger>>();

			// schedule end date events
			buildJob(scheduler,jobTriggers, negotiation.getUuid(),
					NotificationConstants.NotificationType.END_DATE,
					"Negotiation Ending", negotiation.getEndDate());
			
			// schedule process specific owner time events
			for (TimeEvent event : negotiation.getOwnerTimeEvents()) {
				buildJob(scheduler,
						jobTriggers,
						negotiation.getUuid(),
						NotificationConstants.NotificationType.PROCESS_TIME_EVENT,
						event.getName(), event.getTime());
			}

			// schedule process specific participant time events
			for (TimeEvent event : negotiation.getParticipantTimeEvents()) {
				buildJob(scheduler,
						jobTriggers,
						negotiation.getUuid(),
						NotificationConstants.NotificationType.PROCESS_TIME_EVENT,
						event.getName(), event.getTime());
			}

			// trigger job
			if (jobTriggers != null && jobTriggers.size() > 0) {
				scheduler.scheduleJobs(jobTriggers, true);
				scheduler.start();
			}
		} catch (Exception e) {
			logger.error(
					"Failed to schedule job because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}",
					e.getMessage(), e.getStackTrace());
		}
	}

	private static void buildJob(Scheduler scheduler,Map<JobDetail, List<Trigger>> jobTriggers,String negotiationUUID, String eventType, String eventName, Date time) {

		List<Trigger> triggers = new ArrayList<Trigger>();
		if (time != null){
			for (int timeInHr : timeIntervals) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(time);
				calendar.add(timeUnit, -timeInHr);

				if (TimeProvider.now().before(calendar.getTime())) {
					TriggerKey triggerKey = getTriggerKey(negotiationUUID,eventType,eventName,timeInHr);
					SimpleTrigger trigger = (SimpleTrigger) newTrigger()
							.withIdentity(triggerKey)
							.startAt(calendar.getTime()).build();
					triggers.add(trigger);
				}
			}

			if (triggers != null && triggers.size() > 0) {
				JobDetail job = buildJobDetail(negotiationUUID,eventName,eventType);
				jobTriggers.put(job, triggers);
			}
		}
		else if (time == null){
			// remove time event
			JobKey jobKey = getJobJey(negotiationUUID,eventType,eventName);
			try {
				scheduler.deleteJob(jobKey);
			} catch (SchedulerException e) {
				logger.error(String.format("Failed to remove scheduled job %s on negotiation %s",eventName,negotiationUUID));
			}
		}
	}
	
}
