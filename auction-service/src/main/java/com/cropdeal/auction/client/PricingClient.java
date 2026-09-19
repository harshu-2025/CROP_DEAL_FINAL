package com.cropdeal.auction.client;

import com.cropdeal.auction.dto.PricingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "PRICING-SERVICE")
public interface PricingClient {

    @PostMapping("/pricing/calculate")
    PricingResponse calculatePrice(@RequestBody Map<String, Object> request);
}