package com.cropdeal.report.dto;

public class FarmerSalesReportResponse {

    private Long farmerId;
    private String farmerName;
    private Long totalOrders;
    private Double totalQuantitySold;
    private Double totalSalesAmount;

    public FarmerSalesReportResponse() {
    }

	public Long getFarmerId() {
		return farmerId;
	}

	public void setFarmerId(Long farmerId) {
		this.farmerId = farmerId;
	}

	public String getFarmerName() {
		return farmerName;
	}

	public void setFarmerName(String farmerName) {
		this.farmerName = farmerName;
	}

	public Long getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(Long totalOrders) {
		this.totalOrders = totalOrders;
	}

	public Double getTotalQuantitySold() {
		return totalQuantitySold;
	}

	public void setTotalQuantitySold(Double totalQuantitySold) {
		this.totalQuantitySold = totalQuantitySold;
	}

	public Double getTotalSalesAmount() {
		return totalSalesAmount;
	}

	public void setTotalSalesAmount(Double totalSalesAmount) {
		this.totalSalesAmount = totalSalesAmount;
	}

}