package com.cropdeal.pricing.controller;

import com.cropdeal.pricing.dto.PricingRequest;
import com.cropdeal.pricing.dto.PricingResponse;
import com.cropdeal.pricing.entity.MarketRate;
import com.cropdeal.pricing.repository.MarketRateRepository;
import com.cropdeal.pricing.service.PricingService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pricing")
public class PricingController {

    private final PricingService pricingService;
    private final MarketRateRepository marketRateRepository;

    public PricingController(PricingService pricingService,
                             MarketRateRepository marketRateRepository) {
        this.pricingService = pricingService;
        this.marketRateRepository = marketRateRepository;
    }

    @PostMapping("/calculate")
    public ResponseEntity<PricingResponse> calculatePrice(
            @Valid @RequestBody PricingRequest request) {

        return ResponseEntity.ok(
                pricingService.calculateFairPrice(request)
        );
    }

    @PostMapping("/market-rates")
    public ResponseEntity<MarketRate> addMarketRate(
            @RequestBody MarketRate marketRate) {

        marketRate.setUpdatedAt(LocalDateTime.now());

        MarketRate savedRate =
                marketRateRepository.save(marketRate);

        return ResponseEntity.ok(savedRate);
    }
}