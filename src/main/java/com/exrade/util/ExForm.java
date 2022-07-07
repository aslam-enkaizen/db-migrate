package com.exrade.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExForm<T> {

	private final String formName; 
	private final Class<T> classWrapped;

	public ExForm(String name, Class<T> clazz) {
		this.formName = name;
		this.classWrapped = clazz;
	}

	public ExForm(Class<T> clazz) {
		this.formName = null;
		this.classWrapped = clazz;
	}

//	public ExForm(String rootName, Class<T> clazz, Map<String,String> data, Map<String,List<ValidationError>> errors, Option<T> value, Class<?> groups) {
//		super(rootName, clazz, data, errors, value,groups);
//		this.formName = rootName;
//		this.classWrapped = clazz;
//	}
	
	public static <T> ExForm<T> form(Class<T> clazz) {
		return new ExForm<T>(clazz);
	}

	private T blankInstance() {
		try {
			return classWrapped.newInstance();
		} catch(Exception e) {
			throw new RuntimeException("Cannot instantiate " + classWrapped + ". It must have a default constructor", e);
		}
	}

//	@Override
	public ExForm<T> bind(Map<String, String> data, String... allowedFields) {
		return null;
//		DataBinder dataBinder = null;
//		Map<String, String> objectData = data;
//		if(formName == null) {
//			dataBinder = new DataBinder(blankInstance());
//		} else {
//			dataBinder = new DataBinder(blankInstance(), formName);
//			objectData = new HashMap<String,String>();
//			for(String key: data.keySet()) {
//				if(key.startsWith(formName + ".")) {
//					objectData.put(key.substring(formName.length() + 1), data.get(key));
//				}
//			}
//		}
//		if(allowedFields.length > 0) {
//			dataBinder.setAllowedFields(allowedFields);
//		}
//
//
//		dataBinder.setConversionService(play.data.format.Formatters.conversion);
//		dataBinder.setAutoGrowNestedPaths(true);
//		dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
//			//public String getAsText() {
//				//return new SimpleDateFormat("dd/MM/yyyy").format((Date) getValue());
//			//}
//
//			public void setAsText(String text) {
//				try {
//		            //setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
//					setValue(new Date(Long.parseLong(text)));
//		        } catch(NumberFormatException e) {
//		            setValue(null);
//		        }
//			}
//		});
//		dataBinder.bind(new MutablePropertyValues(objectData));
//		Set<ConstraintViolation<Object>> validationErrors;
//		Map<String,List<ValidationError>> errors = new HashMap<>();
//		BindingResult result = dataBinder.getBindingResult();
//
//
//		Object globalError = null;
//		if(result.getTarget() != null) {
//			try {
//				java.lang.reflect.Method v = result.getTarget().getClass().getMethod("validate");
//				globalError = v.invoke(result.getTarget());
//			} catch(NoSuchMethodException e) {
//			} catch(Throwable e) {
//				throw new RuntimeException(e);
//			}
//		}
//		if(globalError != null) {
//			errors = new HashMap<String,List<ValidationError>>();
//			if(globalError instanceof String) {
//				errors.put("", new ArrayList<ValidationError>());
//				errors.get("").add(new ValidationError("", (String)globalError, new ArrayList()));
//			} else if(globalError instanceof List) {
//				for (ValidationError error : (List<ValidationError>) globalError) {
//					List<ValidationError> errorsForKey = errors.get(error.key());
//					if (errorsForKey == null) {
//						errors.put(error.key(), errorsForKey = new ArrayList<ValidationError>());
//					}
//					errorsForKey.add(error);
//				}
//			} else if(globalError instanceof Map) {
//				errors = (Map<String,List<ValidationError>>)globalError;
//			}
//			return new ExForm(formName, classWrapped, data, errors, None(), null);
//		}
//
//		return new ExForm(formName, classWrapped, new HashMap<String,String>(data), new HashMap<String,List<ValidationError>>(errors), Some((T)result.getTarget()), null);
	}

	/**
     * Returns all errors.
     *
     * @return All errors associated with this form.
     */
    public Map<String,List<ExValidationError>> formErrors() {
    	
    	Map<String,List<ExValidationError>> exErrors = new HashMap<>();
    	
//    	for (Map.Entry<String,List<ValidationError>> validationErrors : super.errors().entrySet()) {
//
//			String fieldError = validationErrors.getKey();
//			List<ValidationError> fieldErrors = validationErrors.getValue();
//
//			if (!fieldErrors.isEmpty()){
//				List<ExValidationError> exFieldErrors = new ArrayList<>();
//				for (Object validationErrorObj : fieldErrors) {
//					if(validationErrorObj instanceof ValidationError){
//						ValidationError validationError = (ValidationError) validationErrorObj;
//						exFieldErrors.add(new ExValidationError(validationError.key(), validationError.message(), validationError.arguments()));
//					}
//					else if (validationErrorObj instanceof ExValidationError)
//						exFieldErrors.add((ExValidationError)validationErrorObj);
//				}
//				exErrors.put(fieldError, exFieldErrors);
//
//			}
//
//		}
        return exErrors;
    }

	public ExForm<T> bindFromRequest(){
		return null;
	}

	public Boolean hasErrors(){
		return false;
	}
	public T get() {
		return null;
	}

}
