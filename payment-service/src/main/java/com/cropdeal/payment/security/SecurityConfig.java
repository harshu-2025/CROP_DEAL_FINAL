package com.cropdeal.payment.security;

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
                                .requestMatchers(HttpMethod.POST, "/payments").hasRole("DEALER")
                                .requestMatchers(HttpMethod.PATCH, "/payments/*/process").hasRole("DEALER")
                                .requestMatchers(HttpMethod.PATCH, "/payments/*/success").hasRole("DEALER")
                                .requestMatchers(HttpMethod.PATCH, "/payments/*/fail").hasRole("DEALER")
                                .requestMatchers(HttpMethod.GET, "/payments").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/payments/status/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/payments/**").hasAnyRole("DEALER", "FARMER", "ADMIN")
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