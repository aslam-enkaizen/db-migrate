package com.exrade.util;

import com.exrade.platform.persistence.IPersistenceUUID;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.Map.Entry;

public class ExCollections {

	/**
	 * Cast all the elements of the list to the inferred type.
	 * @param iList
	 * @return ClassCastExcepton if casting fails
	 */
	public static <T, F> List<T> trasform(List<F> iList){
		Function<F, T> tFunction = new Function<F, T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T apply(F arg0) {
				return (T) arg0;
			}

		};
		return Lists.transform(iList, tFunction);
	}

	/**
	 * Cast all the elements of the list to the specified type.
	 * @param iList
	 * @return ClassCastExcepton if casting fails
	 */
	public static <T, F> List<T> trasform(List<F> iList,Class<? extends T> clazz){
		Function<F, T> tFunction = new Function<F, T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T apply(F arg0) {
				return (T) arg0;
			}

		};
		return Lists.transform(iList, tFunction);
	}

	/**
	 * Utility to trasform a {@code List<IPersistence>} into a {@code List<String>}
	 * containing the corrispondent IDs.
	 *
	 * @param iPersistents
	 * @return List<String>
	 */
	public static List<String> extractUUIDs(List<? extends IPersistenceUUID> iPersistents) {
		if (isEmpty(iPersistents)) {
			return Collections.emptyList();
		}

		List<String> persistentIDs = new ArrayList<>();
		for (IPersistenceUUID persistence : iPersistents) {
			persistentIDs.add(persistence.getUuid());
		}
		return persistentIDs;
	}

	public static <T> List<T> copyIterator(Iterator<T> iter) {
	    List<T> copy = new ArrayList<T>();
	    while (iter.hasNext())
	        copy.add(iter.next());
	    return copy;
	}

	public static List<String> commaSeparatedToList(String iCommaSeparated){
		List<String> values = new ArrayList<>();
		if (!Strings.isNullOrEmpty(iCommaSeparated)){
			values.addAll(Arrays.asList(iCommaSeparated.split("\\s*,\\s*")));
		}
		return values;
	}

	public static <E extends Enum<E>> List<E> commaSeparatedToEnumList(String iCommaSeparated,Class<E> enumType){
		List<E> values = new ArrayList<E>();
		if (!Strings.isNullOrEmpty(iCommaSeparated)){
			List<String> stringValues = Arrays.asList(iCommaSeparated.split("\\s*,\\s*"));
			for (String name : stringValues) {
				E enumValue = Enum.valueOf(enumType, name);
				values.add(enumValue);
			}
		}
		return values;
	}

	/**
	 * Null-safe check if the specified collection is empty.
	 * <p>
	 * Null returns true.
	 *
	 * @param coll
	 *            the collection to check, may be null
	 * @return true if empty or null
	 */
	public static boolean isEmpty(Collection<?> coll) {
		return coll == null || coll.isEmpty();
	}

	/**
	 * Null-safe check if the specified collection is not empty.
	 * <p>
	 * Null returns true.
	 *
	 * @param coll
	 *            the collection to check, may be null
	 * @return true if empty or null
	 */
	public static boolean isNotEmpty(Collection<?> coll) {
		return !isEmpty(coll);
	}
	/**
	 * Null-safe check if the specified collection is empty.
	 * <p>
	 * Null returns true.
	 *
	 * @param coll
	 *            the collection to check, may be null
	 * @return true if empty or null
	 */
	public static boolean isEmpty(Map<?,?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * Null-safe check if the specified collection is not empty.
	 * <p>
	 * Null returns true.
	 *
	 * @param coll
	 *            the collection to check, may be null
	 * @return true if empty or null
	 */
	public static boolean isNotEmpty(Map<?,?> map) {
		return !isEmpty(map);
	}

	public static <K, V> V getAny(Map<K, V> map){
		if(isNotEmpty(map)){
			Entry<K, V> entry = map.entrySet().iterator().next();
			return entry.getValue();
		}
		return null;
	}

	public static List<String> toLowerCase(Collection<String> iCollection){
		List<String> values = new ArrayList<>();

		if(iCollection != null){
			Iterator<String> iter = iCollection.iterator();
			while (iter.hasNext()) {
				values.add(iter.next().toLowerCase());
			}
		}
		return values;
	}

	@SafeVarargs
	public static <T> List<T> merge(List<T>... args) {
	    final List<T> result = new ArrayList<>();

	    if (args != null) {
	    	for (List<T> list : args) {
	    		result.addAll(list);
	    	}
	    }

	    return result;
	}
}
