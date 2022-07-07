package com.exrade.models.common;

import com.exrade.platform.persistence.BaseEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultilangItem extends BaseEntity {

	private String value;
	
	private Map<String, String> valueTranslations = new LinkedHashMap<>();

	public static MultilangItem newInstance(final MultilangItem multilangItem) {
		MultilangItem newMultilangItem = new MultilangItem();
		newMultilangItem.setValue(multilangItem.getValue());
		newMultilangItem.setValueTranslations(multilangItem.getValueTranslations());
		return newMultilangItem;
	}
	
	public static List<MultilangItem> newInstance(final List<MultilangItem> multilangItemList) {
		final List<MultilangItem>  newMultilangItemList = new ArrayList<>();
		for (MultilangItem multilangItem : multilangItemList) {
			newMultilangItemList.add(newInstance(multilangItem));
		}
		return newMultilangItemList;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Map<String, String> getValueTranslations() {
		return valueTranslations;
	}

	public void setValueTranslations(Map<String, String> valueTranslations) {
		this.valueTranslations = valueTranslations;
	}

	public MultilangItem addTranslation(String iVisibleValue, String language) {
		getValueTranslations().put(language, iVisibleValue);
		return this;
	}
	
}
