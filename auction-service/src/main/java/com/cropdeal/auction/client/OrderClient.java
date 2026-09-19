package com.cropdeal.auction.client;

import com.cropdeal.auction.dto.OrderRequest;
import com.cropdeal.auction.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {

	@PostMapping("/orders/internal/auction")
	OrderResponse createAuctionOrder(
	        @RequestHeader("X-Internal-Key") String internalKey,
	        @RequestBody OrderRequest request);
}