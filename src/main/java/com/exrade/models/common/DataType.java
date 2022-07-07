package com.exrade.models.common;

import java.util.Date;



public enum DataType {
	AMOUNT(Double.class),
	BOOLEAN(Boolean.class),
	DATE(Date.class),
	DATETIME(Date.class),
	NUMBER(Double.class),
	FLOAT(Float.class),
	INTEGER(Integer.class),
	TEXT(String.class),
	URL(String.class),
	TIME(String.class),
	RESOURCE(String.class);

	private final Class<?> javaClass;

	DataType(Class<?> iJavaClass) {
		javaClass = iJavaClass;
	}

	public Class<?> getJavaClass() {return javaClass;}
}
