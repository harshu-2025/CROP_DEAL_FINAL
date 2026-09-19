package com.cropdeal.auth.service;

import com.cropdeal.auth.dto.AuthResponse;
import com.cropdeal.auth.dto.LoginRequest;
import com.cropdeal.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);
    
    AuthResponse login(LoginRequest request);
}