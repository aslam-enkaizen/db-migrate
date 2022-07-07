package com.exrade.core;

import java.util.List;

public interface FieldsAvailable {

	/**
	 * Valid fields valids for serialization 
	 * @return
	 */
	public List<String> getValidFields();
	
	/**
	 * Valid fields selected by default
	 * @return
	 */
	public List<String> getDefaultFields();

	/**
	 * Option fields available upon request by client
	 * @return
	 */
	public List<String> getExpandFields();
	
	/**
	 * type of the entity to serialize
	 * @return
	 */
	public Class<?> getType();
	
}
