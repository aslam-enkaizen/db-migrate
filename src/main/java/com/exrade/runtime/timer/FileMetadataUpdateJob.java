package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public class FileMetadataUpdateJob extends ExradeJob implements Job {

	private static Logger logger = ExLogger.get();

	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		
		logger.info("FileMetadataUpdateJob started.");
		try{
			setupContext(null);
			//FileMetadataUpdater fileMetadaUpdater = new FileMetadataUpdater();
			//fileMetadaUpdater.update();
			
			//ContractGenerator contractGenerator = new ContractGenerator();
			//contractGenerator.update();
		}
		catch(Exception ex){
			logger.warn("FileMetadataUpdateJob failed!", ex);
		}
		
		logger.info("FileMetadataUpdateJob finished.");
	}
}
