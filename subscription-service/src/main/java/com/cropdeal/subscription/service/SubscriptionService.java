package com.cropdeal.subscription.service;


import java.util.List;

import com.cropdeal.subscription.dto.SubscriptionRequest;
import com.cropdeal.subscription.dto.SubscriptionResponse;

public interface SubscriptionService {

    SubscriptionResponse createSubscription(
            SubscriptionRequest request);

    SubscriptionResponse getSubscriptionById(Long id);

    List<SubscriptionResponse> getAllSubscriptions();

    List<SubscriptionResponse> getSubscriptionsByDealer(Long dealerId);

    SubscriptionResponse updateSubscription(
            Long id,
            SubscriptionRequest request);

    SubscriptionResponse activateSubscription(Long id);

    SubscriptionResponse deactivateSubscription(Long id);

    void deleteSubscription(Long id);
}