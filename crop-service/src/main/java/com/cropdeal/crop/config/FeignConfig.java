package com.cropdeal.crop.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Value("${internal.api.key:cropdeal-internal-123}")
    private String internalApiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Internal-Key", internalApiKey);

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader != null && !authorizationHeader.isBlank()) {
                    requestTemplate.header("Authorization", authorizationHeader);
                }
            }
        };
    }
}
