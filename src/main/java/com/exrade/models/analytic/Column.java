package com.exrade.models.analytic;

import com.exrade.models.common.DataType;

public class Column {

	private String name;
	private ColumnType columnType;
	private DataType dataType;
	
	public static Column create(String name, ColumnType columnType, DataType dataType){
		Column column = new Column();
		column.setName(name);
		column.setColumnType(columnType);
		column.setDataType(dataType);
		return column;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ColumnType getColumnType() {
		return columnType;
	}
	public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	
}
