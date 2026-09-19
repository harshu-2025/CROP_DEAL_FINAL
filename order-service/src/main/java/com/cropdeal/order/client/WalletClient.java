package com.cropdeal.order.client;

import com.cropdeal.order.dto.EscrowReleaseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletClient {

    @PostMapping("/wallets/{dealerId}/escrow/release")
    void releaseEscrow(
            @PathVariable Long dealerId,
            @RequestHeader("X-Internal-Key") String internalKey,
            @RequestBody EscrowReleaseRequest request
    );
}
