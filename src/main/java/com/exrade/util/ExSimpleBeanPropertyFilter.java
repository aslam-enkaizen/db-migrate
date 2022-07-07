package com.exrade.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import javassist.util.proxy.ProxyFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public abstract class ExSimpleBeanPropertyFilter extends SimpleBeanPropertyFilter {
	
	private static ThreadLocal<Stack<String>> depth = new ThreadLocal<Stack<String>>() {
		@Override
		protected Stack<String> initialValue() {
			return new Stack<String>();
		}

	};
	
	 public static SimpleBeanPropertyFilter filterOutAllExcept(Set<String> properties,Class<?> clazz,Set<String> globalProperties) {
	    	return new ExFilterExceptFilter(properties,clazz,globalProperties);
	 }

	 /**
	     * Custom method called to determine whether property will be included
	     * (if 'true' returned) or filtered out (if 'false' returned)
	     */
	protected abstract boolean include(PropertyWriter writer,Object bean); 
	 
	public void serializeAsField(Object bean, JsonGenerator jgen,
			SerializerProvider provider, PropertyWriter writer)
			throws Exception {
		if (include(writer,bean)) {
//			if(depth.get().isEmpty())
//				depth.get().push(bean.getClass().getSimpleName());
//			else if(!bean.getClass().getSimpleName().equals(depth.get().peek())){
//				if(!depth.get().contains(bean.getClass().getSimpleName()))
//					depth.get().push(bean.getClass().getSimpleName());
//				else
//					depth.get().pop();
//			}
//			if(depth.get().size() > 4) {
//				//jgen.writeNull();
//				depth.get().pop();
//			}
//			else {
//				writer.serializeAsField(bean, jgen, provider);
//			}
			writer.serializeAsField(bean, jgen, provider);
		}
	}

	 /**
	  * Filter implementation which defaults to filtering out unknown
	  * properties and only serializes ones explicitly listed.
	  */
	 public static class ExFilterExceptFilter
	 extends ExSimpleBeanPropertyFilter
	 implements java.io.Serializable
	 {
		 private static final long serialVersionUID = 1L;

		 /**
		  * Set of property names to serialize.
		  */
		 protected final Set<String> _propertiesToInclude;
		 protected Class<?> _clazz = null;
		 private Set<String> _globalPropertiesToExclude = new HashSet<String>();
		 
		 public ExFilterExceptFilter(Set<String> properties) {
			 _propertiesToInclude = properties;
		 }

		 public ExFilterExceptFilter(Set<String> properties,Class<?> clazz,Set<String> globalPropertiesToExclude) {
			 _propertiesToInclude = properties;
			 _globalPropertiesToExclude = globalPropertiesToExclude;
			 if (ProxyFactory.isProxyClass(clazz)){
				 _clazz = clazz.getSuperclass();
			 }
			 else {
				 _clazz = clazz;
			 }
		 }

		 @Override
		 protected boolean include(PropertyWriter writer,Object bean) {
			 
 			 if (bean == null)
				 return false;
			 
			String className = null;
			if (ProxyFactory.isProxyClass(bean.getClass())){
				 className = bean.getClass().getSuperclass().getSimpleName();
			 }
			 else {
				 className = bean.getClass().getSimpleName();	 
			 }
			 
			 return !_globalPropertiesToExclude.contains(writer.getName()) && (isRootProperty(writer, className) || isChildProperty(writer, className));
		 }
		 
		/**
		 * Root properties are included only if contained in the list of properties to include 
		 * @param writer
		 * @param className
		 * @return
		 */
		private boolean isRootProperty(PropertyWriter writer,String className) {
			
			return _propertiesToInclude.contains(writer.getName()) && _clazz != null &&
	            		className.equals(_clazz.getSimpleName());
		}

		/**
		 * Child properties are excluded by the filtering
		 * @param writer
		 * @param className
		 * @return
		 */
		private boolean isChildProperty(PropertyWriter writer,
				String className) {
			return _clazz!= null && !className.equals(_clazz.getSimpleName());
			//return _clazz!= null;
		}
		 
		@Override
		protected boolean include(BeanPropertyWriter writer) {
			return _propertiesToInclude.contains(writer.getName());
		}
               
		@Override
        protected boolean include(PropertyWriter writer) {
                return _propertiesToInclude.contains(writer.getName());
        }

	 }
	
}
