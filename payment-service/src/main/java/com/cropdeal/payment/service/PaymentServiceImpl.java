package com.cropdeal.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import com.cropdeal.payment.client.OrderServiceClient;
import com.cropdeal.payment.dto.OrderResponse;
import com.cropdeal.payment.dto.PaymentRequest;
import com.cropdeal.payment.dto.PaymentResponse;
import com.cropdeal.payment.entity.Payment;
import com.cropdeal.payment.entity.PaymentMethod;
import com.cropdeal.payment.entity.PaymentStatus;
import com.cropdeal.payment.exception.PaymentNotFoundException;
import com.cropdeal.payment.messaging.PaymentEvent;
import com.cropdeal.payment.messaging.PaymentEventProducer;
import com.cropdeal.payment.repository.PaymentRepository;
import com.cropdeal.payment.security.SecurityUtils;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;
    private final PaymentEventProducer paymentEventProducer;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderServiceClient orderServiceClient,
                              PaymentEventProducer paymentEventProducer) {
        this.paymentRepository = paymentRepository;
        this.orderServiceClient = orderServiceClient;
        this.paymentEventProducer = paymentEventProducer;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        // ── 1. Check DB for an existing SUCCESS payment first (authoritative guard) ──
        // This is done BEFORE calling the order service so we never create a duplicate
        // even if the order service's paymentStatus is stale or mismatched.
        Optional<Payment> existingSuccess = paymentRepository.findSuccessfulPaymentByOrderId(request.getOrderId());
        if (existingSuccess.isPresent()) {
            return mapToResponse(existingSuccess.get());
        }

        // ── 2. Validate order state ──
        OrderResponse order = orderServiceClient.getOrder(request.getOrderId());

        if ("SUCCESS".equalsIgnoreCase(order.getPaymentStatus())) {
            // Order says paid but we have no success record — data inconsistency, block it
            throw new IllegalArgumentException("Order is already marked as paid. Payment is not required.");
        }

        if (!"ACCEPTED".equalsIgnoreCase(order.getStatus()) && !"PAYMENT_FAILED".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalArgumentException("Payment can be made only for an accepted order");
        }

        validateFakePaymentDetails(request);

        // ── 3. Find most recent non-success payment (for retry) ──
        List<Payment> nonSuccessPayments = paymentRepository.findNonSuccessfulPaymentsByOrderId(request.getOrderId());
        Payment payment = nonSuccessPayments.isEmpty() ? null : nonSuccessPayments.get(0);

        if (payment != null && payment.getStatus() != PaymentStatus.FAILED) {
            throw new IllegalArgumentException("A payment is already in progress for this order");
        }

        if (payment == null) {
            payment = new Payment();
            payment.setPaymentNumber("PAY-" + Year.now().getValue() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            payment.setOrderId(order.getId());
            payment.setDealerId(order.getDealerId());
            payment.setFarmerId(order.getFarmerId());
            payment.setAmount(order.getTotalAmount());
            payment.setCreatedAt(LocalDateTime.now());
        }

        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setTransactionReference(null);
        payment.setCompletedAt(null);

        Payment savedPayment = paymentRepository.save(payment);

        try {
            orderServiceClient.markPaymentSuccess(order.getId());

            Payment completedPayment = completePaymentRecord(savedPayment, request.getPaymentMethod());

            publishSuccess(completedPayment);
            return mapToResponse(completedPayment);

        } catch (RuntimeException ex) {
            try {
                // Double-check: maybe markPaymentSuccess succeeded but something else threw
                Optional<Payment> successCheck = paymentRepository.findSuccessfulPaymentByOrderId(order.getId());
                if (successCheck.isPresent()) {
                    return mapToResponse(successCheck.get());
                }
                OrderResponse latestOrder = orderServiceClient.getOrder(order.getId());
                if ("SUCCESS".equalsIgnoreCase(latestOrder.getPaymentStatus())) {
                    Payment completedPayment = completePaymentRecord(savedPayment, request.getPaymentMethod());
                    publishSuccess(completedPayment);
                    return mapToResponse(completedPayment);
                }
                orderServiceClient.markPaymentFailed(order.getId());
            } catch (RuntimeException ignored) {
                // Keep the original failure. The request can be retried safely.
            }

            savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setCompletedAt(LocalDateTime.now());
            Payment failedPayment = paymentRepository.save(savedPayment);
            publishFailed(failedPayment);
            throw ex;
        }
    }

    private void validateFakePaymentDetails(PaymentRequest request) {
        if (request.getPaymentMethod() == PaymentMethod.CARD) {
            if (!isSixteenDigits(request.getDealerCardNumber()) || !isSixteenDigits(request.getFarmerCardNumber())) {
                throw new IllegalArgumentException("Dealer and farmer card numbers must be 16 digits for fake card payment");
            }
            if (isBlank(request.getDealerCardHolderName()) || isBlank(request.getFarmerCardHolderName())) {
                throw new IllegalArgumentException("Dealer and farmer card holder names are required");
            }
            if (request.getDealerCvv() == null || !request.getDealerCvv().matches("^[0-9]{3}$")) {
                throw new IllegalArgumentException("Dealer CVV must be 3 digits");
            }
            if (request.getDealerCardExpiry() == null || !request.getDealerCardExpiry().matches("^(0[1-9]|1[0-2])/([0-9]{2})$")) {
                throw new IllegalArgumentException("Dealer card expiry must be in MM/YY format");
            }
        }

        if (request.getPaymentMethod() == PaymentMethod.UPI) {
            if (!isValidUpi(request.getDealerUpiId()) || !isValidUpi(request.getFarmerUpiId())) {
                throw new IllegalArgumentException("Valid dealer and farmer UPI IDs are required for fake UPI payment");
            }
        }

        // CASH is a fake cash settlement for this project, so no card/UPI details are required.
    }

    private boolean isSixteenDigits(String value) {
        return value != null && value.matches("^[0-9]{16}$");
    }

    private boolean isValidUpi(String value) {
        return value != null && value.matches("^[A-Za-z0-9._-]{2,}@[A-Za-z]{2,}$");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = findPayment(id);
        orderServiceClient.getOrder(payment.getOrderId());
        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        orderServiceClient.getOrder(orderId);
        // Prefer returning the successful payment if one exists
        Payment payment = paymentRepository.findSuccessfulPaymentByOrderId(orderId)
                .orElseGet(() -> {
                    List<Payment> others = paymentRepository.findNonSuccessfulPaymentsByOrderId(orderId);
                    if (others.isEmpty()) {
                        throw new PaymentNotFoundException("Payment not found for order id: " + orderId);
                    }
                    return others.get(0);
                });
        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        requireAdmin();
        return paymentRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        requireAdmin();
        return paymentRepository.findByStatus(status).stream().map(this::mapToResponse).toList();
    }

    @Override
    public PaymentResponse processPayment(Long id) {
        Payment payment = findPayment(id);
        orderServiceClient.getOrder(payment.getOrderId());

        if (payment.getStatus() != PaymentStatus.CREATED && payment.getStatus() != PaymentStatus.FAILED) {
            throw new IllegalArgumentException("Only created or failed payments can be processed");
        }

        payment.setStatus(PaymentStatus.PROCESSING);
        return mapToResponse(paymentRepository.save(payment));
    }

    @Override
    public PaymentResponse markPaymentSuccess(Long id, String transactionReference) {
        Payment payment = findPayment(id);

        if (payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new IllegalArgumentException("Only processing payments can be marked successful");
        }

        orderServiceClient.getOrder(payment.getOrderId());
        orderServiceClient.markPaymentSuccess(payment.getOrderId());

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionReference(transactionReference);
        payment.setCompletedAt(LocalDateTime.now());
        Payment updatedPayment = paymentRepository.save(payment);
        publishSuccess(updatedPayment);
        return mapToResponse(updatedPayment);
    }

    private Payment completePaymentRecord(Payment payment, PaymentMethod paymentMethod) {
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionReference(
                "FAKE-" + paymentMethod.name() + "-"
                        + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );
        payment.setCompletedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    @Override
    public PaymentResponse markPaymentFailed(Long id) {
        Payment payment = findPayment(id);
        orderServiceClient.getOrder(payment.getOrderId());

        if (payment.getStatus() != PaymentStatus.CREATED && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new IllegalArgumentException("Only created or processing payments can be marked failed");
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setCompletedAt(LocalDateTime.now());
        Payment updatedPayment = paymentRepository.save(payment);
        orderServiceClient.markPaymentFailed(payment.getOrderId());
        publishFailed(updatedPayment);
        return mapToResponse(updatedPayment);
    }

    private void publishSuccess(Payment payment) {
        PaymentEvent event = buildEvent(payment, "Payment successful for order: " + payment.getOrderId());
        paymentEventProducer.publishPaymentSuccess(event);
    }

    private void publishFailed(Payment payment) {
        PaymentEvent event = buildEvent(payment, "Payment failed for order: " + payment.getOrderId());
        paymentEventProducer.publishPaymentFailed(event);
    }

    private PaymentEvent buildEvent(Payment payment, String message) {
        PaymentEvent event = new PaymentEvent();
        event.setPaymentId(payment.getId());
        event.setPaymentNumber(payment.getPaymentNumber());
        event.setOrderId(payment.getOrderId());
        event.setDealerId(payment.getDealerId());
        event.setFarmerId(payment.getFarmerId());
        event.setAmount(payment.getAmount());
        event.setStatus(payment.getStatus().name());
        event.setMessage(message);
        return event;
    }

    private void requireAdmin() {
        if (!SecurityUtils.hasRole("ADMIN")) {
            throw new AccessDeniedException("Admin access required");
        }
    }

    private Payment findPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
    }

    private PaymentResponse mapToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentNumber(payment.getPaymentNumber());
        response.setOrderId(payment.getOrderId());
        response.setDealerId(payment.getDealerId());
        response.setFarmerId(payment.getFarmerId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus());
        response.setTransactionReference(payment.getTransactionReference());
        response.setCreatedAt(payment.getCreatedAt());
        response.setCompletedAt(payment.getCompletedAt());
        return response;
    }
}
