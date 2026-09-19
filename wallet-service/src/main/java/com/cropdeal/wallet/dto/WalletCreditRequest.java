package com.cropdeal.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

public class WalletCreditRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @NotNull(message = "Payment method is required")
    private BankTransferMethod paymentMethod;

    @NotBlank(message = "Account holder name is required")
    private String accountHolderName;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^[0-9]{15}$", message = "Account number must be exactly 15 digits")
    private String accountNumber;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Enter a valid IFSC code")
    private String ifscCode;

    @NotBlank(message = "MPIN is required")
    @Pattern(regexp = "^([0-9]{4}|[0-9]{6})$", message = "MPIN must be 4 or 6 digits")
    private String mpin;

    public WalletCreditRequest() {
    }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public BankTransferMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(BankTransferMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public String getMpin() { return mpin; }
    public void setMpin(String mpin) { this.mpin = mpin; }
}
