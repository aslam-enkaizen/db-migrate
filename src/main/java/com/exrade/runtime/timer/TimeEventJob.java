package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExScheduleJobException;
import com.exrade.runtime.negotiation.NegotiationManager;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.NegotiationFilters;
import com.exrade.util.ContextHelper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public class TimeEventJob extends ExradeJob implements Job {

	private static Logger logger = ExLogger.get();
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();

		String negotiationUUID = data.getString(NegotiationFilters.NEGOTIATION_UUID);
		String userUUID = data.getString(ContextHelper.MEMBERSHIP_UUID);
		String transitionName = data.getString(RestParameters.NegotiationStatusFilters.OPTION);
		setupContext(userUUID);
		logger.info("Executing scheduled transition with params - Transition: {}, NegotiationID: {}, UserID: {}.", transitionName, negotiationUUID, userUUID);
		
		NegotiationManager negotiationManager = new NegotiationManager();
		
		try{
			if(negotiationManager.fireSchedulerGeneratedTransition(userUUID, negotiationUUID, transitionName))
				logger.info("Scheduled transition succedded.");
			else
				logger.info("Scheduled transition failed.");			
		}
		catch(Exception e){
			logger.error("Scheduled transition failed because of the error - {}", e.getMessage(), e);
			throw new ExScheduleJobException(ErrorKeys.JOB_EXECUTION_FAIL, e);
		}
	}

}
