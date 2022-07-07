package com.exrade.util;

import com.exrade.platform.exception.ErrorKeys;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.persistence.ConnectionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import javassist.util.proxy.ProxyFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;





/**
 * This is a utility class to provide utility related to copying objects.
 * @author john
 *
 */
public class ObjectsUtil {

	
	/**
	 * Return a deep clone of the object excluding the property {@code id} and
	 * {@code version}
	 * 
	 * @param iList
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clonePersistent(T object, Class<? extends T> clazz) {
		if (ProxyFactory.isProxyClass(object.getClass())){
			OObjectDatabaseTx db = ConnectionManager.getInstance().getObjectConnection();
			try{
				object = db.detachAll(object, true);
			}
			finally{
				db.close();
			}
			clazz = (Class<? extends T>) object.getClass();
		}
			
		try {
			String str = JSONUtil.toJsonNoPersistence(object);
			return new ObjectMapper().readValue(str, clazz);
		} catch (IOException e) {
			throw new ExException(ErrorKeys.OBJECT_CANNOT_CLONE, e);
		}
	}
	
	/**
	 * Return a deep clone of the object excluding the property {@code id} and
	 * {@code version}
	 * 
	 * @param iList
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clonePersistentWithProxy(T object, Class<? extends T> clazz) {
		T clonedEntity = clonePersistent(object, clazz);
		// dirty way to get db proxied instance
		OObjectDatabaseTx db = ConnectionManager.getInstance().getObjectConnection();
		ODocument doc = db.getRecordByUserObject(clonedEntity, true);
		T proxiedClonedEntity = (T) db.getUserObjectByRecord(doc,null);
		return proxiedClonedEntity;
	}


	
	/**
	 * Return a deep clone of the object excluding the property {@code id and UUID} and
	 * {@code version}
	 * 
	 * @param iList
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clonePersistentExcludingUUID(T object, Class<? extends T> clazz) {
		if (ProxyFactory.isProxyClass(object.getClass())){
			OObjectDatabaseTx db = ConnectionManager.getInstance().getObjectConnection();
			try{
				object = db.detachAll(object, true);
			}
			finally{
				db.close();
			}
			clazz = (Class<? extends T>) object.getClass();
		}
			
		try {
			String str = JSONUtil.toJsonNoPersistenceOrUUID(object);
			return new ObjectMapper().readValue(str, clazz);
		} catch (IOException e) {
			throw new ExException(ErrorKeys.OBJECT_CANNOT_CLONE, e);
		}
	}
	/**
	 * Return a deep clone of the list excluding the property {@code id} and
	 * {@code version}
	 * 
	 * @param iList
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public static <T> List<T> clonePersistentList(List<T> iList, Class<T> clazz) {
		List<T> clonedList = null;
		String json = JSONUtil.toJsonNoPersistence(iList);
		try {
			ObjectMapper mapper = new ObjectMapper();
			TypeFactory t = TypeFactory.defaultInstance();
			clonedList = mapper.readValue(json, t.constructCollectionType(ArrayList.class, clazz));
		} catch (IOException e) {
			throw new ExException(ErrorKeys.OBJECT_CANNOT_CLONE, e);
		}
		return clonedList;
	}



	/**
	 * Generates unique identifier and returns as text.
	 * 
	 * @return unique id as text
	 */
	public static String generateUniqueID() {
		return UUID.randomUUID().toString();
	}

	public static String getOriginalName(Object object){
		if (object == null) {
			return null;
		}
		else if (ProxyFactory.isProxyClass(object.getClass())){
			return object.getClass().getSimpleName().replaceAll("(.*)(_\\$\\$_.*)","$1");
		}
		else {
			return object.getClass().getSimpleName();
		}
	}
	
	/**
	 * Create new proxy object that give the access only to the method of the specified
	 * interface.
	 * 
	 * @param type
	 * @param obj
	 * @return
	 */
	public static <T> T getProxy(Class<T> type, Object obj) {
		/**
		 * Generic implementation of {@code InvocationHandler} that simply forward the
		 * method call
		 * 
		 * @author Carlo Polisini
		 * 
		 */
		class ProxyUtil implements InvocationHandler {
			Object obj;

			public ProxyUtil(Object o) {
				obj = o;
			}

			@Override
			public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
				Object result = null;
				result = m.invoke(obj, args);
				return result;
			}
		}
		// TODO: The suppress warning is needed cause JDK class java.lang.reflect.Proxy
		// needs generics
		@SuppressWarnings("unchecked")
		T proxy = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type },
				new ProxyUtil(obj));
		return proxy;
	}

	/**
	 * Copy the property values of the given source bean into the given target bean.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * @param source the source bean
	 * @param target the target bean
	 * @param editable the class (or interface) to restrict property setting to
	 * @param ignoreProperties array of property names to ignore
	 * @throws BeansException if the copying failed
	 * @see BeanWrapper
	 */
	private static void copyProperties(Object source, Object target, Class<?> editable, String[] ignoreProperties)
			throws BeansException {

		Objects.requireNonNull(source, "Source must not be null");
		Objects.requireNonNull(target, "Target must not be null");

		Class<?> actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
						"] not assignable to Editable class [" + editable.getName() + "]");
			}
			actualEditable = editable;
		}
		
		PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
		List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null &&
					(ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());

				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						Method writeMethod = targetPd.getWriteMethod();
						if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
							writeMethod.setAccessible(true);
						}
						if(value instanceof List && Set.class.equals(writeMethod.getParameterTypes()[0])){
							value = new HashSet((List)value);
						}
						writeMethod.invoke(target, value);
					}
					catch (Exception ex) {
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
				else {
					//try to read from field itself
					Field sourceField;
					try {
						sourceField = source.getClass().getDeclaredField(targetPd.getName());
					} catch (NoSuchFieldException | SecurityException e1) {
						// if field is not found or not accessible it's set to null
						sourceField = null;
					}
					try {
						if (sourceField!= null){						
							Method writeMethod = targetPd.getWriteMethod();
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							writeMethod.invoke(target, sourceField.get(source));
						}

					} catch (Exception e) {
						throw new FatalBeanException("Could not copy properties from source to target", e);
					}
				}
			}
		}
	}
	
	public static void bindFields(Object oSource,Object oDest,List<String> ignoreFields){
		 copyProperties(oSource,oDest,null,ignoreFields.toArray(new String[0]));
	}
	
	/**
	 * Copy the property values of the given source bean into the given target bean.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * @param source the source bean
	 * @param target the target bean
	 */
	public static void bindFields(Object oSource,Object oDest){
		bindFields(oSource,oDest,new ArrayList<String>());
	}

	
	public static List<String> getClassFields(@SuppressWarnings("rawtypes") Class klass){
		List<String> fields = new ArrayList<String>();
		
		if(klass != null && klass.getFields() != null){
			for(Field field : klass.getFields()){
				fields.add(field.getName());
			}
		}
		
		return fields;
	}

	public static boolean setField(Object object, String fieldName, Object fieldValue) {
	    Class<?> clazz = object.getClass();
	    while (clazz != null) {
	        try {
	            Field field = clazz.getDeclaredField(fieldName);
	            field.setAccessible(true);
	            field.set(object, fieldValue);
	            return true;
	        } catch (NoSuchFieldException e) {
	            clazz = clazz.getSuperclass();
	        } catch (Exception e) {
	            throw new IllegalStateException(e);
	        }
	    }
	    return false;
	}
}
