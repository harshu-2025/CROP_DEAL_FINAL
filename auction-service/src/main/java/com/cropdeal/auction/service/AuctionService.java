package com.cropdeal.auction.service;

import java.util.List;

import com.cropdeal.auction.dto.AuctionResponse;
import com.cropdeal.auction.dto.BidResponse;
import com.cropdeal.auction.dto.CreateAuctionRequest;
import com.cropdeal.auction.dto.PlaceBidRequest;

public interface AuctionService {

    AuctionResponse createAuction(CreateAuctionRequest request);
    
    BidResponse placeBid(Long auctionId, PlaceBidRequest request);
    
    AuctionResponse getAuctionById(Long auctionId);

    List<AuctionResponse> getOpenAuctions();

    List<BidResponse> getBidsByAuction(Long auctionId);
}