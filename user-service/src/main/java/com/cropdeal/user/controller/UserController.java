package com.cropdeal.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import com.cropdeal.user.dto.UserRequest;
import com.cropdeal.user.dto.UserResponse;
import com.cropdeal.user.entity.Role;
import com.cropdeal.user.service.UserService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/users")
public class UserController {

    @Value("${internal.api.key:cropdeal-internal-123}")
    private String internalApiKey;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {

        UserResponse response = userService.createUser(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/internal/{id}")
    public ResponseEntity<UserResponse> getInternalUser(@PathVariable Long id,@RequestHeader("X-Internal-Key") String internalKey) {
        if(!internalApiKey.equals(internalKey)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {

        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/auth/{authUserId}")
    public ResponseEntity<UserResponse> getUserByAuthUserId(@PathVariable Long authUserId) {

        return ResponseEntity.ok(userService.getUserByAuthUserId(authUserId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,@Valid @RequestBody UserRequest request) {

        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {

        return ResponseEntity.ok(userService.activateUser(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {

        return ResponseEntity.ok(userService.deactivateUser(id));
    }
}