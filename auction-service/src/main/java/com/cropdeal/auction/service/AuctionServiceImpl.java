package com.cropdeal.auction.service;

import com.cropdeal.auction.client.CropClient;
import com.cropdeal.auction.client.PricingClient;
import com.cropdeal.auction.client.WalletClient;
import com.cropdeal.auction.client.UserClient;
import com.cropdeal.auction.dto.AuctionResponse;
import com.cropdeal.auction.dto.BidResponse;
import com.cropdeal.auction.dto.CreateAuctionRequest;
import com.cropdeal.auction.dto.CropResponse;
import com.cropdeal.auction.dto.PlaceBidRequest;
import com.cropdeal.auction.dto.PricingResponse;
import com.cropdeal.auction.dto.WalletFundsRequest;
import com.cropdeal.auction.dto.UserResponse;
import com.cropdeal.auction.entity.Auction;
import com.cropdeal.auction.entity.Bid;
import com.cropdeal.auction.entity.AuctionStatus;
import com.cropdeal.auction.repository.AuctionRepository;
import com.cropdeal.auction.repository.BidRepository;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import com.cropdeal.auction.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final CropClient cropClient;
    private final PricingClient pricingClient;
    private final WalletClient walletClient;
    private final UserClient userClient;
    private final SimpMessagingTemplate messagingTemplate;

    public AuctionServiceImpl(AuctionRepository auctionRepository, BidRepository bidRepository,
                              CropClient cropClient, PricingClient pricingClient,
                              WalletClient walletClient, UserClient userClient,
                              SimpMessagingTemplate messagingTemplate) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.cropClient = cropClient;
        this.pricingClient = pricingClient;
        this.walletClient = walletClient;
        this.userClient = userClient;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public AuctionResponse createAuction(CreateAuctionRequest request) {

        CropResponse crop = cropClient.getCropById(request.getCropId());
        UserResponse farmer = userClient.getUserById(request.getFarmerId());

        if (!"FARMER".equalsIgnoreCase(farmer.getRole()) || !farmer.isActive()) {
            throw new IllegalArgumentException("Farmer account is not valid");
        }

        if (SecurityUtils.getCurrentAuthUserId() == null ||
                !SecurityUtils.getCurrentAuthUserId().equals(farmer.getAuthUserId())) {
            throw new AccessDeniedException("You can create auctions only for your own farmer profile");
        }

        if (!crop.getFarmerId().equals(request.getFarmerId())) {
            throw new IllegalArgumentException("This crop does not belong to the farmer");
        }

        if (!"AVAILABLE".equalsIgnoreCase(crop.getStatus())) {
            throw new IllegalArgumentException("Only available crops can be auctioned");
        }

        if (auctionRepository.existsByCropIdAndStatus(request.getCropId(), AuctionStatus.OPEN)) {
            throw new IllegalArgumentException("An open auction already exists for this crop");
        }

        if (crop.getGrade() == null || !crop.getGrade().equalsIgnoreCase(request.getGrade())) {
            throw new IllegalArgumentException("Auction grade must match the crop grade");
        }

        Map<String, Object> pricingRequest = new HashMap<>();
        pricingRequest.put("cropName", crop.getCropName());
        pricingRequest.put("location", crop.getLocation());
        pricingRequest.put("grade", crop.getGrade());

        PricingResponse pricingResponse = pricingClient.calculatePrice(pricingRequest);

        LocalDateTime startTime = LocalDateTime.now();

        Auction auction = new Auction();
        auction.setCropId(crop.getId());
        auction.setFarmerId(crop.getFarmerId());
        auction.setBasePrice(pricingResponse.getFairBasePrice());
        auction.setHighestBid(null);
        auction.setHighestBidderId(null);
        auction.setStartTime(startTime);
        auction.setEndTime(startTime.plusMinutes(request.getDurationMinutes()));
        auction.setStatus(AuctionStatus.OPEN);
        auction.setCreatedAt(LocalDateTime.now());
        auction.setUpdatedAt(LocalDateTime.now());

        Auction savedAuction = auctionRepository.save(auction);

        return mapAuctionToResponse(savedAuction);
    }

    @Override
    public BidResponse placeBid(Long auctionId, PlaceBidRequest request) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));

        UserResponse dealer = userClient.getUserById(request.getDealerId());
        if (!"DEALER".equalsIgnoreCase(dealer.getRole()) || !dealer.isActive()) {
            throw new IllegalArgumentException("Dealer account is not valid");
        }
        if (SecurityUtils.getCurrentAuthUserId() == null ||
                !SecurityUtils.getCurrentAuthUserId().equals(dealer.getAuthUserId())) {
            throw new AccessDeniedException("You can place bids only for your own dealer profile");
        }

        if (auction.getStatus() != AuctionStatus.OPEN) {
            throw new IllegalArgumentException("Auction is not open");
        }

        if (LocalDateTime.now().isAfter(auction.getEndTime())) {
            throw new IllegalArgumentException("Auction has ended and is awaiting settlement");
        }

        double currentHighestPrice = auction.getHighestBid() == null
                ? auction.getBasePrice()
                : auction.getHighestBid();

        if (request.getAmount() <= currentHighestPrice) {
            throw new IllegalArgumentException("Bid must be greater than current highest price");
        }

        CropResponse crop = cropClient.getCropById(auction.getCropId());

        double quantity = crop.getQuantity();
        double newTotalAmount = request.getAmount() * quantity;

        Long previousBidderId = auction.getHighestBidderId();
        Double previousBidAmount = auction.getHighestBid();

        if (previousBidderId == null) {

            reserveFunds(request.getDealerId(), newTotalAmount, auctionId);

        } else if (previousBidderId.equals(request.getDealerId())) {

            double previousTotalAmount = previousBidAmount * quantity;
            double additionalAmount = newTotalAmount - previousTotalAmount;

            reserveFunds(request.getDealerId(), additionalAmount, auctionId);

        } else {

            double previousTotalAmount = previousBidAmount * quantity;

            reserveFunds(request.getDealerId(), newTotalAmount, auctionId);

            try {
                releaseFunds(previousBidderId, previousTotalAmount, auctionId);
            } catch (RuntimeException e) {
                releaseFunds(request.getDealerId(), newTotalAmount, auctionId);
                throw e;
            }
        }

        Bid bid = new Bid();
        bid.setAuctionId(auctionId);
        bid.setDealerId(request.getDealerId());
        bid.setAmount(request.getAmount());
        bid.setBidTime(LocalDateTime.now());

        Bid savedBid = bidRepository.save(bid);

        auction.setHighestBid(request.getAmount());
        auction.setHighestBidderId(request.getDealerId());
        auction.setUpdatedAt(LocalDateTime.now());

        auctionRepository.save(auction);

        BidResponse response = mapBidToResponse(savedBid);

        messagingTemplate.convertAndSend(
                "/topic/auctions/" + auctionId,
                response
        );

        return response;
    }
    @Override
    public AuctionResponse getAuctionById(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));

        return mapAuctionToResponse(auction);
    }

    @Override
    public List<AuctionResponse> getOpenAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.OPEN)
                .stream()
                .map(this::mapAuctionToResponse)
                .toList();
    }

    @Override
    public List<BidResponse> getBidsByAuction(Long auctionId) {
        return bidRepository.findByAuctionId(auctionId)
                .stream()
                .map(this::mapBidToResponse)
                .toList();
    }

    private void reserveFunds(Long dealerId, Double amount, Long auctionId) {

        WalletFundsRequest request = new WalletFundsRequest();
        request.setAmount(amount);
        request.setReference("AUCTION-" + auctionId);

        walletClient.reserveFunds(dealerId, request);
    }

    private void releaseFunds(Long dealerId, Double amount, Long auctionId) {

        WalletFundsRequest request = new WalletFundsRequest();
        request.setAmount(amount);
        request.setReference("AUCTION-" + auctionId);

        walletClient.releaseFunds(dealerId, request);
    }

    private AuctionResponse mapAuctionToResponse(Auction auction) {

        AuctionResponse response = new AuctionResponse();
        response.setId(auction.getId());
        response.setCropId(auction.getCropId());
        response.setFarmerId(auction.getFarmerId());
        response.setBasePrice(auction.getBasePrice());
        response.setHighestBid(auction.getHighestBid());
        response.setHighestBidderId(auction.getHighestBidderId());
        response.setStartTime(auction.getStartTime());
        response.setEndTime(auction.getEndTime());
        response.setStatus(auction.getStatus());

        return response;
    }

    private BidResponse mapBidToResponse(Bid bid) {

        BidResponse response = new BidResponse();
        response.setId(bid.getId());
        response.setAuctionId(bid.getAuctionId());
        response.setDealerId(bid.getDealerId());
        response.setAmount(bid.getAmount());
        response.setBidTime(bid.getBidTime());

        return response;
    }
}