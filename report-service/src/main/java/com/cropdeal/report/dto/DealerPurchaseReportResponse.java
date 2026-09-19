package com.cropdeal.report.dto;

public class DealerPurchaseReportResponse {

    private Long dealerId;
    private String dealerName;
    private Long totalOrders;
    private Double totalQuantityPurchased;
    private Double totalPurchaseAmount;

    public DealerPurchaseReportResponse() {
    }

	public Long getDealerId() {
		return dealerId;
	}

	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public Long getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(Long totalOrders) {
		this.totalOrders = totalOrders;
	}

	public Double getTotalQuantityPurchased() {
		return totalQuantityPurchased;
	}

	public void setTotalQuantityPurchased(Double totalQuantityPurchased) {
		this.totalQuantityPurchased = totalQuantityPurchased;
	}

	public Double getTotalPurchaseAmount() {
		return totalPurchaseAmount;
	}

	public void setTotalPurchaseAmount(Double totalPurchaseAmount) {
		this.totalPurchaseAmount = totalPurchaseAmount;
	}

}