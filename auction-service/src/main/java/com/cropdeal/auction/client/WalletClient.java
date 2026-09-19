package com.cropdeal.auction.client;

import com.cropdeal.auction.dto.WalletFundsRequest;
import com.cropdeal.auction.dto.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletClient {

    @GetMapping("/wallets/{dealerId}")
    WalletResponse getWallet(@PathVariable Long dealerId);

    @PostMapping("/wallets/{dealerId}/reserve")
    WalletResponse reserveFunds(
            @PathVariable Long dealerId,
            @RequestBody WalletFundsRequest request
    );

    @PostMapping("/wallets/{dealerId}/release")
    WalletResponse releaseFunds(
            @PathVariable Long dealerId,
            @RequestBody WalletFundsRequest request
    );
    @PostMapping("/wallets/{dealerId}/escrow")
    WalletResponse moveToEscrow(
            @PathVariable Long dealerId,
            @RequestHeader("X-Internal-Key") String internalKey,
            @RequestBody WalletFundsRequest request
    );

    @PostMapping("/wallets/{dealerId}/escrow/rollback")
    WalletResponse returnEscrowToBlocked(
            @PathVariable Long dealerId,
            @RequestHeader("X-Internal-Key") String internalKey,
            @RequestBody WalletFundsRequest request
    );
}