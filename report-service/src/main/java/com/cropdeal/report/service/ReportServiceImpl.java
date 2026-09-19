package com.cropdeal.report.service;

import com.cropdeal.report.client.CropClient;
import com.cropdeal.report.client.OrderClient;
import com.cropdeal.report.client.PaymentClient;
import com.cropdeal.report.client.UserClient;
import com.cropdeal.report.dto.AdminReportResponse;
import com.cropdeal.report.dto.CropResponse;
import com.cropdeal.report.dto.DealerPurchaseReportResponse;
import com.cropdeal.report.dto.FarmerSalesReportResponse;
import com.cropdeal.report.dto.InvoiceResponse;
import com.cropdeal.report.dto.OrderReportResponse;
import com.cropdeal.report.dto.OrderResponse;
import com.cropdeal.report.dto.PaymentReportResponse;
import com.cropdeal.report.dto.PaymentResponse;
import com.cropdeal.report.dto.ReceiptResponse;
import com.cropdeal.report.dto.UserResponse;
import com.cropdeal.report.util.ExcelReportGenerator;
import com.cropdeal.report.security.SecurityUtils;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final OrderClient orderClient;
    private final UserClient userClient;
    private final PaymentClient paymentClient;
    private final CropClient cropClient;
    private final ExcelReportGenerator excelReportGenerator;

    public ReportServiceImpl(OrderClient orderClient, UserClient userClient,PaymentClient paymentClient,CropClient cropClient,ExcelReportGenerator excelReportGenerator) {
        this.orderClient = orderClient;
        this.userClient = userClient;
        this.paymentClient=paymentClient;
        this.cropClient=cropClient;
        this.excelReportGenerator = excelReportGenerator;
    }

    @Override
    public FarmerSalesReportResponse getFarmerSalesReport(Long farmerId) {

        UserResponse farmer = userClient.getUserById(farmerId);

        if (!SecurityUtils.hasRole("ADMIN") &&
                !SecurityUtils.getCurrentAuthUserId().equals(farmer.getAuthUserId())) {
            throw new AccessDeniedException("You can view only your own farmer report");
        }

        if (!"FARMER".equalsIgnoreCase(farmer.getRole())) {
            throw new IllegalArgumentException("User is not a farmer");
        }

        List<OrderResponse> orders = orderClient.getOrdersByFarmer(farmerId);

        List<OrderResponse> completedOrders = orders.stream()
                .filter(order -> "COMPLETED".equalsIgnoreCase(order.getStatus()))
                .toList();

        long totalOrders = completedOrders.size();

        double totalQuantitySold = completedOrders.stream()
                .mapToDouble(OrderResponse::getQuantity)
                .sum();

        double totalSalesAmount = completedOrders.stream()
                .mapToDouble(OrderResponse::getTotalAmount)
                .sum();

        FarmerSalesReportResponse response = new FarmerSalesReportResponse();

        response.setFarmerId(farmer.getId());
        response.setFarmerName(farmer.getName());
        response.setTotalOrders(totalOrders);
        response.setTotalQuantitySold(totalQuantitySold);
        response.setTotalSalesAmount(totalSalesAmount);

        return response;
    }
    @Override
    public DealerPurchaseReportResponse getDealerPurchaseReport(Long dealerId) {

        UserResponse dealer = userClient.getUserById(dealerId);

        if (!SecurityUtils.hasRole("ADMIN") &&
                !SecurityUtils.getCurrentAuthUserId().equals(dealer.getAuthUserId())) {
            throw new AccessDeniedException("You can view only your own dealer report");
        }

        if (!"DEALER".equalsIgnoreCase(dealer.getRole())) {
            throw new IllegalArgumentException("User is not a dealer");
        }

        List<OrderResponse> orders = orderClient.getOrdersByDealer(dealerId);

        List<OrderResponse> completedOrders = orders.stream()
                .filter(order -> "COMPLETED".equalsIgnoreCase(order.getStatus()))
                .toList();

        long totalOrders = completedOrders.size();

        double totalQuantityPurchased = completedOrders.stream()
                .mapToDouble(OrderResponse::getQuantity)
                .sum();

        double totalPurchaseAmount = completedOrders.stream()
                .mapToDouble(OrderResponse::getTotalAmount)
                .sum();

        DealerPurchaseReportResponse response = new DealerPurchaseReportResponse();

        response.setDealerId(dealer.getId());
        response.setDealerName(dealer.getName());
        response.setTotalOrders(totalOrders);
        response.setTotalQuantityPurchased(totalQuantityPurchased);
        response.setTotalPurchaseAmount(totalPurchaseAmount);

        return response;
    }
    @Override
    public OrderReportResponse getOrderReport() {

        requireAdmin();
        List<OrderResponse> orders = orderClient.getAllOrders();

        long totalOrders = orders.size();

        long createdOrders = orders.stream()
                .filter(order -> "CREATED".equalsIgnoreCase(order.getStatus()))
                .count();

        long acceptedOrders = orders.stream()
                .filter(order -> "ACCEPTED".equalsIgnoreCase(order.getStatus()))
                .count();

        long rejectedOrders = orders.stream()
                .filter(order -> "REJECTED".equalsIgnoreCase(order.getStatus()))
                .count();

        long paidOrders = orders.stream()
                .filter(order -> "PAID".equalsIgnoreCase(order.getStatus()))
                .count();

        long completedOrders = orders.stream()
                .filter(order -> "COMPLETED".equalsIgnoreCase(order.getStatus()))
                .count();

        long cancelledOrders = orders.stream()
                .filter(order -> "CANCELLED".equalsIgnoreCase(order.getStatus()))
                .count();

        double totalOrderValue = orders.stream()
                .mapToDouble(OrderResponse::getTotalAmount)
                .sum();

        OrderReportResponse response = new OrderReportResponse();

        response.setTotalOrders(totalOrders);
        response.setCreatedOrders(createdOrders);
        response.setAcceptedOrders(acceptedOrders);
        response.setRejectedOrders(rejectedOrders);
        response.setPaidOrders(paidOrders);
        response.setCompletedOrders(completedOrders);
        response.setCancelledOrders(cancelledOrders);
        response.setTotalOrderValue(totalOrderValue);

        return response;
    }
    @Override
    public PaymentReportResponse getPaymentReport() {

        requireAdmin();
        List<PaymentResponse> payments = paymentClient.getAllPayments();

        long totalPayments = payments.size();

        long createdPayments = payments.stream()
                .filter(payment -> "CREATED".equalsIgnoreCase(payment.getStatus()))
                .count();

        long processingPayments = payments.stream()
                .filter(payment -> "PROCESSING".equalsIgnoreCase(payment.getStatus()))
                .count();

        long successfulPayments = payments.stream()
                .filter(payment -> "SUCCESS".equalsIgnoreCase(payment.getStatus()))
                .count();

        long failedPayments = payments.stream()
                .filter(payment -> "FAILED".equalsIgnoreCase(payment.getStatus()))
                .count();

        double totalSuccessfulAmount = payments.stream()
                .filter(payment -> "SUCCESS".equalsIgnoreCase(payment.getStatus()))
                .mapToDouble(PaymentResponse::getAmount)
                .sum();

        PaymentReportResponse response = new PaymentReportResponse();
  
        response.setTotalPayments(totalPayments);
        response.setCreatedPayments(createdPayments);
        response.setProcessingPayments(processingPayments);
        response.setSuccessfulPayments(successfulPayments);
        response.setFailedPayments(failedPayments);
        response.setTotalSuccessfulAmount(totalSuccessfulAmount);

        return response;
    } 
    @Override
    public AdminReportResponse getAdminReport() {

        requireAdmin();
        List<OrderResponse> orders = orderClient.getAllOrders();
        List<PaymentResponse> payments = paymentClient.getAllPayments();

        long totalOrders = orders.size();

        long completedOrders = orders.stream()
                .filter(order -> "COMPLETED".equalsIgnoreCase(order.getStatus()))
                .count();

        long successfulPayments = payments.stream()
                .filter(payment -> "SUCCESS".equalsIgnoreCase(payment.getStatus()))
                .count();

        long failedPayments = payments.stream()
                .filter(payment -> "FAILED".equalsIgnoreCase(payment.getStatus()))
                .count();

        double totalRevenue = payments.stream()
                .filter(payment -> "SUCCESS".equalsIgnoreCase(payment.getStatus()))
                .mapToDouble(PaymentResponse::getAmount)
                .sum();

        AdminReportResponse response = new AdminReportResponse();

        response.setTotalOrders(totalOrders);
        response.setCompletedOrders(completedOrders);
        response.setSuccessfulPayments(successfulPayments);
        response.setFailedPayments(failedPayments);
        response.setTotalRevenue(totalRevenue);

        return response;
    }
    @Override
    public InvoiceResponse getInvoice(Long orderId) {

        OrderResponse order = orderClient.getOrderById(orderId);

        if (!"COMPLETED".equalsIgnoreCase(order.getStatus()) && !"PAID".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalArgumentException("Invoice can be generated only for a paid or completed order");
        }

        UserResponse farmer = userClient.getUserById(order.getFarmerId());
        UserResponse dealer = userClient.getUserById(order.getDealerId());
        ensureReportPartyOrAdmin(farmer, dealer);
        CropResponse crop = cropClient.getCropById(order.getCropId());

        InvoiceResponse response = new InvoiceResponse();

        response.setOrderNumber(order.getOrderNumber());
        response.setFarmerName(farmer.getName());
        response.setDealerName(dealer.getName());
        response.setCropName(crop.getCropName());
        response.setQuantity(order.getQuantity());
        response.setAgreedPrice(order.getAgreedPrice());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderStatus(order.getStatus());

        return response;
    }
    @Override
    public ReceiptResponse getReceipt(Long paymentId) {

        PaymentResponse payment = paymentClient.getPaymentById(paymentId);

        if (!"SUCCESS".equalsIgnoreCase(payment.getStatus())) {
            throw new IllegalArgumentException("Receipt can be generated only for a successful payment");
        }

        OrderResponse order = orderClient.getOrderById(payment.getOrderId());

        UserResponse dealer = userClient.getUserById(payment.getDealerId());
        UserResponse farmer = userClient.getUserById(payment.getFarmerId());
        ensureReportPartyOrAdmin(farmer, dealer);

        ReceiptResponse response = new ReceiptResponse();

        response.setPaymentNumber(payment.getPaymentNumber());
        response.setOrderNumber(order.getOrderNumber());
        response.setDealerName(dealer.getName());
        response.setFarmerName(farmer.getName());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentStatus(payment.getStatus());
        response.setTransactionReference(payment.getTransactionReference());

        return response;
    }
    @Override
    public byte[] exportOrderReport() {

        requireAdmin();
        List<OrderResponse> orders = orderClient.getAllOrders();

        return excelReportGenerator.generateOrderReport(orders);
    }

    private void ensureReportPartyOrAdmin(UserResponse farmer, UserResponse dealer) {
        if (SecurityUtils.hasRole("ADMIN")) {
            return;
        }
        Long currentUserId = SecurityUtils.getCurrentAuthUserId();
        if (!currentUserId.equals(farmer.getAuthUserId()) &&
                !currentUserId.equals(dealer.getAuthUserId())) {
            throw new AccessDeniedException("You can access reports only for your own transactions");
        }
    }

    private void requireAdmin() {
        if (!SecurityUtils.hasRole("ADMIN")) {
            throw new AccessDeniedException("Admin access required");
        }
    }
}
