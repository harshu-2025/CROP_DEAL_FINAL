package com.cropdeal.report.client;

import com.cropdeal.report.dto.CropResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CROP-SERVICE")
public interface CropClient {

    @GetMapping("/crops/{id}")
    CropResponse getCropById(@PathVariable Long id);
}