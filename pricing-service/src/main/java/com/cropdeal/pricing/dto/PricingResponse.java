package com.cropdeal.pricing.dto;

import com.cropdeal.pricing.entity.Grade;

public class PricingResponse {

    private String cropName;
    private String location;
    private Grade grade;
    private Double marketPrice;
    private Double gradeAdjustmentPercentage;
    private Double fairBasePrice;

    public PricingResponse() {
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Double getGradeAdjustmentPercentage() {
        return gradeAdjustmentPercentage;
    }

    public void setGradeAdjustmentPercentage(Double gradeAdjustmentPercentage) {
        this.gradeAdjustmentPercentage = gradeAdjustmentPercentage;
    }

    public Double getFairBasePrice() {
        return fairBasePrice;
    }

    public void setFairBasePrice(Double fairBasePrice) {
        this.fairBasePrice = fairBasePrice;
    }
}