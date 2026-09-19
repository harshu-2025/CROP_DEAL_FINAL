package com.cropdeal.order.client;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import com.cropdeal.order.dto.UserResponse;

@Service
public class UserServiceClient {

    private final UserClient userClient;

    public UserServiceClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @CircuitBreaker(name = "userService",fallbackMethod = "userServiceFallback")
    public UserResponse getUser(Long userId) {

        return userClient.getUserById(userId);
    }

    public UserResponse userServiceFallback(Long userId,Throwable ex) {

        if (ex instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        throw new RuntimeException("User service is temporarily unavailable", ex);
    }
}
