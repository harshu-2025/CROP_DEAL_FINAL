package com.cropdeal.crop.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cropdeal.crop.dto.UserResponse;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {
	@GetMapping("/users/{id}")
	UserResponse getUserById(@PathVariable("id") Long id);
}
