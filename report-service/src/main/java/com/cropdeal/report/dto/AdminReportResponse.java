package com.cropdeal.report.dto;

public class AdminReportResponse {

    private Long totalOrders;
    private Long completedOrders;
    private Long successfulPayments;
    private Long failedPayments;
    private Double totalRevenue;

    public AdminReportResponse() {
    }

	public Long getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(Long totalOrders) {
		this.totalOrders = totalOrders;
	}

	public Long getCompletedOrders() {
		return completedOrders;
	}

	public void setCompletedOrders(Long completedOrders) {
		this.completedOrders = completedOrders;
	}

	public Long getSuccessfulPayments() {
		return successfulPayments;
	}

	public void setSuccessfulPayments(Long successfulPayments) {
		this.successfulPayments = successfulPayments;
	}

	public Long getFailedPayments() {
		return failedPayments;
	}

	public void setFailedPayments(Long failedPayments) {
		this.failedPayments = failedPayments;
	}

	public Double getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(Double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

}