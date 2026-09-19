package com.cropdeal.order.client;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cropdeal.order.dto.CropResponse;

@Service
public class CropServiceClient {

    private final CropClient cropClient;

    @Value("${internal.api.key}")
    private String internalApiKey;

    public CropServiceClient(CropClient cropClient) {
        this.cropClient = cropClient;
    }

    @CircuitBreaker(name = "cropService",fallbackMethod = "cropServiceFallback")
    public CropResponse getCrop(Long cropId) {

        return cropClient.getCropById(cropId);
    }

    @CircuitBreaker(name = "cropService", fallbackMethod = "reduceQuantityFallback")
    public CropResponse reduceQuantity(Long cropId, Double quantity) {
        return cropClient.reduceQuantity(cropId, quantity, internalApiKey);
    }

    public CropResponse cropServiceFallback(Long cropId,Throwable ex) {
        throw preserveServiceError(ex, "Crop service is temporarily unavailable");
    }

    public CropResponse reduceQuantityFallback(Long cropId, Double quantity, Throwable ex) {
        throw preserveServiceError(ex, "Crop service is temporarily unavailable");
    }

    private RuntimeException preserveServiceError(Throwable ex, String message) {
        if (ex instanceof RuntimeException runtimeException) {
            return runtimeException;
        }
        return new RuntimeException(message, ex);
    }
}
