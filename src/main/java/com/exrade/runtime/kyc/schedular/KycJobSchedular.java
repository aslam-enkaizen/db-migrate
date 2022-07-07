package com.exrade.runtime.kyc.schedular;

import com.exrade.core.ExLogger;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExScheduleJobException;
import com.exrade.runtime.kyc.job.KycJob;
import com.exrade.runtime.rest.RestParameters.KycFilters;
import com.exrade.runtime.timer.ExScheduler;
import com.exrade.runtime.timer.TimeProvider;
import org.quartz.*;
import org.slf4j.Logger;

import java.util.Calendar;

public class KycJobSchedular extends ExScheduler {

	private static Logger logger = ExLogger.get();
	
	public <T> void schedule(String negotiationUUID, String offerUUID){
		logger.info("Scheduling KYC Job Negotiation: {}, Offer: {}", negotiationUUID, offerUUID);
		
		
		try {
			Scheduler scheduler = getScheduler();

			JobDataMap dataMap = new JobDataMap();
			dataMap.put(KycFilters.NEGOTIATION_UUID, negotiationUUID);
			dataMap.put(KycFilters.OFFER_UUID, offerUUID);

			JobDetail job = JobBuilder.newJob(KycJob.class)
					.withIdentity(getJobJey(negotiationUUID, offerUUID))
					.usingJobData(dataMap)
					.build();

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(TimeProvider.now());
			calendar.add(Calendar.SECOND, 30);
			SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
					.withIdentity(getTriggerKey(negotiationUUID,offerUUID))
					.startAt(calendar.getTime())
					.build();

			scheduler.scheduleJob(job, trigger);
			scheduler.start();

			logger.info("Scheduler started for the KYC job  {} with trigger {}", job.getKey(), trigger.getKey());
		} catch (Exception e) {
			logger.error("Failed to schedule KYC job because of the error - ExceptionMessage: {}, ExceptionStackTrace: {}", e.getMessage(),
					e.getStackTrace());
			throw new ExScheduleJobException(ErrorKeys.JOB_SCHEDULE_FAIL, e);
		}
		
	}
	
	private String getJobJey(String clientReference, String callReference){
		return "KycJobKey:" + clientReference + ":" + callReference;
	}
	
	private String getTriggerKey(String clientReference, String callReference){
		return "KycJobTrigger:" + clientReference + ":" + callReference;
	}
	
}
