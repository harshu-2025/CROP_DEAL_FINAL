package com.cropdeal.wallet.entity;

public enum WalletTransactionType {
    CASH_CREDIT,
    IMPS_CREDIT,
    NEFT_CREDIT,
    RCTS_CREDIT, // Legacy database rows only; wallet top-up no longer accepts RCTS.
    BID_RESERVED,
    BID_RELEASED,
    ESCROW_TRANSFER,
    ESCROW_RELEASED,
    FARMER_CREDIT,
    REFUND
}
