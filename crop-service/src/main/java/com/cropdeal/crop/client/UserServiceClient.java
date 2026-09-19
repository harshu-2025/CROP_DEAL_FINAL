package com.cropdeal.crop.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import com.cropdeal.crop.dto.UserResponse;

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
    public UserResponse getFarmer(Long farmerId) {

        return userClient.getUserById(farmerId);
    }

    public UserResponse userServiceFallback(
            Long farmerId,
            Throwable ex) {

        if (ex instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        throw new RuntimeException("User service is temporarily unavailable", ex);
    }
}
