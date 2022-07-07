package com.exrade.models.userprofile;

public class Invoice {

	private String number;

	private Long periodStart;

	private Long periodEnd;

	private Long issueDate;

	private Long totalAmount;

	private String currency;

	private String status;

	private String invoicePdfUrl;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Long getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(Long periodStart) {
		this.periodStart = periodStart;
	}

	public Long getPeriodEnd() {
		return periodEnd;
	}

	public void setPeriodEnd(Long periodEnd) {
		this.periodEnd = periodEnd;
	}

	public Long getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Long issueDate) {
		this.issueDate = issueDate;
	}

	public Long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInvoicePdfUrl() {
		return invoicePdfUrl;
	}

	public void setInvoicePdfUrl(String invoicePdfUrl) {
		this.invoicePdfUrl = invoicePdfUrl;
	}
}
