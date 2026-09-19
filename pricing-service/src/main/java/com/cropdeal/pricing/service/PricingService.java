package com.cropdeal.pricing.service;

import com.cropdeal.pricing.dto.PricingRequest;
import com.cropdeal.pricing.dto.PricingResponse;

public interface PricingService {

    PricingResponse calculateFairPrice(PricingRequest request);
}