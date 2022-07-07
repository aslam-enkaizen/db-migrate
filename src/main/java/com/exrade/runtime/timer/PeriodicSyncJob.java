package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.runtime.asset.integration.ServitlyIntegrationManager;
import com.exrade.runtime.integration.IntegrationSettingManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public class PeriodicSyncJob extends ExradeJob implements Job {

	private static Logger logger = ExLogger.get();
	ServitlyIntegrationManager servitlyIntegrationManager;
	IntegrationSettingManager integrationSettingManager = new IntegrationSettingManager();

	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {

		logger.info("PeriodicSyncJob started.");
		try{
			setupContext(null);

			//TODO: parallel synchronization

			// Servitly asset synchronization
			/*
			 * List<IntegrationSetting> integrationSettings =
			 * integrationSettingManager.getIntegrationSettings(IntegrationServiceType.
			 * SERVITLY_SERVITIZATION); for(IntegrationSetting integrationSetting :
			 * integrationSettings) { servitlyIntegrationManager = new
			 * ServitlyIntegrationManager(integrationSetting.getProfile().getUuid());
			 * servitlyIntegrationManager.syncAssets(); }
			 */

		}
		catch(Exception ex){
			logger.warn("PeriodicSyncJob failed!", ex);
		}

		logger.info("PeriodicSyncJob finished.");
	}
}
