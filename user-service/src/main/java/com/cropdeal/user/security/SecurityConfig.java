package com.cropdeal.user.security;

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
                .cors(cors -> {})

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth ->
                        auth
                        
                                .requestMatchers(HttpMethod.GET, "/users/internal/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users")
                                .authenticated()

                                .requestMatchers(HttpMethod.GET, "/users")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/users/role/**")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/users/**")
                                .hasAnyRole("FARMER", "DEALER", "ADMIN")

                                .requestMatchers(HttpMethod.PUT, "/users/**")
                                .hasAnyRole("FARMER", "DEALER", "ADMIN")

                                .requestMatchers(HttpMethod.PATCH, "/users/*/activate")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.PATCH, "/users/*/deactivate")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.DELETE, "/users/**")
                                .hasRole("ADMIN")
                                
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