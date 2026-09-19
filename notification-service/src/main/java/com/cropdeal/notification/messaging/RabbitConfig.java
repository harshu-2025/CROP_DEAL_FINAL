package com.cropdeal.notification.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "cropdeal.exchange";

    public static final String CROP_MATCH_QUEUE = "notification.crop.queue";
    public static final String ORDER_QUEUE = "notification.order.queue";

    public static final String CROP_MATCH_ROUTING_KEY = "crop.match.found";

    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_ACCEPTED = "order.accepted";
    public static final String ORDER_REJECTED = "order.rejected";
    public static final String ORDER_COMPLETED = "order.completed";
    
    public static final String PAYMENT_QUEUE = "notification.payment.queue";

    public static final String PAYMENT_SUCCESS = "payment.success";
    public static final String PAYMENT_FAILED = "payment.failed";

    @Bean
    public DirectExchange cropDealExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue cropMatchQueue() {
        return new Queue(CROP_MATCH_QUEUE, true);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }

    @Bean
    public Binding cropMatchBinding() {
        return BindingBuilder
                .bind(cropMatchQueue())
                .to(cropDealExchange())
                .with(CROP_MATCH_ROUTING_KEY);
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder
                .bind(orderQueue())
                .to(cropDealExchange())
                .with(ORDER_CREATED);
    }

    @Bean
    public Binding orderAcceptedBinding() {
        return BindingBuilder
                .bind(orderQueue())
                .to(cropDealExchange())
                .with(ORDER_ACCEPTED);
    }

    @Bean
    public Binding orderRejectedBinding() {
        return BindingBuilder
                .bind(orderQueue())
                .to(cropDealExchange())
                .with(ORDER_REJECTED);
    }

    @Bean
    public Binding orderCompletedBinding() {
        return BindingBuilder
                .bind(orderQueue())
                .to(cropDealExchange())
                .with(ORDER_COMPLETED);
    }
    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }

    @Bean
    public Binding paymentSuccessBinding() {
        return BindingBuilder.bind(paymentQueue())
                .to(cropDealExchange())
                .with(PAYMENT_SUCCESS);
    }

    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder.bind(paymentQueue())
                .to(cropDealExchange())
                .with(PAYMENT_FAILED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}