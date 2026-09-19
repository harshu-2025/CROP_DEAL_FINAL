package com.cropdeal.auction.controller;

import com.cropdeal.auction.dto.AuctionResponse;
import com.cropdeal.auction.dto.BidResponse;
import com.cropdeal.auction.dto.CreateAuctionRequest;
import com.cropdeal.auction.dto.PlaceBidRequest;
import com.cropdeal.auction.service.AuctionService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(
            @Valid @RequestBody CreateAuctionRequest request) {

        return ResponseEntity.ok(
                auctionService.createAuction(request)
        );
    }
    @GetMapping("/{auctionId}")
    public ResponseEntity<AuctionResponse> getAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(auctionService.getAuctionById(auctionId));
    }

    @GetMapping("/open")
    public ResponseEntity<List<AuctionResponse>> getOpenAuctions() {
        return ResponseEntity.ok(auctionService.getOpenAuctions());
    }
    @PostMapping("/{auctionId}/bids")
    public ResponseEntity<BidResponse> placeBid(
            @PathVariable Long auctionId,
            @Valid @RequestBody PlaceBidRequest request) {

        return ResponseEntity.ok(
                auctionService.placeBid(auctionId, request)
        );
    }

    @GetMapping("/{auctionId}/bids")
    public ResponseEntity<List<BidResponse>> getAuctionBids(@PathVariable Long auctionId) {
        return ResponseEntity.ok(auctionService.getBidsByAuction(auctionId));
    }
}