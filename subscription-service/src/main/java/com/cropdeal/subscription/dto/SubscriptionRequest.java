package com.cropdeal.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SubscriptionRequest {

    @NotNull(message = "Dealer id is required")
    private Long dealerId;

    @NotBlank(message = "Crop name is required")
    private String cropName;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Minimum quantity is required")
    @Positive(message = "Minimum quantity must be greater than 0")
    private Double minimumQuantity;

    @NotNull(message = "Maximum price is required")
    @Positive(message = "Maximum price must be greater than 0")
    private Double maximumPrice;

	public Long getDealerId() {
		return dealerId;
	}

	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
	}

	public String getCropName() {
		return cropName;
	}

	public void setCropName(String cropName) {
		this.cropName = cropName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getMinimumQuantity() {
		return minimumQuantity;
	}

	public void setMinimumQuantity(Double minimumQuantity) {
		this.minimumQuantity = minimumQuantity;
	}

	public Double getMaximumPrice() {
		return maximumPrice;
	}

	public void setMaximumPrice(Double maximumPrice) {
		this.maximumPrice = maximumPrice;
	}

   
}
