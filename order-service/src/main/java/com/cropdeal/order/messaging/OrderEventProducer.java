package com.cropdeal.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public OrderEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(OrderEvent event) {
        send(RabbitConfig.ORDER_CREATED, event);
    }

    public void publishOrderAccepted(OrderEvent event) {
        send(RabbitConfig.ORDER_ACCEPTED, event);
    }

    public void publishOrderRejected(OrderEvent event) {
        send(RabbitConfig.ORDER_REJECTED, event);
    }

    public void publishOrderCompleted(OrderEvent event) {
        send(RabbitConfig.ORDER_COMPLETED, event);
    }

    private void send(String routingKey, OrderEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, event);
        } catch (RuntimeException ex) {
            logger.warn("Order operation completed, but notification event could not be sent: {}", ex.getMessage());
        }
    }
}
