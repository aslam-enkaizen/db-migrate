package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.runtime.alert.AlertManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public class PeriodicAlertGeneratorJob extends ExradeJob implements Job {

	private static Logger logger = ExLogger.get();

	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		
		logger.info("Generating periodic alerts.");
		try{
			setupContext(null);
			AlertManager alertManager = new AlertManager();
			alertManager.generateAlerts();
		}
		catch(Exception ex){
			logger.warn("Periodic alerts generation failed!", ex);
		}
		
		logger.info("Periodic alerts generation finished.");
	}
}
