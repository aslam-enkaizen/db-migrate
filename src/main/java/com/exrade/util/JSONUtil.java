package com.exrade.util;

import com.exrade.models.informationmodel.InformationModelCategory;
import com.exrade.models.userprofile.Membership;
import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExJSONException;
import com.exrade.runtime.rest.RestParameters;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.*;

public class JSONUtil {

	public final static Set<String> ignorableProxyField = Sets.newHashSet("id", "version", "handler");
	public final static Set<String> ignorableProxyHandler = Sets.newHashSet("handler");
	public final static Set<String> ignorableProxyFieldAndUUID = Sets.newHashSet("uuid","id", "version", "handler");

	public static JsonNode toJsonNode(Object obj) {
		if(obj == null)
			return NullNode.getInstance();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(obj);
	}

	public static JsonNode toJsonNode(String jsonText) {
		try{
			if(Strings.isNullOrEmpty(jsonText))
				return NullNode.getInstance();

			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(jsonText);
		}
		catch(Exception e){
			throw new ExJSONException(ErrorKeys.JSON_CANNOT_CONVERT_TO, e);
		}
	}

	/**
	 * Execute a plain JSON mapping with Jackson Mapper.
	 *
	 * @param obj
	 * @return String
	 * @throws ExJSONException
	 *             if an exception is raised by conversion process.
	 */
	public static String toJson(Object obj) {
		String json = "";
		try {
			json = new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new ExJSONException(ErrorKeys.JSON_CANNOT_CONVERT_TO, e);
		}
		return json;
	}

	/**
	 * Execute a plain JSON mapping with Jackson Mapper.
	 *
	 * @param obj
	 * @return String
	 * @throws ExJSONException
	 *             if an exception is raised by conversion process.
	 */
	public static String toJson(ObjectMapper customMapper,Object obj) {
		String json = "";
		try {
			json = customMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new ExJSONException(ErrorKeys.JSON_CANNOT_CONVERT_TO, e);
		}
		return json;
	}

	public static byte[] toBytes(JsonNode jsnoNode) {
		ObjectMapper om = new ObjectMapper();

		try {
			return om.writeValueAsBytes(jsnoNode);
		} catch (JsonProcessingException e) {
			return null;
		}

	}

	/**
	 * Execute a JSON mapping filtering out {@code id}, {@code version} and
	 * {@code handler} properties.
	 *
	 * @param obj
	 * @return String
	 * @throws ExJSONException
	 *             if an exception is raised by conversion process.
	 */
	public static String toJsonNoPersistence(Object obj) {
		ObjectMapper mapper = JSONUtil.getExcludeMapper(ignorableProxyField.toArray(new String[] {}));
		try {
			return mapper.writeValueAsString(obj);
		} catch (IOException e) {
			throw new ExJSONException(ErrorKeys.JSON_CANNOT_CONVERT_TO, e);
		}
	}

	/**
	 * Execute a JSON mapping filtering out {@code id}, {@code version} and
	 * {@code handler} properties.
	 *
	 * @param obj
	 * @return String
	 * @throws ExJSONException
	 *             if an exception is raised by conversion process.
	 */
	public static JsonNode toJsonExcludedPersistence(Object obj) {
		ObjectMapper mapper = JSONUtil.getExcludeMapper(ignorableProxyField.toArray(new String[] {}));
		return mapper.valueToTree(obj);
	}


	/**
	 * Execute a JSON mapping filtering out  uuid {@code id}, {@code version}
	 * {@code handler} properties.
	 *
	 * @param obj
	 * @return String
	 * @throws ExJSONException
	 *             if an exception is raised by conversion process.
	 */
	public static String toJsonNoPersistenceOrUUID(Object obj) {
		ObjectMapper mapper = JSONUtil.getExcludeMapper(ignorableProxyFieldAndUUID.toArray(new String[] {}));
		try {
			return mapper.writeValueAsString(obj);
		} catch (IOException e) {
			throw new ExJSONException(ErrorKeys.JSON_CANNOT_CONVERT_TO, e);
		}
	}

	/**
	 * Execute a JSON mapping filtering out {@code id}, {@code version} and
	 * {@code handler} properties.
	 *
	 * @param obj
	 * @return String
	 * @throws ExJSONException
	 *             if an exception is raised by conversion process.
	 */
	public static String toJsonExcludeHandler(Object obj) {
		ObjectMapper mapper = JSONUtil.getExcludeMapper(ignorableProxyHandler.toArray(new String[] {}));
		try {
			return mapper.writeValueAsString(obj);
		} catch (IOException e) {
			throw new ExJSONException(ErrorKeys.JSON_CANNOT_CONVERT_TO, e);
		}
	}


	public static JsonNode toJsonFieldsFiltered(Object objectToSerialize,List<String> fieldsParameters,Class<?> filteredClass) {
		JsonNode json = null;
		json = JSONUtil.getIncludeOnlyMapperForProxy(fieldsParameters, filteredClass).valueToTree(objectToSerialize);
		return json;
	}

	/**
	 * Return a new instance of Jackson Mapper with custom static strategies to
	 * properly serialize JSON Exrade entities.
	 *
	 * @see {@link ExradeModule}
	 * @return ObjectMapper
	 */
	public static ObjectMapper getExMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new ExradeModule());
		return mapper;
	}

	/**
	 * Return a JSON writer ignoring the serialization of the specified fields.
	 *
	 * @param ignorableFieldNames
	 *            array of property name to ignore
	 * @return ObjectMapper
	 */
	public static ObjectMapper getExcludeMapper(String[] ignorableFieldNames) {
		@JsonFilter("filter properties by name")
		class PropertyFilterMixIn {
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.addMixIn(Object.class, PropertyFilterMixIn.class);

		FilterProvider filters = new SimpleFilterProvider().addFilter("filter properties by name",
				SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames));
		mapper.setFilterProvider(filters);

		return mapper;
	}

	/**
	 * Return a JSON mapper that will serialize only the fields specified for
	 * the the given class, and globally, so also for child objects, exclude
	 * fields related to persistence: {@code id,version,handler}.
	 *
	 * @param includeOnlyFields
	 *            array of property name to include
	 * @param filteredClazz
	 *            class to filter respect to includeOnlyFields
	 * @param globalIgnorableFields
	 *            array of property name to globally ignore
	 * @return ObjectMapper
	 */
	public static ObjectMapper getIncludeOnlyMapper(List<String> includeOnlyFields, Class<?> filteredClazz, List<String> globalIgnorableFields) {
		@JsonFilter("filter properties by name")
		class PropertyFilterMixIn {
		}

		@JsonFilter("membership filter")
		class MembershipFilterMixIn {
		}

		ObjectMapper mapper = new ObjectMapper();
		if(filteredClazz != Membership.class)
			mapper.addMixIn(Membership.class, MembershipFilterMixIn.class);

		mapper.addMixIn(Object.class, PropertyFilterMixIn.class);

		FilterProvider filters = new SimpleFilterProvider().addFilter("filter properties by name", ExSimpleBeanPropertyFilter.filterOutAllExcept(
				new HashSet<String>(includeOnlyFields), filteredClazz, new HashSet<String>(globalIgnorableFields)))
				.addFilter("membership filter",
						ExSimpleBeanPropertyFilter.filterOutAllExcept(new HashSet<String>(RestParameters.MembershipFields.DEFAULT_FIELDS), Membership.class, new HashSet<String>(globalIgnorableFields)));

		mapper.setFilterProvider(filters);

		return mapper;
	}

	/**
	 * Return a JSON mapper that will serialize only the fields specified for
	 * the the given class, and globally, so also for child objects, exclude
	 * fields related to persistence: {@code id,version,handler}.
	 *
	 * @param includeOnlyFields
	 *            array of property name to include
	 * @param filteredClazz
	 *            class to filter respect to includeOnlyFields
	 * @return ObjectMapper
	 */
	public static ObjectMapper getIncludeOnlyMapperForProxy(List<String> includeOnlyFields, Class<?> filteredClazz) {
		@JsonFilter("include properties by name")
		class PropertyFilterMixIn {
		}

		@JsonFilter("membership filter")
		class MembershipFilterMixIn {
		}

		ObjectMapper mapper = new ObjectMapper();
		if(filteredClazz != Membership.class)
			mapper.addMixIn(Membership.class, MembershipFilterMixIn.class);

		mapper.addMixIn(Object.class, PropertyFilterMixIn.class);

//		SimpleModule module = new SimpleModule();
//		module.addSerializer(Membership.class, new MembershipSerializer());
//		mapper.registerModule(module);

		FilterProvider filters = new SimpleFilterProvider().addFilter("include properties by name",
				ExSimpleBeanPropertyFilter.filterOutAllExcept(new HashSet<String>(includeOnlyFields), filteredClazz, ignorableProxyField))
				.addFilter("membership filter",
						ExSimpleBeanPropertyFilter.filterOutAllExcept(new HashSet<String>(RestParameters.MembershipFields.DEFAULT_FIELDS), Membership.class, ignorableProxyField));
		mapper.setFilterProvider(filters);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return mapper;
	}

//	/**
//	 * Return a JSON writer ignoring the serialization of the specified fields.
//	 *
//	 * @deprecated replaced by <code>getExcludeMapper()</code>.
//	 * @param ignorableFieldNames
//	 *            array of property name to ignore
//	 * @return ObjectMapper
//	 */
//	@Deprecated
//	public static ObjectWriter getExcludeWriter(String[] ignorableFieldNames) {
//		@JsonFilter("filter properties by name")
//		class PropertyFilterMixIn {
//		}
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.addMixInAnnotations(Object.class, PropertyFilterMixIn.class);
//
//		FilterProvider filters = new SimpleFilterProvider().addFilter("filter properties by name",
//				SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames));
//		ObjectWriter writer = mapper.writer(filters);
//
//		return writer;
//	}

//	/**
//	 * Return a JSON writer including only the specified fields.
//	 *
//	 * @deprecated replaced by <code>getIncludeOnlyMapper()</code>.
//	 * @param includeFieldNames
//	 *            array of property name to include
//	 * @return ObjectMapper
//	 */
//	@Deprecated
//	public static ObjectWriter getIncludeWriter(String[] includeFieldNames) {
//		@JsonFilter("filter properties by name")
//		class PropertyFilterMixIn {
//		}
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.addMixInAnnotations(Object.class, PropertyFilterMixIn.class);
//
//		FilterProvider filters = new SimpleFilterProvider().addFilter("filter properties by name",
//				SimpleBeanPropertyFilter.filterOutAllExcept(includeFieldNames));
//		ObjectWriter writer = mapper.writer(filters);
//
//		return writer;
//	}

	/**
	 * Provide some static strategy to properly serialize to JSON Exrade
	 * entities.
	 *
	 * @author carlo.polisini
	 *
	 */
	public static class ExradeModule extends SimpleModule {
		public ExradeModule() {
			super("JSON", new Version(1, 0, 0, null, null, null));
		}

		@Override
		public void setupModule(SetupContext context) {
			context.setMixInAnnotations(InformationModelCategory.class, CategoryMixIn.class);
			// and other set up, if any
		}

	}

	/**
	 * Setup the Json serialize for Category class: ignore parent property to
	 * avoid circular dependency (but include parentName property in
	 * serialization) ignore full name to avoid heavy computation
	 *
	 * @author carlo.polisini
	 *
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	abstract class CategoryMixIn {
		@JsonIgnore
		abstract String getFullName();

		@JsonIgnore
		InformationModelCategory parent;
	}

	/**
	 * Deserialize json string representation to List
	 *
	 * @param jsonText
	 *            text representation of json objects
	 * @param clazz
	 *            Class represents json object
	 * @return List of object of specified type. If jsonText is null or empty it
	 *         will return empty list
	 */
	public static <T> List<T> deserializeList(String jsonText, Class<T> clazz) {

		if (jsonText == null || jsonText.isEmpty()){
			return new ArrayList<T>();
		}

		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			TypeFactory t = TypeFactory.defaultInstance();
			return mapper.readValue(jsonText, t.constructCollectionType(ArrayList.class, clazz));

			/*
			 * DeserializationConfig deserializationConfig =
			 * mapper.getDeserializationConfig();
			 * deserializationConfig.setDateFormat
			 */
			// return new ObjectMapper().readValue(jsonText, new
			// TypeReference<ArrayList<T>>() { });
		} catch (Exception e) {
			throw new ExJSONException(ErrorKeys.JSON_CANNOT_CONVERT_FROM, e);
		}
	}

	public static <T> T deserialize(String jsonText, Class<T> clazz) {

		if (jsonText == null || jsonText.isEmpty())
			return null;

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.readValue(jsonText, clazz);
		} catch (Exception e) {
			throw new ExJSONException(ErrorKeys.JSON_CANNOT_CONVERT_FROM, e);
		}
	}

	public static <K, V> Map<K, V> deserializeMap(String jsonText, Class<K> clazzK, Class<V> clazzV) {
		if (jsonText == null || jsonText.isEmpty())
			return Collections.emptyMap();

		ObjectMapper mapper = new ObjectMapper();
		TypeFactory typeFactory = mapper.getTypeFactory();
		MapType mapType = typeFactory.constructMapType(HashMap.class, clazzK, clazzV);
		try {
			return mapper.readValue(jsonText, mapType);
		} catch (Exception e) {
			throw new ExJSONException(ErrorKeys.JSON_CANNOT_CONVERT_FROM, e);
		}
	}

	public static <T> List<T> deserializePolymorphicList(String jsonText, Class<T> toClazz, Class baseClass, Class childClass) {

		if (jsonText == null || jsonText.isEmpty())
			return Collections.emptyList();

		try {

			ObjectMapper mapper = new ObjectMapper();
			SimpleModule myModule = new SimpleModule();
			myModule.addAbstractTypeMapping(baseClass, childClass);
			mapper.registerModule(myModule);
			TypeFactory t = TypeFactory.defaultInstance();
			return mapper.readValue(jsonText, t.constructCollectionType(Collection.class, toClazz));

		} catch (Exception e) {
			throw new ExJSONException(ErrorKeys.JSON_CANNOT_CONVERT_FROM, e);
		}
	}

	public static List<String> nodeToTextList(JsonNode jsonArray){
		List<String> values = new ArrayList<>();
		if (jsonArray != null && jsonArray.isArray()){
			for (int i = 0; i < jsonArray.size(); i++) {
				String fileUUID =  jsonArray.get(i).asText();
				if (!Strings.isNullOrEmpty(fileUUID)) {
					values.add(fileUUID);
				}
			}
		}
		return values;
	}


}
