package com.cropdeal.wallet.repository;

import com.cropdeal.wallet.entity.FarmerWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmerWalletRepository extends JpaRepository<FarmerWallet, Long> {

    Optional<FarmerWallet> findByFarmerId(Long farmerId);
}