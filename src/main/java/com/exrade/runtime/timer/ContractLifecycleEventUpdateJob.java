package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import com.exrade.core.ExradeJob;
import com.exrade.models.contract.Contract;
import com.exrade.models.contract.ContractLifecycleEvent;
import com.exrade.models.contract.ContractStatus;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.runtime.contract.ContractManager;
import com.exrade.runtime.rest.RestParameters.ContractFields;
import com.exrade.util.DateUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ContractLifecycleEventUpdateJob extends ExradeJob implements Job {

	private static Logger logger = ExLogger.get();

	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {

		logger.info("ContractLifecycleEventUpdateJob");
		try{
			setupContext(null);

			Date now = TimeProvider.now();
			ContractManager contractManager = new ContractManager();
			QueryFilters filters = QueryFilters.create(ContractFields.EFFECTIVE_DATE, DateUtil.toBeginningOfTheDay(now));
			filters.put(ContractFields.STATUS, ContractStatus.ACTIVE_FUTURE);
			filters.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
			List<Contract> contracts = contractManager.listContracts(filters);
			for(Contract contract : contracts) {
				contractManager.updateContractStatusAndCurrentExecutionDuration(contract, now);
				contractManager.updateContract(contract);
			}

			filters = QueryFilters.create(ContractFields.EXPIRY_DATE, DateUtil.addWithCurrentDate(Calendar.DAY_OF_MONTH, -1, false));
			filters.put(QueryParameters.PER_PAGE, Integer.MAX_VALUE);
			contracts = contractManager.listContracts(filters);
			for(Contract contract : contracts) {
				logger.info("Generating next lifecycle event for Contract: {}", contract.getUuid());
				ContractLifecycleEvent nextEvent = contractManager.nextRenewalEvent(contract, now);
				if(nextEvent != null) {
					contract.getLifecycleEvents().add(nextEvent);
				}
				else {
					contractManager.createEndEvent(contract, now);
				}
				contractManager.updateContractStatusAndCurrentExecutionDuration(contract, now);
				contractManager.updateContract(contract);
			}
		}
		catch(Exception ex){
			logger.warn("ContractLifecycleEventUpdateJob failed!", ex);
		}

		logger.info("ContractLifecycleEventUpdateJob finished.");
	}
}
