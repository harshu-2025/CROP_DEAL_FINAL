package com.cropdeal.subscription.service;


import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import com.cropdeal.subscription.client.UserServiceClient;
import com.cropdeal.subscription.dto.SubscriptionRequest;
import com.cropdeal.subscription.dto.SubscriptionResponse;
import com.cropdeal.subscription.dto.UserResponse;
import com.cropdeal.subscription.entity.CropSubscription;
import com.cropdeal.subscription.exception.SubscriptionNotFoundException;
import com.cropdeal.subscription.repository.CropSubscriptionRepository;
import com.cropdeal.subscription.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionServiceImpl
        implements SubscriptionService {

    private final CropSubscriptionRepository subscriptionRepository;
    private final UserServiceClient userServiceClient;

    public SubscriptionServiceImpl(
            CropSubscriptionRepository subscriptionRepository,
            UserServiceClient userServiceClient) {

        this.subscriptionRepository = subscriptionRepository;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public SubscriptionResponse createSubscription(
            SubscriptionRequest request) {

        UserResponse user =userServiceClient.getDealer(request.getDealerId());

        ensureCurrentDealer(user);

        if (!"DEALER".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Only dealers can create subscriptions");
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException("Dealer account is not active");
        }

        CropSubscription subscription =new CropSubscription();

        subscription.setDealerId(request.getDealerId());
        subscription.setCropName(request.getCropName());
        subscription.setCategory(request.getCategory());
        subscription.setLocation(request.getLocation());
        subscription.setMinimumQuantity(request.getMinimumQuantity());
        subscription.setMaximumPrice(request.getMaximumPrice());

        subscription.setActive(true);
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setUpdatedAt(LocalDateTime.now());

        CropSubscription savedSubscription =subscriptionRepository.save(subscription);

        return mapToResponse(savedSubscription);
    }

    @Override
    public SubscriptionResponse getSubscriptionById(Long id) {

        CropSubscription subscription =
                subscriptionRepository.findById(id).orElseThrow(() ->new SubscriptionNotFoundException(
                                        "Subscription not found with id: " + id));

        ensureSubscriptionOwner(subscription);
        return mapToResponse(subscription);
    }

    @Override
    public List<SubscriptionResponse> getAllSubscriptions() {

        if (!SecurityUtils.hasRole("ADMIN")) {
            throw new AccessDeniedException("Admin access required");
        }
        return subscriptionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SubscriptionResponse> getSubscriptionsByDealer(Long dealerId) {

        UserResponse dealer = userServiceClient.getDealer(dealerId);
        if (!SecurityUtils.hasRole("ADMIN")) {
            ensureCurrentDealer(dealer);
        }

        return subscriptionRepository
                .findByDealerId(dealerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SubscriptionResponse updateSubscription(Long id,SubscriptionRequest request) {

        CropSubscription subscription =
                subscriptionRepository.findById(id) .orElseThrow(() ->new SubscriptionNotFoundException(
                                        "Subscription not found with id: " + id));

        UserResponse user =userServiceClient.getDealer(subscription.getDealerId());

        ensureCurrentDealer(user);

        if (!"DEALER".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException(
                    "Only dealers can update subscriptions");
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException(
                    "Dealer account is not active");
        }

        if (request.getDealerId() != null && !subscription.getDealerId().equals(request.getDealerId())) {
            throw new IllegalArgumentException("Subscription owner cannot be changed");
        }

        subscription.setCropName(request.getCropName());
        subscription.setCategory(request.getCategory());
        subscription.setLocation(request.getLocation());
        subscription.setMinimumQuantity(
                request.getMinimumQuantity());
        subscription.setMaximumPrice(
                request.getMaximumPrice());

        subscription.setUpdatedAt(LocalDateTime.now());

        CropSubscription updatedSubscription =
                subscriptionRepository.save(subscription);

        return mapToResponse(updatedSubscription);
    }

    @Override
    public SubscriptionResponse activateSubscription(Long id) {

        CropSubscription subscription =
                subscriptionRepository.findById(id).orElseThrow(() ->
                                new SubscriptionNotFoundException("Subscription not found with id: " + id));

        ensureSubscriptionOwner(subscription);
        subscription.setActive(true);
        subscription.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(subscriptionRepository.save(subscription));
    }

    @Override
    public SubscriptionResponse deactivateSubscription(Long id) {

        CropSubscription subscription =
                subscriptionRepository.findById(id).orElseThrow(() ->new SubscriptionNotFoundException(
                                        "Subscription not found with id: " + id));

        ensureSubscriptionOwner(subscription);
        subscription.setActive(false);
        subscription.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(subscriptionRepository.save(subscription));
    }

    @Override
    public void deleteSubscription(Long id) {

        CropSubscription subscription =
                subscriptionRepository.findById(id).orElseThrow(() ->new SubscriptionNotFoundException(
                                        "Subscription not found with id: " + id));

        ensureSubscriptionOwner(subscription);
        subscriptionRepository.delete(subscription);
    }

    private void ensureCurrentDealer(UserResponse dealer) {
        if (!SecurityUtils.getCurrentAuthUserId().equals(dealer.getAuthUserId())) {
            throw new AccessDeniedException("You can access only your own dealer data");
        }
    }

    private void ensureSubscriptionOwner(CropSubscription subscription) {
        if (SecurityUtils.hasRole("ADMIN")) {
            return;
        }
        UserResponse dealer = userServiceClient.getDealer(subscription.getDealerId());
        ensureCurrentDealer(dealer);
    }

    private SubscriptionResponse mapToResponse(CropSubscription subscription) {

        SubscriptionResponse response =new SubscriptionResponse();

        response.setId(subscription.getId());
        response.setDealerId(subscription.getDealerId());
        response.setCropName(subscription.getCropName());
        response.setCategory(subscription.getCategory());
        response.setLocation(subscription.getLocation());
        response.setMinimumQuantity(subscription.getMinimumQuantity());
        response.setMaximumPrice(subscription.getMaximumPrice());
        response.setActive(subscription.isActive());
        response.setCreatedAt(subscription.getCreatedAt());
        response.setUpdatedAt(subscription.getUpdatedAt());

        return response;
    }
}