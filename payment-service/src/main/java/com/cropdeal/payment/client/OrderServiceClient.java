package com.cropdeal.payment.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.cropdeal.payment.dto.OrderResponse;

@Service
public class OrderServiceClient {

    private final OrderClient orderClient;

    @Value("${internal.api.key}")
    private String internalApiKey;

    public OrderServiceClient(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderFallback")
    public OrderResponse getOrder(Long orderId) {
        return orderClient.getOrderById(orderId);
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "updateOrderFallback")
    public OrderResponse markPaymentSuccess(Long orderId) {
        return orderClient.markPaymentSuccess(orderId, internalApiKey);
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "updateOrderFallback")
    public OrderResponse markPaymentFailed(Long orderId) {
        return orderClient.markPaymentFailed(orderId, internalApiKey);
    }

    public OrderResponse getOrderFallback(Long orderId, Throwable ex) {
        throw preserveServiceError(ex, "Order service is temporarily unavailable");
    }

    public OrderResponse updateOrderFallback(Long orderId, Throwable ex) {
        throw preserveServiceError(ex, "Unable to update order because Order Service is unavailable");
    }

    private RuntimeException preserveServiceError(Throwable ex, String message) {
        if (ex instanceof RuntimeException runtimeException) {
            return runtimeException;
        }
        return new RuntimeException(message, ex);
    }
}
