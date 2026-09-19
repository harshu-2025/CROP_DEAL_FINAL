package com.cropdeal.crop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.GET, "/crops/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/crops").hasRole("FARMER")
                                .requestMatchers(HttpMethod.PATCH, "/crops/*/reduce-quantity").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/crops/*/reduce-quantity").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/crops/**").hasRole("FARMER")
                                .requestMatchers(HttpMethod.PATCH, "/crops/*/status").hasRole("FARMER")
                                .requestMatchers(HttpMethod.DELETE, "/crops/**").hasRole("FARMER")
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html"
                                ).permitAll()
                                .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}