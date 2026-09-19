package com.cropdeal.auction.dto;

public class WalletResponse {

    private Long id;
    private Long dealerId;
    private Double availableBalance;
    private Double blockedBalance;

    public WalletResponse() {
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

    public Double getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(Double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public Double getBlockedBalance() {
        return blockedBalance;
    }

    public void setBlockedBalance(Double blockedBalance) {
        this.blockedBalance = blockedBalance;
    }
}