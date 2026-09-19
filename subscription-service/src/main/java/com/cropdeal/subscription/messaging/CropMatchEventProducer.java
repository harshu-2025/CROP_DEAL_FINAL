package com.cropdeal.subscription.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class CropMatchEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public CropMatchEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCropMatch(CropMatchFoundEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.CROP_MATCH_FOUND_ROUTING_KEY,
                event);
    }
}