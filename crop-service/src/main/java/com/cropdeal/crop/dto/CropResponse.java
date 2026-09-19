package com.cropdeal.crop.dto;

import java.time.LocalDateTime;

import com.cropdeal.crop.entity.CropStatus;

public class CropResponse {

    private Long id;
    private Long farmerId;
    private String category;
    private String cropName;
    private String grade;
    private Double quantity;
    private Double expectedPrice;
    private String location;
    private CropStatus status;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    public CropResponse() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFarmerId() {
		return farmerId;
	}

	public void setFarmerId(Long farmerId) {
		this.farmerId = farmerId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCropName() {
		return cropName;
	}

	public void setCropName(String cropName) {
		this.cropName = cropName;
	}

    public String getGrade() { return grade; }

    public void setGrade(String grade) { this.grade = grade; }

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getExpectedPrice() {
		return expectedPrice;
	}

	public void setExpectedPrice(Double expectedPrice) {
		this.expectedPrice = expectedPrice;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public CropStatus getStatus() {
		return status;
	}

	public void setStatus(CropStatus status) {
		this.status = status;
	}

	public LocalDateTime getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(LocalDateTime publishedAt) {
		this.publishedAt = publishedAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

   
}