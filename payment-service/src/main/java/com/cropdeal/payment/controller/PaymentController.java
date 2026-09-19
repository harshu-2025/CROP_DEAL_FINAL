package com.cropdeal.payment.controller;


import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cropdeal.payment.dto.PaymentRequest;
import com.cropdeal.payment.dto.PaymentResponse;
import com.cropdeal.payment.entity.PaymentStatus;
import com.cropdeal.payment.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {

        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {

        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId) {

        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable PaymentStatus status) {

        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }

    @PatchMapping("/{id}/process")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable Long id) {

        return ResponseEntity.ok(paymentService.processPayment(id));
    }

    @PatchMapping("/{id}/success")
    public ResponseEntity<PaymentResponse> markPaymentSuccess(
            @PathVariable Long id,
            @RequestParam String transactionReference) {

        return ResponseEntity.ok(paymentService.markPaymentSuccess(id, transactionReference));
    }

    @PatchMapping("/{id}/fail")
    public ResponseEntity<PaymentResponse> markPaymentFailed(@PathVariable Long id) {

        return ResponseEntity.ok(paymentService.markPaymentFailed(id));
    }
}