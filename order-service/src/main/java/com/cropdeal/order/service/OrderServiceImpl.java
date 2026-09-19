package com.cropdeal.order.service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;

import com.cropdeal.order.client.CropServiceClient;
import com.cropdeal.order.client.UserServiceClient;
import com.cropdeal.order.client.WalletClient;
import com.cropdeal.order.dto.CropResponse;
import com.cropdeal.order.dto.DeliveryConfirmationRequest;
import com.cropdeal.order.dto.EscrowReleaseRequest;
import com.cropdeal.order.dto.OrderRequest;
import com.cropdeal.order.dto.OrderResponse;
import com.cropdeal.order.dto.UserResponse;
import com.cropdeal.order.entity.Order;
import com.cropdeal.order.entity.OrderStatus;
import com.cropdeal.order.entity.PaymentStatus;
import com.cropdeal.order.entity.PickupStatus;
import com.cropdeal.order.exception.OrderNotFoundException;
import com.cropdeal.order.messaging.OrderEvent;
import com.cropdeal.order.messaging.OrderEventProducer;
import com.cropdeal.order.repository.OrderRepository;
import com.cropdeal.order.security.SecurityUtils;
@Service
public class OrderServiceImpl implements OrderService{
	
	private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final CropServiceClient cropServiceClient;
    private final OrderEventProducer orderEventProducer;
    private final WalletClient walletClient;
    private final QrCodeService qrCodeService;

    @Value("${internal.api.key}")
    private String internalApiKey;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserServiceClient userServiceClient,
            CropServiceClient cropServiceClient,
            OrderEventProducer orderEventProducer,
            WalletClient walletClient,
            QrCodeService qrCodeService) {

        this.orderRepository = orderRepository;
        this.userServiceClient = userServiceClient;
        this.cropServiceClient = cropServiceClient;
        this.orderEventProducer = orderEventProducer;
        this.walletClient = walletClient;
        this.qrCodeService = qrCodeService;
    }

	@Override
	public OrderResponse createOrder(OrderRequest request) {
		
		UserResponse dealer=userServiceClient.getUser(request.getDealerId());

        if (!SecurityUtils.getCurrentAuthUserId().equals(dealer.getAuthUserId())) {
            throw new AccessDeniedException("You can place orders only for your own dealer profile");
        }
		
		if(!"DEALER".equalsIgnoreCase(dealer.getRole())) {
			throw new IllegalArgumentException( "Only dealers can place orders");
			
		}
		if(!dealer.isActive()) {
			throw new IllegalArgumentException( "Dealer account is not active");
			
		}
		CropResponse crop = cropServiceClient.getCrop(request.getCropId());
		
		if(!"AVAILABLE".equalsIgnoreCase(crop.getStatus())) {
			throw new IllegalArgumentException("Crop is not active");
		}
		if(crop.getQuantity()<request.getQuantity()) {
			throw new IllegalArgumentException("Asked quantity is higher than available quantrity");
		}
		UserResponse farmer =userServiceClient.getUser(crop.getFarmerId());
		
        if (!"FARMER".equalsIgnoreCase(farmer.getRole())) {
            throw new IllegalArgumentException("Crop does not belong to a valid farmer");
        }

        if (!farmer.isActive()) {
            throw new IllegalArgumentException("Farmer account is not active");
        }
        Double totalAmount = request.getAgreedPrice()*request.getQuantity();
        
        Order order = new Order();

        order.setDealerId(request.getDealerId());

        order.setFarmerId(crop.getFarmerId());

        order.setCropId(request.getCropId());
        order.setQuantity(request.getQuantity());
        order.setAgreedPrice(request.getAgreedPrice());
        order.setTotalAmount(totalAmount);

        order.setStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPickupStatus(PickupStatus.PENDING);

        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        String orderNumber = "ORD-" + Year.now().getValue() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderNumber(orderNumber);

        Order savedOrder = orderRepository.save(order);

        OrderEvent event = new OrderEvent();
        event.setOrderId(savedOrder.getId());
        event.setOrderNumber(savedOrder.getOrderNumber());
        event.setFarmerId(savedOrder.getFarmerId());
        event.setDealerId(savedOrder.getDealerId());
        event.setCropId(savedOrder.getCropId());
        event.setStatus(savedOrder.getStatus().name());
        event.setMessage("New order created: " + savedOrder.getOrderNumber());

        orderEventProducer.publishOrderCreated(event);

        return mapToResponse(savedOrder);
	}

    @Override
    public OrderResponse createAuctionOrder(OrderRequest request) {

        CropResponse crop = cropServiceClient.getCrop(request.getCropId());
        if (!"AVAILABLE".equalsIgnoreCase(crop.getStatus())) {
            throw new IllegalArgumentException("Crop is not available");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0 || request.getQuantity() > crop.getQuantity()) {
            throw new IllegalArgumentException("Invalid crop quantity");
        }
        if (request.getAgreedPrice() == null || request.getAgreedPrice() <= 0) {
            throw new IllegalArgumentException("Invalid agreed price");
        }

        cropServiceClient.reduceQuantity(request.getCropId(), request.getQuantity());

        Order order = new Order();
        order.setOrderNumber("ORD-" + Year.now().getValue() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setFarmerId(crop.getFarmerId());
        order.setDealerId(request.getDealerId());
        order.setCropId(request.getCropId());
        order.setQuantity(request.getQuantity());
        order.setAgreedPrice(request.getAgreedPrice());
        order.setTotalAmount(request.getQuantity() * request.getAgreedPrice());
        order.setStatus(OrderStatus.ACCEPTED);
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        order.setPickupStatus(PickupStatus.PENDING);
        order.setDeliveryQrCode(UUID.randomUUID().toString());
        order.setDeliveryConfirmed(false);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        OrderEvent event = new OrderEvent();
        event.setOrderId(savedOrder.getId());
        event.setOrderNumber(savedOrder.getOrderNumber());
        event.setFarmerId(savedOrder.getFarmerId());
        event.setDealerId(savedOrder.getDealerId());
        event.setCropId(savedOrder.getCropId());
        event.setStatus(savedOrder.getStatus().name());
        event.setMessage("Auction order created: " + savedOrder.getOrderNumber());
        orderEventProducer.publishOrderCreated(event);

        return mapToResponse(savedOrder);
    }

	@Override
	public OrderResponse completeOrder(Long id) {

	    Order order = findOrder(id);
        ensureOrderPartyOrAdmin(order);

        if (order.getStatus() != OrderStatus.PAID || order.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalArgumentException("Only paid orders can be completed");
        }

	    order.setStatus(OrderStatus.COMPLETED);
	    order.setPickupStatus(PickupStatus.COMPLETED);
	    order.setCompletedAt(LocalDateTime.now());
	    order.setUpdatedAt(LocalDateTime.now());

	    Order updatedOrder = orderRepository.save(order);

	    OrderEvent event = new OrderEvent();

	    event.setOrderId(updatedOrder.getId());
	    event.setOrderNumber(updatedOrder.getOrderNumber());
	    event.setFarmerId(updatedOrder.getFarmerId());
	    event.setDealerId(updatedOrder.getDealerId());
	    event.setCropId(updatedOrder.getCropId());
	    event.setStatus(updatedOrder.getStatus().name());
	    event.setMessage("Order completed: " + updatedOrder.getOrderNumber());

	    orderEventProducer.publishOrderCompleted(event);

	    return mapToResponse(updatedOrder);
	}

	 @Override
	    public OrderResponse getOrderById(Long id) {

	        Order order = findOrder(id);
        ensureOrderPartyOrAdmin(order);

	        return mapToResponse(order);
	    }

	    @Override
	    public List<OrderResponse> getAllOrders() {

            requireAdmin();
	        return orderRepository.findAll()
	                .stream()
	                .map(this::mapToResponse)
	                .toList();
	    }

	    @Override
	    public List<OrderResponse> getOrdersByDealer(
	            Long dealerId) {

            UserResponse dealer = userServiceClient.getUser(dealerId);
            if (!SecurityUtils.hasRole("ADMIN") &&
                    !SecurityUtils.getCurrentAuthUserId().equals(dealer.getAuthUserId())) {
                throw new AccessDeniedException("You can view only your own dealer orders");
            }

	        return orderRepository
	                .findByDealerId(dealerId)
	                .stream()
	                .map(this::mapToResponse)
	                .toList();
	    }

	    @Override
	    public List<OrderResponse> getOrdersByFarmer(
	            Long farmerId) {

            UserResponse farmer = userServiceClient.getUser(farmerId);
            if (!SecurityUtils.hasRole("ADMIN") &&
                    !SecurityUtils.getCurrentAuthUserId().equals(farmer.getAuthUserId())) {
                throw new AccessDeniedException("You can view only your own farmer orders");
            }

	        return orderRepository
	                .findByFarmerId(farmerId)
	                .stream()
	                .map(this::mapToResponse)
	                .toList();
	    }

	    @Override
	    public List<OrderResponse> getOrdersByStatus(
	            OrderStatus status) {

            requireAdmin();
	        return orderRepository
	                .findByStatus(status)
	                .stream()
	                .map(this::mapToResponse)
	                .toList();
	    }

	    @Override
	    public OrderResponse acceptOrder(Long id) {

	        Order order = findOrder(id);
        ensureFarmerOwner(order);

            if (order.getStatus() != OrderStatus.CREATED) {
                throw new IllegalArgumentException("Only newly created orders can be accepted");
            }

	        order.setStatus(OrderStatus.ACCEPTED);
	        order.setUpdatedAt(LocalDateTime.now());

	        Order updatedOrder = orderRepository.save(order);

	        OrderEvent event = new OrderEvent();
	        event.setOrderId(updatedOrder.getId());
	        event.setOrderNumber(updatedOrder.getOrderNumber());
	        event.setFarmerId(updatedOrder.getFarmerId());
	        event.setDealerId(updatedOrder.getDealerId());
	        event.setCropId(updatedOrder.getCropId());
	        event.setStatus(updatedOrder.getStatus().name());
	        event.setMessage("Order accepted: " + updatedOrder.getOrderNumber());

	        orderEventProducer.publishOrderAccepted(event);

	        return mapToResponse(updatedOrder);
	    }

	    @Override
	    public OrderResponse rejectOrder(Long id) {

	        Order order = findOrder(id);
        ensureFarmerOwner(order);

            if (order.getStatus() != OrderStatus.CREATED) {
                throw new IllegalArgumentException("Only newly created orders can be rejected");
            }

	        order.setStatus(OrderStatus.REJECTED);
	        order.setUpdatedAt(LocalDateTime.now());

	        Order updatedOrder = orderRepository.save(order);

	        OrderEvent event = new OrderEvent();
	        event.setOrderId(updatedOrder.getId());
	        event.setOrderNumber(updatedOrder.getOrderNumber());
	        event.setFarmerId(updatedOrder.getFarmerId());
	        event.setDealerId(updatedOrder.getDealerId());
	        event.setCropId(updatedOrder.getCropId());
	        event.setStatus(updatedOrder.getStatus().name());
	        event.setMessage("Order rejected: " + updatedOrder.getOrderNumber());

	        orderEventProducer.publishOrderRejected(event);

	        return mapToResponse(updatedOrder);
	    }

	    @Override
	    public OrderResponse cancelOrder(Long id) {

	        Order order = findOrder(id);
        ensureDealerOwner(order);

            if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.ACCEPTED) {
                throw new IllegalArgumentException("Only created or accepted orders can be cancelled");
            }

	        order.setStatus(OrderStatus.CANCELLED);
	        order.setUpdatedAt(LocalDateTime.now());

	        Order updatedOrder = orderRepository.save(order);

	        return mapToResponse(updatedOrder);
	    }

	    @Override
	    public void deleteOrder(Long id) {

            requireAdmin();
	        Order order = findOrder(id);

	        orderRepository.delete(order);
	    }
	    @Override
	    public OrderResponse markPaymentSuccess(Long id) {

	        Order order = findOrder(id);

            if (order.getStatus() == OrderStatus.PAID && order.getPaymentStatus() == PaymentStatus.SUCCESS) {
                return mapToResponse(order);
            }

            if (order.getStatus() != OrderStatus.ACCEPTED && order.getStatus() != OrderStatus.PAYMENT_FAILED) {
                throw new IllegalArgumentException("Payment can be completed only for an accepted order");
            }

            cropServiceClient.reduceQuantity(order.getCropId(), order.getQuantity());

	        order.setPaymentStatus(PaymentStatus.SUCCESS);
	        order.setStatus(OrderStatus.PAID);
	        order.setUpdatedAt(LocalDateTime.now());

	        Order updatedOrder = orderRepository.save(order);

	        return mapToResponse(updatedOrder);
	    }

	    @Override
	    public OrderResponse markPaymentFailed(Long id) {

	        Order order = findOrder(id);

            if (order.getStatus() != OrderStatus.ACCEPTED && order.getStatus() != OrderStatus.PAYMENT_FAILED) {
                throw new IllegalArgumentException("Payment can fail only for an accepted order");
            }

	        order.setPaymentStatus(PaymentStatus.FAILED);
	        order.setStatus(OrderStatus.PAYMENT_FAILED);
	        order.setUpdatedAt(LocalDateTime.now());

	        Order updatedOrder = orderRepository.save(order);

	        return mapToResponse(updatedOrder);
	    }
	    @Override
	    public OrderResponse confirmDelivery(Long orderId, DeliveryConfirmationRequest request) {

	        Order order = orderRepository.findById(orderId)
	                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            if (!SecurityUtils.hasRole("ADMIN")) {
                ensureDealerOwner(order);
            }

	        if (Boolean.TRUE.equals(order.getDeliveryConfirmed())) {
	            throw new IllegalArgumentException("Delivery already confirmed");
	        }

	        if (order.getDeliveryQrCode() == null ||
	                !order.getDeliveryQrCode().equals(request.getQrCode())) {
	            throw new IllegalArgumentException("Invalid delivery QR code");
	        }

	        if (order.getPaymentStatus() != PaymentStatus.SUCCESS) {
	            throw new IllegalArgumentException("Payment is not completed");
	        }

	        double totalAmount = order.getQuantity() * order.getAgreedPrice();

	        EscrowReleaseRequest escrowRequest = new EscrowReleaseRequest();
	        escrowRequest.setFarmerId(order.getFarmerId());
	        escrowRequest.setAmount(totalAmount);
	        escrowRequest.setReference("ORDER-" + order.getId());

	        walletClient.releaseEscrow(
                    order.getDealerId(),
                    internalApiKey,
                    escrowRequest
            );

	        order.setDeliveryConfirmed(true);
	        order.setPickupStatus(PickupStatus.COMPLETED);
	        order.setStatus(OrderStatus.COMPLETED);
	        order.setCompletedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());

	        Order savedOrder = orderRepository.save(order);

            OrderEvent event = new OrderEvent();
            event.setOrderId(savedOrder.getId());
            event.setOrderNumber(savedOrder.getOrderNumber());
            event.setFarmerId(savedOrder.getFarmerId());
            event.setDealerId(savedOrder.getDealerId());
            event.setCropId(savedOrder.getCropId());
            event.setStatus(savedOrder.getStatus().name());
            event.setMessage("Order completed after QR delivery confirmation: " + savedOrder.getOrderNumber());
            orderEventProducer.publishOrderCompleted(event);

	        return mapToResponse(savedOrder);
	    }

        
    @Override
    public byte[] getDeliveryQrCode(Long orderId) {
        Order order = findOrder(orderId);

        if (!SecurityUtils.hasRole("ADMIN")) {
            ensureFarmerOwner(order);
        }

        if (order.getDeliveryQrCode() == null) {
            throw new IllegalArgumentException("Delivery QR code is not available for this order");
        }

        return qrCodeService.generateQrCode(order.getDeliveryQrCode());
    }

        private void ensureFarmerOwner(Order order) {
            UserResponse farmer = userServiceClient.getUser(order.getFarmerId());
            if (!SecurityUtils.getCurrentAuthUserId().equals(farmer.getAuthUserId())) {
                throw new AccessDeniedException("Only the order farmer can perform this action");
            }
        }

        private void ensureDealerOwner(Order order) {
            UserResponse dealer = userServiceClient.getUser(order.getDealerId());
            if (!SecurityUtils.getCurrentAuthUserId().equals(dealer.getAuthUserId())) {
                throw new AccessDeniedException("Only the order dealer can perform this action");
            }
        }

        private void ensureOrderPartyOrAdmin(Order order) {
            if (SecurityUtils.hasRole("ADMIN")) {
                return;
            }
            Long currentUserId = SecurityUtils.getCurrentAuthUserId();
            UserResponse farmer = userServiceClient.getUser(order.getFarmerId());
            UserResponse dealer = userServiceClient.getUser(order.getDealerId());
            if (!currentUserId.equals(farmer.getAuthUserId()) && !currentUserId.equals(dealer.getAuthUserId())) {
                throw new AccessDeniedException("You can access only your own orders");
            }
        }

        private void requireAdmin() {
            if (!SecurityUtils.hasRole("ADMIN")) {
                throw new AccessDeniedException("Admin access required");
            }
        }

	    private Order findOrder(Long id) {

	        return orderRepository.findById(id).orElseThrow(() ->new OrderNotFoundException(
	                                "Order not found with id: " + id));
	    }

	    private OrderResponse mapToResponse(Order order) {

	        OrderResponse response =new OrderResponse();

	        response.setId(order.getId());
	        response.setOrderNumber(order.getOrderNumber());
	        response.setFarmerId(order.getFarmerId());
	        response.setDealerId(order.getDealerId());
	        response.setCropId(order.getCropId());
	        response.setQuantity(order.getQuantity());
	        response.setAgreedPrice(order.getAgreedPrice());
	        response.setTotalAmount(order.getTotalAmount());
	        response.setStatus(order.getStatus());
	        response.setPaymentStatus(order.getPaymentStatus());
	        response.setPickupStatus(order.getPickupStatus());
	        response.setCreatedAt(order.getCreatedAt());
	        response.setUpdatedAt(order.getUpdatedAt());
	        response.setCompletedAt(order.getCompletedAt());
        response.setDeliveryConfirmed(order.getDeliveryConfirmed());

	        return response;
	    }
}
