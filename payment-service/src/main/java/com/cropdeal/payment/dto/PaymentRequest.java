package com.cropdeal.payment.dto;

import com.cropdeal.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String dealerCardNumber;
    private String dealerCardHolderName;
    private String dealerCardExpiry;
    private String dealerCvv;
    private String farmerCardNumber;
    private String farmerCardHolderName;

    private String dealerUpiId;
    private String farmerUpiId;

    public PaymentRequest() {
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getDealerCardNumber() { return dealerCardNumber; }
    public void setDealerCardNumber(String dealerCardNumber) { this.dealerCardNumber = dealerCardNumber; }
    public String getDealerCardHolderName() { return dealerCardHolderName; }
    public void setDealerCardHolderName(String dealerCardHolderName) { this.dealerCardHolderName = dealerCardHolderName; }
    public String getDealerCardExpiry() { return dealerCardExpiry; }
    public void setDealerCardExpiry(String dealerCardExpiry) { this.dealerCardExpiry = dealerCardExpiry; }
    public String getDealerCvv() { return dealerCvv; }
    public void setDealerCvv(String dealerCvv) { this.dealerCvv = dealerCvv; }
    public String getFarmerCardNumber() { return farmerCardNumber; }
    public void setFarmerCardNumber(String farmerCardNumber) { this.farmerCardNumber = farmerCardNumber; }
    public String getFarmerCardHolderName() { return farmerCardHolderName; }
    public void setFarmerCardHolderName(String farmerCardHolderName) { this.farmerCardHolderName = farmerCardHolderName; }
    public String getDealerUpiId() { return dealerUpiId; }
    public void setDealerUpiId(String dealerUpiId) { this.dealerUpiId = dealerUpiId; }
    public String getFarmerUpiId() { return farmerUpiId; }
    public void setFarmerUpiId(String farmerUpiId) { this.farmerUpiId = farmerUpiId; }
}
