package com.exrade.runtime.timer;

import java.util.Date;

/**
 * This class responsible to provide current time from configured time server.\
 * Configured time server could be local machine or any external server
 * @author john
 *
 */
public class TimeProvider {

	/**
	 * Returns current time from configured time server
	 * If no configuration is present, it returns time from local machine
	 * @return
	 */
	public static Date now(){
		//TODO: add functiaonality to fetch time from external time server
		return new Date();
	}
	
}
