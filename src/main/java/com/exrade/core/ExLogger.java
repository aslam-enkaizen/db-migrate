package com.exrade.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExLogger {

	private static final Logger logger = LoggerFactory.getLogger("application");

	/**
	 * Return the default logger
	 * @return
	 */
	public static Logger get(){
		return logger;
	}

	/**
	 * If for particular needs you need to use a dedicated new context logger, use this 
	 * method providing an unique identifier.
	 * @param name
	 * @return new Logger context
	 */
	public static Logger of(String name){
		return LoggerFactory.getLogger("application."+name);
	}


}
