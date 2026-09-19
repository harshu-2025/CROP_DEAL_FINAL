package com.cropdeal.auction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateAuctionRequest {

    @NotNull(message = "Crop ID is required")
    private Long cropId;

    @NotNull(message = "Farmer ID is required")
    private Long farmerId;

    @NotNull(message = "Grade is required")
    private String grade;

    @NotNull(message = "Auction duration is required")
    @Positive(message = "Auction duration must be greater than zero")
    private Integer durationMinutes;

    public CreateAuctionRequest() {
    }

    public Long getCropId() {
        return cropId;
    }

    public void setCropId(Long cropId) {
        this.cropId = cropId;
    }

    public Long getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(Long farmerId) {
        this.farmerId = farmerId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}