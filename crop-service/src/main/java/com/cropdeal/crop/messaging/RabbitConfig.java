package com.cropdeal.crop.messaging;

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
	
	public static final String EXCHANGE ="cropdeal.exchange";
	public static final String SUBSCRIPTION_CROP_QUEUE = "subscription.crop.queue";
	public static final String CROP_PUBLISHED_ROUTING_KEY = "crop.published";
	
	@Bean
    public DirectExchange cropDealExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue subscriptionCropQueue() {
        return new Queue(SUBSCRIPTION_CROP_QUEUE, true);
    }

    @Bean
    public Binding cropPublishedBinding() {
        return BindingBuilder.bind(subscriptionCropQueue())
                .to(cropDealExchange())
                .with(CROP_PUBLISHED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
