package com.cropdeal.wallet.repository;

import com.cropdeal.wallet.entity.DealerWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DealerWalletRepository extends JpaRepository<DealerWallet, Long> {

    Optional<DealerWallet> findByDealerId(Long dealerId);
}