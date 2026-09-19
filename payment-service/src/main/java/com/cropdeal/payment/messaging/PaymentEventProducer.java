package com.cropdeal.payment.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public PaymentEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPaymentSuccess(PaymentEvent event) {
        send(RabbitConfig.PAYMENT_SUCCESS, event);
    }

    public void publishPaymentFailed(PaymentEvent event) {
        send(RabbitConfig.PAYMENT_FAILED, event);
    }

    private void send(String routingKey, PaymentEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, event);
        } catch (RuntimeException ex) {
            logger.warn("Payment operation completed, but notification event could not be sent: {}", ex.getMessage());
        }
    }
}
