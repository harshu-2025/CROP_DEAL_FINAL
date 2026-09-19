package com.cropdeal.frontend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ProxyController {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final Map<String, String> services = new LinkedHashMap<>();

    public ProxyController() {
        services.put("auth", "http://localhost:8088");
        services.put("users", "http://localhost:8081");
        services.put("crops", "http://localhost:8082");
        services.put("subscriptions", "http://localhost:8083");
        services.put("orders", "http://localhost:8084");
        services.put("payments", "http://localhost:8085");
        services.put("notifications", "http://localhost:8086");
        services.put("reports", "http://localhost:8087");
        services.put("wallets", "http://localhost:8089");
        services.put("pricing", "http://localhost:8090");
        services.put("auctions", "http://localhost:8091");
    }

    @RequestMapping("/proxy/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest servletRequest) throws IOException, InterruptedException {

        String requestUri = servletRequest.getRequestURI();
        String withoutPrefix = requestUri.substring("/proxy/".length());
        int slashIndex = withoutPrefix.indexOf('/');

        String serviceKey = slashIndex >= 0 ? withoutPrefix.substring(0, slashIndex) : withoutPrefix;
        String remainingPath = slashIndex >= 0 ? withoutPrefix.substring(slashIndex) : "";

        String baseUrl = services.get(serviceKey);
        if (baseUrl == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(("Unknown service: " + serviceKey).getBytes());
        }

        String query = servletRequest.getQueryString();
        String targetUrl = baseUrl + remainingPath + (query == null ? "" : "?" + query);

        byte[] requestBody = StreamUtils.copyToByteArray(servletRequest.getInputStream());
        String method = servletRequest.getMethod();

        HttpRequest.BodyPublisher bodyPublisher = requestBody.length == 0
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofByteArray(requestBody);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .timeout(Duration.ofSeconds(30))
                .method(method, bodyPublisher);

        copyHeader(servletRequest, builder, "Authorization");
        copyHeader(servletRequest, builder, "Content-Type");
        copyHeader(servletRequest, builder, "Accept");
        copyHeader(servletRequest, builder, "X-Internal-Key");

        HttpResponse<byte[]> response = httpClient.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofByteArray()
        );

        HttpHeaders headers = new HttpHeaders();
        response.headers().firstValue("Content-Type").ifPresent(value -> headers.set("Content-Type", value));
        response.headers().firstValue("Content-Disposition").ifPresent(value -> headers.set("Content-Disposition", value));

        return new ResponseEntity<>(response.body(), headers, HttpStatus.valueOf(response.statusCode()));
    }

    private void copyHeader(HttpServletRequest request, HttpRequest.Builder builder, String headerName) {
        String value = request.getHeader(headerName);
        if (value != null && !value.isBlank()) {
            builder.header(headerName, value);
        }
    }
}
