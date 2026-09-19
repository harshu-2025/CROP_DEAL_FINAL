package com.cropdeal.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cropdeal.order.entity.Order;
import com.cropdeal.order.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order,Long> {
	
	 Optional<Order> findByOrderNumber(String orderNumber);

	    List<Order> findByDealerId(Long dealerId);

	    List<Order> findByFarmerId(Long farmerId);

	    List<Order> findByStatus(OrderStatus status);

	    List<Order> findByDealerIdAndStatus(Long dealerId,OrderStatus status);

}
