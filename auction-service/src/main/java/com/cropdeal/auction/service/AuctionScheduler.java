package com.cropdeal.auction.service;

import com.cropdeal.auction.client.CropClient;
import com.cropdeal.auction.client.OrderClient;
import com.cropdeal.auction.client.WalletClient;
import com.cropdeal.auction.dto.CropResponse;
import com.cropdeal.auction.dto.OrderRequest;
import com.cropdeal.auction.dto.WalletFundsRequest;
import com.cropdeal.auction.entity.Auction;
import com.cropdeal.auction.entity.AuctionStatus;
import com.cropdeal.auction.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuctionScheduler {

    private final AuctionRepository auctionRepository;
    private final CropClient cropClient;
    private final OrderClient orderClient;
    private final WalletClient walletClient;

    @Value("${internal.api.key}")
    private String internalApiKey;

    public AuctionScheduler(AuctionRepository auctionRepository, CropClient cropClient,
                            OrderClient orderClient, WalletClient walletClient) {
        this.auctionRepository = auctionRepository;
        this.cropClient = cropClient;
        this.orderClient = orderClient;
        this.walletClient = walletClient;
    }

    @Scheduled(fixedRate = 60000)
    public void closeExpiredAuctions() {

        List<Auction> openAuctions = auctionRepository.findByStatus(AuctionStatus.OPEN);
        LocalDateTime now = LocalDateTime.now();

        for (Auction auction : openAuctions) {
            if (!now.isAfter(auction.getEndTime())) {
                continue;
            }

            try {
                closeAuction(auction);
            } catch (RuntimeException e) {
                System.err.println("Could not close auction " + auction.getId() + ": " + e.getMessage());
            }
        }
    }

    private void closeAuction(Auction auction) {

        if (auction.getHighestBidderId() != null) {
            CropResponse crop = cropClient.getCropById(auction.getCropId());
            double totalAmount = auction.getHighestBid() * crop.getQuantity();

            WalletFundsRequest walletRequest = new WalletFundsRequest();
            walletRequest.setAmount(totalAmount);
            walletRequest.setReference("AUCTION-" + auction.getId());

            walletClient.moveToEscrow(
                    auction.getHighestBidderId(),
                    internalApiKey,
                    walletRequest
            );

            try {
                OrderRequest orderRequest = new OrderRequest();
                orderRequest.setDealerId(auction.getHighestBidderId());
                orderRequest.setCropId(auction.getCropId());
                orderRequest.setQuantity(crop.getQuantity());
                orderRequest.setAgreedPrice(auction.getHighestBid());

                orderClient.createAuctionOrder(internalApiKey, orderRequest);
            } catch (RuntimeException e) {
                walletClient.returnEscrowToBlocked(
                        auction.getHighestBidderId(),
                        internalApiKey,
                        walletRequest
                );
                throw e;
            }
        }

        auction.setStatus(AuctionStatus.CLOSED);
        auction.setUpdatedAt(LocalDateTime.now());
        auctionRepository.save(auction);
    }
}
