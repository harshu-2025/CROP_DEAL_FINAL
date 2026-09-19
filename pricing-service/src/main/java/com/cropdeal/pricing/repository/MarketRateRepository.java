package com.cropdeal.pricing.repository;

import com.cropdeal.pricing.entity.MarketRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketRateRepository extends JpaRepository<MarketRate, Long> {

    Optional<MarketRate> findByCropNameIgnoreCaseAndLocationIgnoreCase(String cropName,String location);
}