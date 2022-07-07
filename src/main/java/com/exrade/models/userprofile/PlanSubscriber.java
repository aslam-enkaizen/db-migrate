package com.exrade.models.userprofile;

public interface PlanSubscriber {
	
	public PlanSubscription getPlanSubscription();
	
	/**
	 * Set subscribed plan
	 */
	public void setPlanSubscription(PlanSubscription planSubscription);
	
}