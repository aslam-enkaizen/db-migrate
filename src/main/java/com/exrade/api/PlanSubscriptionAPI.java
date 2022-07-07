package com.exrade.api;

import com.exrade.core.ExRequestEnvelope;
import com.exrade.models.userprofile.Plan;

import java.util.List;
import java.util.Map;

public interface PlanSubscriptionAPI {

	List<Plan> getPlans(ExRequestEnvelope request, Map<String, String> iFilters);
	Plan createPlan(ExRequestEnvelope request, Plan iPlan);
	Plan updatePlan(ExRequestEnvelope request, Plan iPlan);
	void deletePlan(ExRequestEnvelope request, String iPlanUUID);
	//List<PlanLiveOffers> getPlansLiveOffers(ExRequestEnvelope request, Map<String, String> iFilters);
	
}
