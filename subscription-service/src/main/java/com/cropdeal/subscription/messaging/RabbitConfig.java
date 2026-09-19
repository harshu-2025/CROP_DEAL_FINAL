package com.cropdeal.subscription.messaging;

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

    public static final String CROP_QUEUE = "subscription.crop.queue";
    public static final String NOTIFICATION_CROP_QUEUE = "notification.crop.queue";

    public static final String CROP_PUBLISHED_ROUTING_KEY = "crop.published";

    public static final String CROP_MATCH_FOUND_ROUTING_KEY = "crop.match.found";

    @Bean
    public DirectExchange cropDealExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue cropQueue() {
        return new Queue(CROP_QUEUE, true);
    }

    @Bean
    public Binding cropBinding() {
        return BindingBuilder
                .bind(cropQueue())
                .to(cropDealExchange())
                .with(CROP_PUBLISHED_ROUTING_KEY);
    }

    @Bean
    public Queue notificationCropQueue() {
        return new Queue(NOTIFICATION_CROP_QUEUE, true);
    }

    @Bean
    public Binding cropMatchBinding() {
        return BindingBuilder.bind(notificationCropQueue())
                .to(cropDealExchange())
                .with(CROP_MATCH_FOUND_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
