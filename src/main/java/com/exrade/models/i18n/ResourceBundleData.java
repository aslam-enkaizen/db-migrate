package com.exrade.models.i18n;

import com.exrade.platform.persistence.IPersistenceUUID;
import com.exrade.util.ObjectsUtil;

import javax.persistence.Id;
import javax.persistence.Version;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResourceBundleData implements IPersistenceUUID {
	
	@Id
	private String id;

	@Version
	private Integer version;

	private String uuid = ObjectsUtil.generateUniqueID();
	
	private String localizedEntityUUID;
	
	private String country;

	private String language;

	private String variant;
	
	private Map<String,String> keyToLocalizedValue = new HashMap<>();
	
	public String getId() {
		return id;
	}

	public Integer getVersion() {
		return version;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public ResourceBundleData(){}
	
	public static ResourceBundleData createResourceBundleData(String iCountry,String iLanguage, String iVariant,String iLocalizedEntityUUID){
		ResourceBundleData resourceBundleData = new ResourceBundleData(); 
		resourceBundleData.country = iCountry;
		resourceBundleData.language = iLanguage;
		resourceBundleData.variant = iVariant;
		resourceBundleData.localizedEntityUUID = iLocalizedEntityUUID;
		return resourceBundleData;
	}
	
	public static ResourceBundleData createResourceBundleData(Locale iLocale,String iLocalizedEntityUUID){
		return createResourceBundleData(iLocale.getCountry(),iLocale.getLanguage(),iLocale.getVariant(),iLocalizedEntityUUID);
	}
	
	public static ResourceBundleData createResourceBundleData(String iLanguageTag,String iLocalizedEntityUUID){
		return createResourceBundleData(Locale.forLanguageTag(iLanguageTag),iLocalizedEntityUUID);
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getLocalizedEntityUUID() {
		return localizedEntityUUID;
	}

	public Map<String,String> getKeyToLocalizedValue() {
		return keyToLocalizedValue;
	}
	
	public String getKeyToLocalizedValue(String key) {
		return getKeyToLocalizedValue().get(key);
	}

	public void setKeyToLocalizedValue(Map<String,String> keyToLocalizedValue) {
		this.keyToLocalizedValue = keyToLocalizedValue;
	}

	public void addReplaceKeyToLocalizedValue(String iKey,String iLocalizedValue){
		getKeyToLocalizedValue().put(iKey, iLocalizedValue);
	}
	
	
}
