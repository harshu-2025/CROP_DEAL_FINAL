package com.cropdeal.payment.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cropdeal.payment.entity.Payment;
import com.cropdeal.payment.entity.PaymentStatus;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentNumber(String paymentNumber);

    // Returns only the successful payment for an order (there should be at most one)
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId AND p.status = 'SUCCESS'")
    Optional<Payment> findSuccessfulPaymentByOrderId(@Param("orderId") Long orderId);

    // Returns the most recent non-successful payment for retry logic
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId AND p.status <> 'SUCCESS' ORDER BY p.createdAt DESC")
    List<Payment> findNonSuccessfulPaymentsByOrderId(@Param("orderId") Long orderId);

    List<Payment> findByDealerId(Long dealerId);

    List<Payment> findByFarmerId(Long farmerId);

    List<Payment> findByStatus(PaymentStatus status);
}