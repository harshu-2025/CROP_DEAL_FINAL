package com.cropdeal.auction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PlaceBidRequest {

    @NotNull(message = "Dealer ID is required")
    private Long dealerId;

    @NotNull(message = "Bid amount is required")
    @Positive(message = "Bid amount must be greater than zero")
    private Double amount;

    public PlaceBidRequest() {
    }

    public Long getDealerId() {
        return dealerId;
    }

    public void setDealerId(Long dealerId) {
        this.dealerId = dealerId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}