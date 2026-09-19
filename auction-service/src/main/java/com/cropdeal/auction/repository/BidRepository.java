package com.cropdeal.auction.repository;

import com.cropdeal.auction.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByAuctionId(Long auctionId);

    List<Bid> findByDealerId(Long dealerId);

    List<Bid> findByAuctionIdOrderByAmountDesc(Long auctionId);
}