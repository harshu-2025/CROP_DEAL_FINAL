package com.cropdeal.order.security;

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
                                .requestMatchers(HttpMethod.POST, "/orders").hasRole("DEALER")
                                .requestMatchers(HttpMethod.PATCH, "/orders/*/accept").hasRole("FARMER")
                                .requestMatchers(HttpMethod.PATCH, "/orders/*/reject").hasRole("FARMER")
                                .requestMatchers(HttpMethod.PATCH, "/orders/*/cancel").hasRole("DEALER")
                                .requestMatchers(HttpMethod.PATCH, "/orders/*/payment-success").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/orders/*/payment-success").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/orders/*/payment-failed").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/orders/*/payment-failed").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/orders/*/complete").hasAnyRole("FARMER", "DEALER")
                                .requestMatchers(HttpMethod.GET, "/orders/*/delivery-qr").hasAnyRole("FARMER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/orders/*/confirm-delivery").hasAnyRole("DEALER", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/orders").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/orders/status/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/orders/dealer/**").hasAnyRole("DEALER", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/orders/farmer/**").hasAnyRole("FARMER", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("FARMER", "DEALER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/orders/**").hasRole("ADMIN")
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html"
                                ).permitAll()
                                .requestMatchers(HttpMethod.POST, "/orders/internal/auction").permitAll()
                                .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}