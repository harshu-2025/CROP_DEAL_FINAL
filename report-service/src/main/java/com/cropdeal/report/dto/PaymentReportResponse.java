package com.cropdeal.report.dto;

public class PaymentReportResponse {

    private Long totalPayments;
    private Long createdPayments;
    private Long processingPayments;
    private Long successfulPayments;
    private Long failedPayments;
    private Double totalSuccessfulAmount;

    public PaymentReportResponse() {
    }

	public Long getTotalPayments() {
		return totalPayments;
	}

	public void setTotalPayments(Long totalPayments) {
		this.totalPayments = totalPayments;
	}

	public Long getCreatedPayments() {
		return createdPayments;
	}

	public void setCreatedPayments(Long createdPayments) {
		this.createdPayments = createdPayments;
	}

	public Long getProcessingPayments() {
		return processingPayments;
	}

	public void setProcessingPayments(Long processingPayments) {
		this.processingPayments = processingPayments;
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

	public Double getTotalSuccessfulAmount() {
		return totalSuccessfulAmount;
	}

	public void setTotalSuccessfulAmount(Double totalSuccessfulAmount) {
		this.totalSuccessfulAmount = totalSuccessfulAmount;
	}

}