package com.cropdeal.payment.service;

import java.util.List;

import com.cropdeal.payment.dto.PaymentRequest;
import com.cropdeal.payment.dto.PaymentResponse;
import com.cropdeal.payment.entity.PaymentStatus;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    PaymentResponse getPaymentByOrderId(Long orderId);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByStatus(PaymentStatus status);

    PaymentResponse processPayment(Long id);

    PaymentResponse markPaymentSuccess(Long id,String transactionReference);

    PaymentResponse markPaymentFailed(Long id);
}