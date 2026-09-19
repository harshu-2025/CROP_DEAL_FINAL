package com.cropdeal.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

import com.cropdeal.payment.dto.CropResponse;

@FeignClient(name = "CROP-SERVICE")
public interface CropClient {

    @PutMapping("/crops/{id}/reduce-quantity")
    CropResponse reduceQuantity(
            @PathVariable Long id,
            @RequestParam Double quantity,
            @RequestHeader("X-Internal-Key") String internalKey
    );
}