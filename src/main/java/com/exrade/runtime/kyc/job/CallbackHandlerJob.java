package com.exrade.runtime.kyc.job;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.runtime.kyc.KycManager;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.util.JSONUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public class CallbackHandlerJob extends ExradeJob implements Job  {

	private static Logger logger = ExLogger.get();
	
	private KycManager kycManager = KycManager.getInstance();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("Executing CallbackHandlerJob: {} with Trigger: {}", context.getJobDetail().getKey(), context.getTrigger().getKey());
		
		JobDataMap data = context.getJobDetail().getJobDataMap();
		
		try{
			kycManager.handleCallback(JSONUtil.toJsonNode((String)data.get(RestParameters.DATA)));
		}
		catch(Exception ex){
			logger.warn("CallbackHandlerJob Failed!", ex);
			throw ex;
		}
		
		logger.info("Finished Executing CallbackHandlerJob: {} with Trigger: {}", context.getJobDetail().getKey(), context.getTrigger().getKey());
	}

}
