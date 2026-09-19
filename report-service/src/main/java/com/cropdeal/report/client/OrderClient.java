package com.cropdeal.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cropdeal.report.dto.OrderResponse;

import java.util.List;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {

    @GetMapping("/orders")
    List<OrderResponse> getAllOrders();

    @GetMapping("/orders/{id}")
    OrderResponse getOrderById(@PathVariable Long id);

    @GetMapping("/orders/farmer/{farmerId}")
    List<OrderResponse> getOrdersByFarmer(@PathVariable Long farmerId);

    @GetMapping("/orders/dealer/{dealerId}")
    List<OrderResponse> getOrdersByDealer(@PathVariable Long dealerId);
}