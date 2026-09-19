package com.cropdeal.auction.dto;

public class WalletFundsRequest {

    private Double amount;
    private String reference;

    public WalletFundsRequest() {
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}