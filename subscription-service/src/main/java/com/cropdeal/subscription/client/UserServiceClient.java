package com.cropdeal.subscription.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import com.cropdeal.subscription.dto.UserResponse;

@Service
public class UserServiceClient {

    private final UserClient userClient;

    public UserServiceClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @CircuitBreaker(
            name = "userService",
            fallbackMethod = "userServiceFallback"
    )
    public UserResponse getDealer(Long dealerId) {
        return userClient.getUserById(dealerId);
    }

    public UserResponse userServiceFallback(
            Long dealerId,
            Throwable ex) {

        if (ex instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        throw new RuntimeException("User service is temporarily unavailable", ex);
    }
}
