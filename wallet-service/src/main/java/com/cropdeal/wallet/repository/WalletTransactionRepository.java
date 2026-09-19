package com.cropdeal.wallet.repository;

import com.cropdeal.wallet.entity.WalletTransaction;
import com.cropdeal.wallet.entity.WalletTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    boolean existsByReferenceAndTransactionType(String reference, WalletTransactionType transactionType);

    List<WalletTransaction> findByDealerId(Long dealerId);

    List<WalletTransaction> findByWalletId(Long walletId);
}