package com.cropdeal.crop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CropRequest {

    @NotNull(message = "Farmer id is required")
    private Long farmerId;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Crop name is required")
    @Size(min = 2, max = 50, message = "Crop name must be between 2 and 50 characters")
    private String cropName;

    @NotBlank(message = "Grade is required")
    @Pattern(regexp = "^[ABCabc]$", message = "Grade must be A, B or C")
    private String grade;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity in quintal must be greater than 0")
    private Double quantity;

    @NotNull(message = "Expected price is required")
    @Positive(message = "Expected price per quintal must be greater than 0")
    private Double expectedPrice;

    @NotBlank(message = "Location is required")
    private String location;

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public Double getExpectedPrice() { return expectedPrice; }
    public void setExpectedPrice(Double expectedPrice) { this.expectedPrice = expectedPrice; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
