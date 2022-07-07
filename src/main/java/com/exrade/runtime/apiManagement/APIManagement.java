package com.exrade.runtime.apiManagement;

/**
 * @author jasonfinnegan
 * This interface holds methods for managing access to the eXrade API
 * Through this we will configure the unique space for a specific Application
 * This will allow selection of user Identity model, select what information models user can select,
 * This Interface may also implement features related to pricing
 * 
 * This will specify what second level users are able to do , whether new Info models are allowed 
 * or a specified preset only.
 * 
 * 
 * 
 */
public interface APIManagement {
	
	//enable anonymous reading of negotiation Descriptions
	public boolean enableAnonymousRead(boolean allow);
	
}
