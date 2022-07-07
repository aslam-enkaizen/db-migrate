package com.exrade.util;

import com.exrade.Messages;
import com.exrade.models.i18n.ExLang;

import java.util.HashMap;
import java.util.Map;

public class MultiLanguageUtil {

	/**
	 * Retrieves localized message with default language
	 * @param key
	 * 				unique key of the message
	 * @return
	 */
	public static String getLabel(String key) {
		return getLabelReplaced(key, null);
	}
	
	/**
	 * Retrieves localized message for specified language
	 * @param key
	 * 				unique key of the message
	 * @param iLanguage
	 * @return
	 */
	public static String getLabel(String key, String iLanguage) {
		return getLabelReplaced(key, null, iLanguage);
	}
	
	/**
	 * Retrieves localized message for the key and context and replace specified
	 * values
	 * 
	 * @param key
	 *            unique key of the message
	 * @param replaceValues
	 *            values to be replaced
	 * @return
	 */
	public static String getLabelReplaced(String key, Map<String,String> replaceValues) {
		String message = Messages.get(key);
		if (message != null && replaceValues != null
				&& replaceValues.size() > 0) {
			for (String marker : replaceValues.keySet()) {
				message = message.replace(marker, replaceValues.get(marker));
			}
		}
		return message;
	}
	
	/**
	 * Retrieves localized message for the key and context and replace specified
	 * values
	 * 
	 * @param key
	 *            unique key of the message
	 * @param replaceValues
	 *            values to be replaced
	 * @param iLanguage
	 *            language
	 * @return
	 */
	public static String getLabelReplaced(String key, Map<String,String> replaceValues,String iLanguage) {
		//todo update lang
		String message = Messages.get("Lang.forCode(iLanguage)",key);
		if (message != null && replaceValues != null
				&& replaceValues.size() > 0) {
			for (String marker : replaceValues.keySet()) {
				message = message.replace(marker, replaceValues.get(marker));
			}
		}
		return message;
	}
	
	/**
	 * Retrieves all localized messages for the given key
	 * @param key
	 * @return
	 */
	public static Map<String, String> getAllLabels(String key) {
		Map<String, String> localizedMessages=new HashMap<String, String>();
		
		for(ExLang lang : ExLang.all){
			localizedMessages.put(lang.getCode(), MultiLanguageUtil.getLabel(key, lang.getCode()));
		}
		
		return localizedMessages;
	}
}
