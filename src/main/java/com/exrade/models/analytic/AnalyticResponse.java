package com.exrade.models.analytic;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.*;

public class AnalyticResponse {

	private List<Metric> metrics = new ArrayList<>();
	
	private List<Dimension> dimensions = new ArrayList<>();
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	private Date startDate;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	private Date endDate;
	
	private List<Column> columns = new ArrayList<>();
	
	private Map<String, String> totals = new HashMap<>();
	
	private List<List<String>> rows = new ArrayList<List<String>>();
	
	private String objectID;
	
	public static AnalyticResponse create(AnalyticRequest request){
		AnalyticResponse response = new AnalyticResponse();
		response.setDimensions(request.getDimensions());
		response.setMetrics(request.getMetrics());
		response.setStartDate(request.getStartDate());
		response.setEndDate(request.getEndDate());
		response.setObjectID(request.getObjectID());
		return response;
	}
	
	public List<Metric> getMetrics() {
		return metrics;
	}
	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}
	public List<Dimension> getDimensions() {
		return dimensions;
	}
	public void setDimensions(List<Dimension> dimensions) {
		this.dimensions = dimensions;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	public Map<String, String> getTotals() {
		return totals;
	}
	public void setTotals(Map<String, String> totals) {
		this.totals = totals;
	}
	public List<List<String>> getRows() {
		return rows;
	}
	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}
}
