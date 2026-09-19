package com.cropdeal.wallet.dto;


import java.time.LocalDateTime;

import com.cropdeal.wallet.entity.WalletTransactionType;

public class WalletTransactionResponse {

    private Long id;
    private Long walletId;
    private Long dealerId;
    private WalletTransactionType transactionType;
    private Double amount;
    private String reference;
    private LocalDateTime createdAt;

    public WalletTransactionResponse() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWalletId() {
		return walletId;
	}

	public void setWalletId(Long walletId) {
		this.walletId = walletId;
	}

	public Long getDealerId() {
		return dealerId;
	}

	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
	}

	public WalletTransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(WalletTransactionType transactionType) {
		this.transactionType = transactionType;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}