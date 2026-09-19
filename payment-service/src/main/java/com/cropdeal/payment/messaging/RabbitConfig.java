package com.cropdeal.payment.messaging;

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
    public static final String NOTIFICATION_PAYMENT_QUEUE = "notification.payment.queue";

    public static final String PAYMENT_SUCCESS = "payment.success";
    public static final String PAYMENT_FAILED = "payment.failed";

    @Bean
    public DirectExchange cropDealExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue notificationPaymentQueue() {
        return new Queue(NOTIFICATION_PAYMENT_QUEUE, true);
    }

    @Bean
    public Binding paymentSuccessBinding() {
        return BindingBuilder.bind(notificationPaymentQueue())
                .to(cropDealExchange())
                .with(PAYMENT_SUCCESS);
    }

    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder.bind(notificationPaymentQueue())
                .to(cropDealExchange())
                .with(PAYMENT_FAILED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
