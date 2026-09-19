package com.cropdeal.crop.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class CropEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(CropEventProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public CropEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCropCreated(CropPublishedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.CROP_PUBLISHED_ROUTING_KEY,
                    event
            );
        } catch (RuntimeException ex) {
            logger.warn("Crop was saved, but crop published notification could not be sent: {}", ex.getMessage());
        }
    }
}
