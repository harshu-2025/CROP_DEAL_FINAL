package com.cropdeal.subscription.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
	@Table(name = "crop_subscriptions")
	public class CropSubscription {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private Long dealerId;

	    private String cropName;

	    private String category;

	    private String location;

	    private Double minimumQuantity;

	    private Double maximumPrice;

	    private boolean active;

	    private LocalDateTime createdAt;

	    private LocalDateTime updatedAt;

	    public CropSubscription() {
	    }

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

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

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}

		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}
	    
	}
