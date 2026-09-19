package com.cropdeal.auction.repository;

import com.cropdeal.auction.entity.Auction;
import com.cropdeal.auction.entity.AuctionStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByFarmerId(Long farmerId);

    List<Auction> findByStatus(AuctionStatus status);

    List<Auction> findByCropId(Long cropId);
    
    boolean existsByCropIdAndStatus(Long cropId, AuctionStatus status);
}