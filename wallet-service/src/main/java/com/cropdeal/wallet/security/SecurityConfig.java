package com.cropdeal.wallet.security;

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
                                .requestMatchers(HttpMethod.POST, "/wallets/*/credit")
                                .hasRole("DEALER")

                                .requestMatchers(HttpMethod.GET, "/wallets/farmer/*")
                                .hasAnyRole("FARMER", "ADMIN")

                                .requestMatchers(HttpMethod.GET, "/wallets/**")
                                .hasAnyRole("DEALER", "ADMIN")

                                .requestMatchers(HttpMethod.POST, "/wallets/*/reserve")
                                .hasRole("DEALER")

                                .requestMatchers(HttpMethod.POST, "/wallets/*/release")
                                .hasRole("DEALER")

                                .requestMatchers(HttpMethod.POST, "/wallets/*/escrow")
                                .permitAll()

                                .requestMatchers(HttpMethod.POST, "/wallets/*/escrow/rollback")
                                .permitAll()

                                .requestMatchers(HttpMethod.POST, "/wallets/*/escrow/release")
                                .permitAll()

                                .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}