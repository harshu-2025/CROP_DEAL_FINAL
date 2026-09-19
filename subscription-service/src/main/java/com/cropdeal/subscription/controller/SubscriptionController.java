package com.cropdeal.subscription.controller;


import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cropdeal.subscription.dto.SubscriptionRequest;
import com.cropdeal.subscription.dto.SubscriptionResponse;
import com.cropdeal.subscription.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @Valid @RequestBody SubscriptionRequest request) {

        SubscriptionResponse response =
                subscriptionService.createSubscription(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> getSubscriptionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                subscriptionService.getSubscriptionById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions() {

        return ResponseEntity.ok(
                subscriptionService.getAllSubscriptions()
        );
    }

    @GetMapping("/dealer/{dealerId}")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByDealer(
            @PathVariable Long dealerId) {

        return ResponseEntity.ok(
                subscriptionService.getSubscriptionsByDealer(dealerId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> updateSubscription(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionRequest request) {

        return ResponseEntity.ok(
                subscriptionService.updateSubscription(id, request)
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<SubscriptionResponse> activateSubscription(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                subscriptionService.activateSubscription(id)
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<SubscriptionResponse> deactivateSubscription(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                subscriptionService.deactivateSubscription(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(
            @PathVariable Long id) {

        subscriptionService.deleteSubscription(id);

        return ResponseEntity.noContent().build();
    }
}