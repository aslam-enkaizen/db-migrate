package com.exrade.runtime.template;

import java.util.Map;

/**
 *
 * @author john
 *
 */
public interface ITemplateProcessor {

	/***
	 * Process the templateContent using the dataMap and returns byte array
	 * @param dataMap data as key value pair
	 * @param templateContent body of the template
	 * @return byte array of processed template
	 * @throws Exception
	 */
	byte[] process(Map<String, Object> dataMap,	String templateContent) throws Exception;

	/***
	 *
	 * @param dataMap
	 * @param templateBaseDir
	 * @param templateSet
	 * @param templateName
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	//byte[] processTemplate(Map<String, Object> dataMap, String templateBaseDir, String templateSet,String templateName, String locale) throws Exception;

	/***
	 * Process the template with specified data using templateBaseDir as base dir.
	 * It resolves actual file by concatenating templateBaseDir with templateSet, locale and templateFile
	 * @param dataMap data as key value pair
	 * @param templateBaseDir base directory of the the template
	 * @param templateSet directory containing template file(s)
	 * @param templateFile name of the template file. deafult is default.ftl
	 * @param locale language of the template
	 * @return
	 * @throws Exception
	 */
	byte[] processTemplate(Map<String, Object> dataMap, String templateBaseDir, String templateSet, String templateFile, String locale) throws Exception;

	/***
	 * Process the template with specified data using templateBaseDir as base dir.
	 * It resolves actual file by concatenating templateBaseDir with templateSet, locale and templateFile
	 * @param dataMap data as key value pair
	 * @param templateBaseDir base directory of the the template
	 * @param templateSet directory containing template file(s)
	 * @param templateFile name of the template file. deafult is default.ftl
	 * @param locale language of the template
	 * @return
	 * @throws Exception
	 */
	byte[] processTemplateForPdf(Map<String, Object> dataMap, String templateBaseDir, String templateSet, String templateFile, String locale) throws Exception;
	
}