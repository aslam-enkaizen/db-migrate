package com.exrade.models.analytic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnalyticRequest {

	private List<Metric> metrics = new ArrayList<>();
	private List<Dimension> dimensions = new ArrayList<>();
	private Date startDate;
	private Date endDate;
	private String objectID;
	
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
	public String getObjectID() {
		return objectID;
	}
	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}
	
}
