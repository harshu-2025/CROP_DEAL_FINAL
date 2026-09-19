package com.cropdeal.wallet.controller;

import com.cropdeal.wallet.dto.EscrowReleaseRequest;
import com.cropdeal.wallet.dto.FarmerWalletResponse;
import com.cropdeal.wallet.dto.ReleaseFundsRequest;
import com.cropdeal.wallet.dto.ReserveFundsRequest;
import com.cropdeal.wallet.dto.WalletCreditRequest;
import com.cropdeal.wallet.dto.WalletResponse;
import com.cropdeal.wallet.dto.WalletTransactionResponse;
import com.cropdeal.wallet.service.WalletService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    @Value("${internal.api.key}")
    private String internalApiKey;

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{dealerId}/credit")
    public ResponseEntity<WalletResponse> creditWallet(
            @PathVariable Long dealerId,
            @Valid @RequestBody WalletCreditRequest request) {

        return ResponseEntity.ok(
                walletService.creditWallet(dealerId, request)
        );
    }

    @GetMapping("/{dealerId}")
    public ResponseEntity<WalletResponse> getWallet(
            @PathVariable Long dealerId) {

        return ResponseEntity.ok(
                walletService.getWalletByDealerId(dealerId)
        );
    }

    @PostMapping("/{dealerId}/reserve")
    public ResponseEntity<WalletResponse> reserveFunds(
            @PathVariable Long dealerId,
            @Valid @RequestBody ReserveFundsRequest request) {

        return ResponseEntity.ok(
                walletService.reserveFunds(dealerId, request)
        );
    }

    @PostMapping("/{dealerId}/release")
    public ResponseEntity<WalletResponse> releaseFunds(
            @PathVariable Long dealerId,
            @Valid @RequestBody ReleaseFundsRequest request) {

        return ResponseEntity.ok(
                walletService.releaseFunds(dealerId, request)
        );
    }
    @GetMapping("/{dealerId}/transactions")
    public ResponseEntity<List<WalletTransactionResponse>> getTransactions(
            @PathVariable Long dealerId) {

        return ResponseEntity.ok(
                walletService.getTransactionsByDealer(dealerId)
        );
    }
    @PostMapping("/{dealerId}/escrow")
    public ResponseEntity<WalletResponse> moveToEscrow(
            @PathVariable Long dealerId,
            @RequestHeader("X-Internal-Key") String internalKey,
            @Valid @RequestBody ReserveFundsRequest request) {

        if (!internalApiKey.equals(internalKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(walletService.moveToEscrow(dealerId, request));
    }

    @PostMapping("/{dealerId}/escrow/rollback")
    public ResponseEntity<WalletResponse> returnEscrowToBlocked(
            @PathVariable Long dealerId,
            @RequestHeader("X-Internal-Key") String internalKey,
            @Valid @RequestBody ReserveFundsRequest request) {

        if (!internalApiKey.equals(internalKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(walletService.returnEscrowToBlocked(dealerId, request));
    }

    @PostMapping("/{dealerId}/escrow/release")
    public ResponseEntity<FarmerWalletResponse> releaseEscrow(
            @PathVariable Long dealerId,
            @RequestHeader("X-Internal-Key") String internalKey,
            @Valid @RequestBody EscrowReleaseRequest request) {

        if (!internalApiKey.equals(internalKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(walletService.releaseEscrow(dealerId, request));
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<FarmerWalletResponse> getFarmerWallet(
            @PathVariable Long farmerId) {

        return ResponseEntity.ok(
                walletService.getFarmerWallet(farmerId)
        );
    }
}