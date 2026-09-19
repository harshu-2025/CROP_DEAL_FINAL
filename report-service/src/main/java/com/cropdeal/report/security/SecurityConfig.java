package com.cropdeal.report.security;

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
                                .requestMatchers(HttpMethod.GET, "/reports/farmer/**")
                                .hasRole("FARMER")

                                .requestMatchers(HttpMethod.GET, "/reports/dealer/**")
                                .hasRole("DEALER")

                                .requestMatchers(HttpMethod.GET, "/reports/admin")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/reports/orders")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/reports/payments")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/reports/orders/excel")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/reports/invoice/**")
                                .hasAnyRole("FARMER", "DEALER", "ADMIN")

                                .requestMatchers(HttpMethod.GET, "/reports/receipt/**")
                                .hasAnyRole("FARMER", "DEALER", "ADMIN")
                                
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