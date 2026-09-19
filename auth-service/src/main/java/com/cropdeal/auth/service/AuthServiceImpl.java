package com.cropdeal.auth.service;

import com.cropdeal.auth.dto.AuthResponse;
import com.cropdeal.auth.dto.LoginRequest;
import com.cropdeal.auth.dto.RegisterRequest;
import com.cropdeal.auth.entity.AuthUser;
import com.cropdeal.auth.repository.AuthUserRepository;
import com.cropdeal.auth.entity.Role;
import com.cropdeal.auth.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

	private final AuthUserRepository authUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthServiceImpl(AuthUserRepository authUserRepository,
	                       PasswordEncoder passwordEncoder,
	                       JwtService jwtService) {
	    this.authUserRepository = authUserRepository;
	    this.passwordEncoder = passwordEncoder;
	    this.jwtService = jwtService;
	}

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Admin cannot be registered publicly");
        }

        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        AuthUser user = new AuthUser();

        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(request.getRole());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());

        AuthUser savedUser = authUserRepository.save(user);

        AuthResponse response = new AuthResponse();

        response.setUserId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole().name());
        response.setMessage("User registered successfully");

        return response;
    }
    @Override
    public AuthResponse login(LoginRequest request) {

        AuthUser user = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new DisabledException("User account is inactive");
        }

        String token = jwtService.generateToken(user);

        AuthResponse response = new AuthResponse();

        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setMessage("Login successful");
        response.setToken(token);

        return response;
    }
}