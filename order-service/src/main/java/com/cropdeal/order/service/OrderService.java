package com.cropdeal.order.service;

import java.util.List;

import com.cropdeal.order.dto.DeliveryConfirmationRequest;
import com.cropdeal.order.dto.OrderRequest;
import com.cropdeal.order.dto.OrderResponse;
import com.cropdeal.order.entity.OrderStatus;

public interface OrderService {
	OrderResponse createOrder(OrderRequest request);

    OrderResponse createAuctionOrder(OrderRequest request);

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrdersByDealer(Long dealerId);

    List<OrderResponse> getOrdersByFarmer(Long farmerId);

    List<OrderResponse> getOrdersByStatus(OrderStatus status);

    OrderResponse acceptOrder(Long id);

    OrderResponse rejectOrder(Long id);

    OrderResponse cancelOrder(Long id);
    
    OrderResponse markPaymentSuccess(Long id);

    OrderResponse markPaymentFailed(Long id);

    void deleteOrder(Long id);
    
    OrderResponse completeOrder(Long id);
    
    OrderResponse confirmDelivery(Long orderId, DeliveryConfirmationRequest request);

    byte[] getDeliveryQrCode(Long orderId);
}
