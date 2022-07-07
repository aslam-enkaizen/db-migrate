package com.exrade.runtime.kyc.job;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.models.messaging.Offer;
import com.exrade.platform.exception.ExException;
import com.exrade.runtime.kyc.KycManager;
import com.exrade.runtime.message.IMessageManager;
import com.exrade.runtime.message.MessageManager;
import com.exrade.runtime.rest.RestParameters.KycFilters;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public class KycJob extends ExradeJob implements Job  {

	private static Logger logger = ExLogger.get();

	private KycManager kycManager = KycManager.getInstance();

	private IMessageManager messageManager = new MessageManager();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("Executing KYC Job: {} with Trigger: {}", context.getJobDetail().getKey(), context.getTrigger().getKey());

		JobDataMap data = context.getJobDetail().getJobDataMap();

		try{
			Offer offer = (Offer)messageManager.readByUUID((String)data.get(KycFilters.OFFER_UUID));
			if(offer != null) {
				if(!offer.isDraft())
					kycManager.checkKyc(offer);
				else
					logger.warn("Ignored KYC checking for draft offer: {}", offer.getUuid());
			}
		}
		catch(ExException ex){
			logger.warn("KYC Job Failed! {}", ex.getMessage());
		}
		catch(Exception ex){
			logger.warn("KYC Job Failed!", ex);
		}

		logger.info("Finished Executing KYC Job: {} with Trigger: {}", context.getJobDetail().getKey(), context.getTrigger().getKey());
	}

}
