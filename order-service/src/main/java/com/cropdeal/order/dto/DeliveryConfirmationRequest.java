package com.cropdeal.order.dto;

import jakarta.validation.constraints.NotBlank;

public class DeliveryConfirmationRequest {

    @NotBlank
    private String qrCode;

    public DeliveryConfirmationRequest() {
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}