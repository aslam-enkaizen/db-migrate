package com.exrade.runtime.kyc.schedular;

import com.exrade.core.ExLogger;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExScheduleJobException;
import com.exrade.runtime.kyc.job.CallbackHandlerJob;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.KycFields;
import com.exrade.runtime.rest.RestParameters.KycFilters;
import com.exrade.runtime.timer.ExScheduler;
import com.exrade.runtime.timer.TimeProvider;
import org.quartz.*;
import org.slf4j.Logger;

import java.util.Calendar;

public class CallbackHandlerJobScheduler extends ExScheduler {

	private static Logger logger = ExLogger.get();
	
	public <T> void schedule(String clientReference, String callReference, String callbackData){
		logger.info("Scheduling KYC CallbackHandler Job ClientReference: {}, CallReference: {}", clientReference, callReference);
		
		
		try {
			Scheduler scheduler = getScheduler();

			JobDataMap dataMap = new JobDataMap();
			dataMap.put(KycFields.SERVICE_CALL_REFERENCE, callReference);
			dataMap.put(KycFilters.CLIENT_REFERENCE, clientReference);
			dataMap.put(RestParameters.DATA, callbackData);

			JobDetail job = JobBuilder.newJob(CallbackHandlerJob.class)
					.withIdentity(getJobJey(clientReference,callReference))
					.usingJobData(dataMap)
					.build();

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(TimeProvider.now());
			calendar.add(Calendar.MINUTE, 2);
			SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
					.withIdentity(getTriggerKey(clientReference,callReference))
					.startAt(calendar.getTime())
					.build();

			scheduler.scheduleJob(job, trigger);
			scheduler.start();

			logger.info("Scheduler started for the job  {} with trigger {}", job.getKey(), trigger.getKey());
		} catch (Exception e) {
			logger.error("Failed to schedule job because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}", e.getMessage(),
					e.getStackTrace());
			throw new ExScheduleJobException(ErrorKeys.JOB_SCHEDULE_FAIL, e);
		}
		
	}
	
	private String getJobJey(String clientReference, String callReference){
		return "CallbackHandlerJob:" + clientReference + ":" + callReference;
	}
	
	private String getTriggerKey(String clientReference, String callReference){
		return "CallbackHandlerTrigger:" + clientReference + ":" + callReference;
	}
	
}
