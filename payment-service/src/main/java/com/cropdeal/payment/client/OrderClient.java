package com.cropdeal.payment.client;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.cropdeal.payment.dto.OrderResponse;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {

    @GetMapping("/orders/{id}")
    OrderResponse getOrderById(@PathVariable Long id);

    @PutMapping("/orders/{id}/payment-success")
    OrderResponse markPaymentSuccess(@PathVariable Long id, @RequestHeader("X-Internal-Key") String internalKey);

    @PutMapping("/orders/{id}/payment-failed")
    OrderResponse markPaymentFailed(@PathVariable Long id, @RequestHeader("X-Internal-Key") String internalKey);
}