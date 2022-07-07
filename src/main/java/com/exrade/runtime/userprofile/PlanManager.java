package com.exrade.runtime.userprofile;

import com.exrade.models.userprofile.Plan;
import com.exrade.models.userprofile.User;
import com.exrade.models.userprofile.security.PlatformRole;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.exception.ExNotFoundException;
import com.exrade.platform.persistence.PersistentManager;
import com.exrade.platform.persistence.query.IQuery;
import com.exrade.platform.persistence.query.OrientSqlBuilder.QueryParameters;
import com.exrade.platform.persistence.query.QueryFilters;
import com.exrade.platform.persistence.query.SimpleOQuery;
import com.exrade.platform.security.Security;
import com.exrade.runtime.payment.StripeManager;
import com.exrade.runtime.rest.RestParameters;
import com.exrade.runtime.rest.RestParameters.PlanFields;
import com.exrade.runtime.userprofile.persistence.query.PlanQuery;
import com.exrade.util.ContextHelper;
import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.util.List;

public class PlanManager {

	private PersistentManager persistentManager;

	private static final PlanManager INSTANCE = new PlanManager();

	private PlanManager() {
		this(new PersistentManager());
	}

	public PlanManager(PersistentManager iPersistentManager) {
		persistentManager = iPersistentManager;
	}

	public static PlanManager getInstance() {
		return INSTANCE;
	}

	public List<Plan> getPlans(QueryFilters iFilters){
		if (iFilters.isNull(QueryParameters.SORT)){
			iFilters.put(QueryParameters.SORT,PlanFields.GRADE);
		}
		return persistentManager.listObjects(new PlanQuery(), iFilters);
	}

	public Plan create(Plan iPlan) {
		Security.checkPlatformRole((User)ContextHelper.getMembership().getUser(), PlatformRole.SUPERADMIN);
		if(Strings.isNullOrEmpty(iPlan.getOfferID()) && !(iPlan.getAmount() == null || iPlan.getAmount().compareTo(BigDecimal.ZERO) == 0)){
			try {
				iPlan.setOfferID(StripeManager.getInstance().createPlan(iPlan));
			} catch (Exception e) {
				throw new ExException("Error creating plan", e);
			}
		}
		Plan createdPlan = persistentManager.create(iPlan);
		return createdPlan;
	}

	public Plan findByName(String iPlanName) {
		Plan plan = null;
		if (iPlanName != null){
			IQuery query = new SimpleOQuery<>(Plan.class).eq(PlanFields.NAME, iPlanName).getQuery();
			plan = persistentManager.readObject(query);
		}
		return plan;
	}

	public Plan findByUUID(String iPlanUUID) {
		Plan plan = null;
		if (iPlanUUID != null){
			IQuery query = new SimpleOQuery<>(Plan.class).eq(RestParameters.UUID, iPlanUUID).getQuery();
			plan = persistentManager.readObject(query);
		}
		return plan;
	}

	public Plan findByNameOrUUID(String planNameOrUUID) {
		Plan plan = findByName(planNameOrUUID);

		if(plan == null)
			plan = findByUUID(planNameOrUUID);

		return plan;
	}

	public Plan update(Plan iPlan){
		Security.checkPlatformRole((User)ContextHelper.getMembership().getUser(), PlatformRole.SUPERADMIN);
		//todo commented
//		throw new UnsupportedOperationException();
		return persistentManager.update(iPlan);
	}

	public void delete(String iPlanUUID){
		Security.checkPlatformRole((User)ContextHelper.getMembership().getUser(), PlatformRole.SUPERADMIN);
		Plan plan = findByUUID(iPlanUUID);

		if(plan == null)
			throw new ExNotFoundException("Plan not found");
		if(plan.isDefaultPlan())
			throw new ExException("Deleting default plan is not allowed");

		persistentManager.delete(plan);
	}

	/*public List<PlanLiveOffers> getPlansLiveOffers(QueryFilters iFilters) {
		List<Plan> plans = getPlans(iFilters);
		List<PlanLiveOffers> planLiveoffers = new ArrayList<>();
		for (Plan plan : plans) {
			planLiveoffers.add(new PlanLiveOffers(plan));
		}
		return planLiveoffers;
	}*/
}
