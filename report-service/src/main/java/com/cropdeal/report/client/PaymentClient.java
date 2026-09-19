package com.cropdeal.report.client;

import com.cropdeal.report.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {

    @GetMapping("/payments")
    List<PaymentResponse> getAllPayments();
    
    @GetMapping("/payments/{id}")
    PaymentResponse getPaymentById(@PathVariable Long id);
}
