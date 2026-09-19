package com.cropdeal.wallet.service;

import java.util.List;

import com.cropdeal.wallet.dto.EscrowReleaseRequest;
import com.cropdeal.wallet.dto.FarmerWalletResponse;
import com.cropdeal.wallet.dto.ReleaseFundsRequest;
import com.cropdeal.wallet.dto.ReserveFundsRequest;
import com.cropdeal.wallet.dto.WalletCreditRequest;
import com.cropdeal.wallet.dto.WalletResponse;
import com.cropdeal.wallet.dto.WalletTransactionResponse;

public interface WalletService {

    WalletResponse creditWallet(Long dealerId, WalletCreditRequest request);

    WalletResponse getWalletByDealerId(Long dealerId);

    WalletResponse reserveFunds(Long dealerId, ReserveFundsRequest request);

    WalletResponse releaseFunds(Long dealerId, ReleaseFundsRequest request);
    
    List<WalletTransactionResponse> getTransactionsByDealer(Long dealerId);
    
    WalletResponse moveToEscrow(Long dealerId, ReserveFundsRequest request);

    WalletResponse returnEscrowToBlocked(Long dealerId, ReserveFundsRequest request);
    
    FarmerWalletResponse releaseEscrow(Long dealerId, EscrowReleaseRequest request);

    FarmerWalletResponse getFarmerWallet(Long farmerId);
}