package com.cropdeal.subscription.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cropdeal.subscription.entity.CropSubscription;

public interface CropSubscriptionRepository
extends JpaRepository<CropSubscription, Long> {

List<CropSubscription> findByDealerId(Long dealerId);

List<CropSubscription> findByActiveTrue();

List<CropSubscription> findByCropNameIgnoreCaseAndActiveTrue(String cropName);
}