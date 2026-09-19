package com.cropdeal.wallet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dealer_wallets")
public class DealerWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long dealerId;

    private Double availableBalance;

    private Double blockedBalance;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    private Double escrowBalance;

    public DealerWallet() {
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
	

	public Double getEscrowBalance() {
		return escrowBalance;
	}

	public void setEscrowBalance(Double escrowBalance) {
		this.escrowBalance = escrowBalance;
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