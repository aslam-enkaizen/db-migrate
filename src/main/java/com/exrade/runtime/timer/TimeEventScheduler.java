package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExScheduleJobException;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.NegotiationFilters;
import com.exrade.util.ContextHelper;
import org.quartz.*;
import org.slf4j.Logger;

import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class TimeEventScheduler extends ExScheduler{

	private static Logger logger = ExLogger.get();

	public void schedule(String negotiationUUID, String userUUID, String transitionName, String eventName, Date eventTime) {
		//TODO instead of having scheduler specific to per user, change into per negotiation
		if(eventTime == null){
			logger.warn("Cannot schedule task as date is null. NegotiationUUID: {}, UserUUID: {}, TransitionName: {}, EventName: {}", 
											negotiationUUID, userUUID, transitionName, eventName);
			return;
		}
		try {
			Scheduler scheduler = getScheduler();

			JobDataMap dataMap = new JobDataMap();
			dataMap.put(NegotiationFilters.NEGOTIATION_UUID, negotiationUUID);
			dataMap.put(ContextHelper.MEMBERSHIP_UUID, userUUID);
			dataMap.put(RestParameters.NegotiationStatusFilters.OPTION, transitionName);
			// dataMap.put("EventName", eventName);
			// dataMap.put("EventTime", eventTime);

			JobDetail job = newJob(TimeEventJob.class)
					.withIdentity(getJobJey(negotiationUUID,userUUID,transitionName,eventName))
					.usingJobData(dataMap)
					.build();

			SimpleTrigger trigger = (SimpleTrigger) newTrigger()
					.withIdentity(getTriggerKey(negotiationUUID,userUUID,transitionName,eventName))
					.startAt(eventTime)
					.build();

			logger.info("Scheduling job {} with trigger {}", job.getDescription(), trigger.getDescription());

			scheduler.scheduleJob(job, trigger);
			scheduler.start();

			logger.info("Scheduler started for the job  {} with trigger {}", job.getDescription(), trigger.getDescription());
		} catch (Exception e) {
			logger.error("Failed to schedule job because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}", e.getMessage(),
					e.getStackTrace());
			throw new ExScheduleJobException(ErrorKeys.JOB_SCHEDULE_FAIL, e);
		}

	}
	
	private String getJobJey(String negotiationUUID, String userUUID, String transitionName, String eventName){
		return "job:" + negotiationUUID + ":" + userUUID + ":" + transitionName + ":" + eventName;
	}
	
	private String getTriggerKey(String negotiationUUID, String userUUID, String transitionName, String eventName){
		return "trigger:" + negotiationUUID + ":" + userUUID + ":" + transitionName + ":" + eventName;
	}
	
	public void deleteScheduledJob(String negotiationUUID, String userUUID, String transitionName, String eventName){
		JobKey jobKey = new JobKey(getJobJey(negotiationUUID,userUUID,transitionName,eventName),null);
		Scheduler scheduler = getScheduler();
		
		try {
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			throw new ExScheduleJobException(String.format("Failed to remove job: %s on negotiation ",eventName,negotiationUUID));
		}
	}
	
	public void updateScheduledJob(String negotiationUUID, String userUUID, String transitionName, String eventName, Date eventTime){
		JobKey jobKey = new JobKey(getJobJey(negotiationUUID,userUUID,transitionName,eventName),null);
		TriggerKey triggerKey = new TriggerKey(getTriggerKey(negotiationUUID,userUUID,transitionName,eventName),null);
		Scheduler scheduler = getScheduler();
		
		JobDetail jobDetail = null;
		try {
			jobDetail = scheduler.getJobDetail(jobKey);
		} catch (SchedulerException e) {
			throw new ExScheduleJobException("Failed to load job details");
		}
		if (jobDetail != null){
			updateScheduledJob(jobDetail,triggerKey,eventTime);
		}
		else {
			schedule(negotiationUUID, userUUID, transitionName, eventName, eventTime);
		}
	}
}
