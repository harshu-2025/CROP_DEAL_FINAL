package com.cropdeal.pricing.dto;

import com.cropdeal.pricing.entity.Grade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PricingRequest {

    @NotBlank(message = "Crop name is required")
    private String cropName;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Grade is required")
    private Grade grade;

    public PricingRequest() {
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
}