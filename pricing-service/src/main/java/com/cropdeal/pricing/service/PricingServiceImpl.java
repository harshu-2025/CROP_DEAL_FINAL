package com.cropdeal.pricing.service;

import com.cropdeal.pricing.dto.PricingRequest;
import com.cropdeal.pricing.dto.PricingResponse;
import com.cropdeal.pricing.entity.MarketRate;
import com.cropdeal.pricing.entity.Grade;
import com.cropdeal.pricing.repository.MarketRateRepository;

import org.springframework.stereotype.Service;

@Service
public class PricingServiceImpl implements PricingService {

    private final MarketRateRepository marketRateRepository;

    public PricingServiceImpl(MarketRateRepository marketRateRepository) {
        this.marketRateRepository = marketRateRepository;
    }

    @Override
    public PricingResponse calculateFairPrice(PricingRequest request) {

        MarketRate marketRate = marketRateRepository
                .findByCropNameIgnoreCaseAndLocationIgnoreCase(
                        request.getCropName(),
                        request.getLocation()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Market rate not found for crop and location"
                        )
                );

        double gradeAdjustment = getGradeAdjustment(request.getGrade());

        double adjustmentAmount =
                marketRate.getMarketPrice() * gradeAdjustment / 100;

        double fairBasePrice =
                marketRate.getMarketPrice() + adjustmentAmount;

        PricingResponse response = new PricingResponse();

        response.setCropName(marketRate.getCropName());
        response.setLocation(marketRate.getLocation());
        response.setGrade(request.getGrade());
        response.setMarketPrice(marketRate.getMarketPrice());
        response.setGradeAdjustmentPercentage(gradeAdjustment);
        response.setFairBasePrice(fairBasePrice);

        return response;
    }

    private double getGradeAdjustment(Grade grade) {

        if (grade == Grade.A) {
            return 5.0;
        }

        if (grade == Grade.B) {
            return 2.0;
        }

        return 0.0;
    }
}