package com.exrade.models.common;

import com.exrade.platform.persistence.IPersistence;
import com.exrade.util.ExCollections;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.persistence.Id;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
/**
 * This class represent a subset of json-chema specification that can be found at
 *  http://json-schema.org/draft-03/schema#.
 * @author carlo.polisini
 *
 */
@JsonInclude(Include.NON_EMPTY)
public class Meta implements IPersistence {

	@Id
	protected String id;

	@Version
	protected Integer version;

	private String type;

	private boolean required;

	private String format;

	private String defaultValue;

	private String description;

	private BigDecimal minimum;

	private BigDecimal maximum;

	private Integer minLength;

	private Integer maxLength;

	private String widget;

	private String formula; // Excel like formula to calculate value automatically

	private String cellId; // Cell identifier (e.g., A1, B1 etc.) for the evaluation of excel like formula

	private String condition; // Excel like formula to make the it optional or required

	private Map<String,Meta> properties = new HashMap<>();

	private JsonType jsonType;

	private boolean requiredByProcess;
	
	private boolean isFinalAmount;

	/**
	 * List of allowed values for the field, i.e. an option list, corresponds to
	 * {@code enum} property in Json-schema but {@code enum} keyword is not
	 * allowed in java as property name
	 */
	private List<MultilangItem> allowedValues = new ArrayList<>();

	private boolean allowMultiple;

	/**	Allowed operation on the field, possible values are SET,ADD,REMOVE  */
	private transient Set<String> operations = new HashSet<>();

	private Items items;

	public Meta(){}

	public static Meta newInstance(JsonType iJsonType){
		Meta meta = new Meta();
		meta.jsonType = iJsonType;
		meta.type = iJsonType.getType();
		meta.format = iJsonType.getFormat();
		meta.widget = iJsonType.getWidget();
		return meta;
	}

	public static Meta newInstance(){
		return new Meta();
	}

	public static Meta newInstance(Meta iMeta){
		Meta meta = new Meta();
		meta.type = iMeta.getType();
		meta.required = iMeta.isRequired();
		meta.operations = new HashSet<>(iMeta.getOperations());
		meta.defaultValue = iMeta.getDefaultValue();
		meta.format = iMeta.getFormat();
		meta.description = iMeta.getDescription();
		meta.minimum = iMeta.getMinimum();
		meta.maximum = iMeta.getMaximum();
		meta.description = iMeta.getDescription();
		meta.widget = iMeta.getWidget();

		if(ExCollections.isNotEmpty(iMeta.getProperties())) {
			for(Entry<String, Meta> item : iMeta.getProperties().entrySet()) {
				meta.getProperties().put(item.getKey(), Meta.newInstance(item.getValue()));
			}
		}
		meta.allowedValues = MultilangItem.newInstance(iMeta.getAllowedValues());
		meta.allowMultiple = iMeta.isAllowMultiple();
		meta.jsonType = iMeta.getJsonType();
		meta.cellId = iMeta.getCellId();
		meta.formula = iMeta.getFormula();
		meta.condition = iMeta.getCondition();
		meta.maxLength = iMeta.getMaxLength();
		meta.minLength = iMeta.getMinLength();
		meta.requiredByProcess = iMeta.isRequiredByProcess();
		meta.isFinalAmount = iMeta.isFinalAmount();
		// deep clone not needed because Items is immutable
		meta.items = iMeta.getItems();

		return meta;
	}

	/*
	 * Allows simple creation of Enumeration where value=label
	 */
	public static Meta newChoiceMeta(String[] options, String language){
		Meta meta =  Meta.newInstance(JsonType.ARRAY);
		meta.setWidget(Widget.OPTIONS);

		for(String option:options){
			meta.addOptionItem(option, language);
		}
		return meta;
	}

	/**
	 * Allows simple creation of Enumeration where oddd elements are value and even are names
	 */
	public static Meta newChoiceValueNameMeta(String[] options, String language){
		Meta meta =  Meta.newInstance(JsonType.ARRAY);
		meta.setWidget(Widget.OPTIONS);

		for (int i = 0; i/2 < options.length/2; i=i+2) {
			meta.addOptionItem(options[i], options[i+1], language);
		}

		return meta;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Integer getVersion() {
		return version;
	}

	public boolean isRequired() {
		return required;
	}

	public List<MultilangItem> getAllowedValues() {
		return allowedValues;
	}

	public void setAllowedValues(List<MultilangItem> allowedValues) {
		this.allowedValues = allowedValues;
	}

	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}

	public MultilangItem addOptionItem(String iValue, String iVisibleValue, String language) {
		Objects.requireNonNull(iValue);
		Objects.requireNonNull(iVisibleValue);
		Objects.requireNonNull(language);

		MultilangItem visibleValueItem = null;

		for(MultilangItem item : getAllowedValues()){
			if(iValue.equals(item.getValue())){
				visibleValueItem = item;
				break;
			}
		}

		if(visibleValueItem != null){
			visibleValueItem.getValueTranslations().put(language, iVisibleValue);
		}
		else{
			visibleValueItem = new MultilangItem();
			visibleValueItem.setValue(iValue);
			visibleValueItem.getValueTranslations().put(language, iVisibleValue);
			getAllowedValues().add(visibleValueItem);
		}

		return visibleValueItem;
	}

	public MultilangItem addOptionItem(String iValue, String language) {
		return addOptionItem(iValue, iValue, language);
	}

	public Set<String> getOperations() {
		return operations;
	}

	public Meta addOperation(String operation) {
		getOperations().add(operation);
		return this;
	}

	public Meta addOperations(Set<String> iOperations) {
		getOperations().addAll(iOperations);
		return this;
	}

	public Meta setOperations(Set<String> operations) {
		this.operations = operations;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public String getFormat() {
		return format;
	}

	public Meta setFormat(String format) {
		this.format = format;
		return this;
	}

	public BigDecimal getMinimum() {
		return minimum;
	}

	public Meta setMinimum(BigDecimal minimum) {
		this.minimum = minimum;
		return this;
	}

	public BigDecimal getMaximum() {
		return maximum;
	}

	public Meta setMaximum(BigDecimal maximum) {
		this.maximum = maximum;
		return this;
	}

	public String getType() {
		return type;
	}

	public Meta setType(String iType){
		type = iType;
		return this;
	}

	public Items getItems() {
		return items;
	}

	public Meta setItems(Items iItems) {
		items = iItems;
		return this;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public Meta setMinLength(Integer minLength) {
		this.minLength = minLength;
		return this;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public Meta setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public Meta setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public String getWidget() {
		return widget;
	}

	public Meta setWidget(String widget) {
		this.widget = widget;
		return this;
	}

	public Meta setRequired(boolean required) {
		this.required = required;
		return this;
	}

	public Meta setDescription(String description) {
		this.description = description;
		return this;
	}

	public Map<String,Meta> getProperties() {
		return properties;
	}

	public Meta addProperty(String iPropertyName,Meta iPropertyMeta) {
		getProperties().put(iPropertyName,iPropertyMeta);
		return this;
	}

	public Meta setProperties(Map<String,Meta> properties) {
		this.properties = properties;
		return this;
	}

	@JsonIgnore
	public JsonType getJsonType() {
		return jsonType;
	}

	public void setJsonType(JsonType jsonType) {
		this.jsonType = jsonType;
	}

	public boolean isRequiredByProcess() {
		return requiredByProcess;
	}

	public Meta setRequiredByProcess(boolean requiredByProcess) {
		this.requiredByProcess = requiredByProcess;
		return this;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public boolean isFinalAmount() {
		return isFinalAmount;
	}

	public void setFinalAmount(boolean isFinalAmount) {
		this.isFinalAmount = isFinalAmount;
	}

	public static class Items{
		private String type;

		public static Items newInstance(String iType){
			Items items = new Items();
			items.type = iType;
			return items;
		}

		public String getType() {
			return type;
		}
	}

//	public static class AllowedValue implements IPersistence {
//		private String key;
//		private String value;
//
//		public static AllowedValue newInstance(String iKey,String iValue){
//			AllowedValue allowedValue = new AllowedValue();
//			allowedValue.key = iKey;
//			allowedValue.value = iValue;
//			return allowedValue;
//		}
//
//		public String getKey() {
//			return key;
//		}
//
//		public String getValue() {
//			return value;
//		}
//
//	}

	public static class Operation{
		public final static String SET = "set";
		public final static String ADD_ITEM = "addItem";
		public final static String REMOVE_ITEM = "removeItem";
		public final static String SET_REQUIRED = "setRequired"; //Why this properties is here?? It should be in main class!
		public final static String SET_NEGOTIABLE = "setNegotiable"; //Why this properties is here?? It should be in main class!
		public final static String SET_REQUIRED_BY_PROCESS = "setRequiredByProcess"; //Why this properties is here?? It should be in main class!
		public final static String ADD = "add";
		public final static String REMOVE = "remove";
	}

	public enum JsonType{
		AMOUNT("number",null,Widget.AMOUNT,DataType.AMOUNT),
		ARRAY("array",null,Widget.OPTIONS,null),
		ATTACHMENT("string",Format.URI,Widget.ATTACHMENT,DataType.URL),
		BOOLEAN("boolean",null,null,DataType.BOOLEAN),
		DATE("string",Format.DATE,null,DataType.DATE),
		DATETIME("string",Format.DATETIME,null,DataType.DATETIME),
		NUMBER("number",null,null,DataType.NUMBER),
		IMAGE("string",Format.URI,Widget.IMAGE,DataType.URL),
		INTEGER("integer",null,null,DataType.INTEGER),
		TEXT("string","",null,DataType.TEXT),
		TEXTBOX("string","",Widget.TEXTAREA,DataType.TEXT),
		OBJECT("object",null,null,null),
		TIME("string",Format.TIME,null,DataType.TIME),
		DURATION("string",Format.DURATION,null,DataType.TEXT),
		URL("string",Format.URI,null,DataType.URL),
		RESOURCE_USER("string",Format.USER,null,DataType.RESOURCE),
		RESOURCE_NEGOTIATION("string",Format.NEGOTIATION,null,DataType.RESOURCE),
		RESOURCE_COMPANY("string",Format.COMPANY,null,DataType.RESOURCE),
		RESOURCE_NEGOTIATION_MESSAGE("string",Format.NEGOTIATION_MESSAGE,null,DataType.RESOURCE),
		LOCATION("object",null,Widget.LOCATION,null),
		ROUTE("object",null,Widget.ROUTE,null),
		EMAIL("string",Format.EMAIL,null,DataType.TEXT),
		PAYKEY("string",Format.PAYPAL_PAYKEY,null,null),
		PERCENT("number",Format.PERCENT,null,DataType.NUMBER),
		CLAUSE("clause",null,Widget.TEXTAREA,DataType.TEXT),;

		private final String type;
		private final String format;
		/** Schema.org corresponding datatype */
		private final DataType dataType;
		/** Default widget for that kind of Json schema */
		private final String widget;
		private final static Map<String,JsonType> nametoJsonType= new HashMap<>();

		static	{
			for (JsonType type : JsonType.values()) {
				nametoJsonType.put(type.name(),type);

			}
		}

		JsonType(String iType,String iFormat,String iWidget,DataType iDataType){
			type = iType;
			format = iFormat;
			widget = iWidget;
			dataType = iDataType;
		}

		public String getFormat() {	return format; }
		public String getType() { return type; }
		public String getWidget() { return widget; }
		public DataType getDataType() { return dataType; }
		public static JsonType getJsonTypeByName(String iName){
			return nametoJsonType.get(iName);
		}
	}

	public static class Format{
		public final static String DATETIME = "utc-millisec";
		public final static String DATE = "date";
		public final static String TIME = "time";
		public final static String EMAIL = "email";
		public final static String URI = "uri";
		public final static String PHONE = "phone";
		public final static String DURATION = "duration";
		public final static String USER = "user"; // format for user resource
		public final static String NEGOTIATION = "negotiation"; // format for negotiation resource
		public final static String COMPANY = "company"; // format for company resource
		public final static String NEGOTIATION_MESSAGE = "negotiation-message"; // format for negotiation message resource
		public final static String PAYPAL_PAYKEY="paypal_paykey";//format for linking to a paypal payment
		public final static String PERCENT = "percent";
	}

	public static class Widget{
		public final static String OPTIONS = "options";
		public final static String RADIOBUTTONS = "radiobuttons";
		public final static String CHECKBOX = "checkbox";
		public final static String CHECKBOXES = "checkboxes";
		public final static String TEXTAREA = "textarea";
		public final static String ATTACHMENT = "attachment";
		public final static String AMOUNT = "amount";
		public final static String IMAGE = "image";
		public final static String LOCATION = "location";
		public final static String ROUTE = "route";
	}

}
