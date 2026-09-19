package com.cropdeal.report.dto;

public class OrderReportResponse {

    private Long totalOrders;
    private Long createdOrders;
    private Long acceptedOrders;
    private Long rejectedOrders;
    private Long paidOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private Double totalOrderValue;

    public OrderReportResponse() {
    }

	public Long getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(Long totalOrders) {
		this.totalOrders = totalOrders;
	}

	public Long getCreatedOrders() {
		return createdOrders;
	}

	public void setCreatedOrders(Long createdOrders) {
		this.createdOrders = createdOrders;
	}

	public Long getAcceptedOrders() {
		return acceptedOrders;
	}

	public void setAcceptedOrders(Long acceptedOrders) {
		this.acceptedOrders = acceptedOrders;
	}

	public Long getRejectedOrders() {
		return rejectedOrders;
	}

	public void setRejectedOrders(Long rejectedOrders) {
		this.rejectedOrders = rejectedOrders;
	}

	public Long getPaidOrders() {
		return paidOrders;
	}

	public void setPaidOrders(Long paidOrders) {
		this.paidOrders = paidOrders;
	}

	public Long getCompletedOrders() {
		return completedOrders;
	}

	public void setCompletedOrders(Long completedOrders) {
		this.completedOrders = completedOrders;
	}

	public Long getCancelledOrders() {
		return cancelledOrders;
	}

	public void setCancelledOrders(Long cancelledOrders) {
		this.cancelledOrders = cancelledOrders;
	}

	public Double getTotalOrderValue() {
		return totalOrderValue;
	}

	public void setTotalOrderValue(Double totalOrderValue) {
		this.totalOrderValue = totalOrderValue;
	}

}