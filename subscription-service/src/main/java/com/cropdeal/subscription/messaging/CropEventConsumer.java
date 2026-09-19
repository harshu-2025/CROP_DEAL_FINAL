package com.cropdeal.subscription.messaging;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.cropdeal.subscription.entity.CropSubscription;
import com.cropdeal.subscription.repository.CropSubscriptionRepository;

import java.util.List;

@Service
public class CropEventConsumer {

    private final CropSubscriptionRepository subscriptionRepository;
    private final CropMatchEventProducer cropMatchEventProducer;

    public CropEventConsumer(
            CropSubscriptionRepository subscriptionRepository,
            CropMatchEventProducer cropMatchEventProducer) {

        this.subscriptionRepository = subscriptionRepository;
        this.cropMatchEventProducer = cropMatchEventProducer;
    }

    @RabbitListener(queues = RabbitConfig.CROP_QUEUE)
    public void receiveCropPublished(CropPublishedEvent event) {

        List<CropSubscription> subscriptions = subscriptionRepository.findByActiveTrue();

        for (CropSubscription subscription : subscriptions) {

            boolean cropNameMatch =
                    subscription.getCropName().equalsIgnoreCase(event.getCropName());

            boolean categoryMatch =
                    subscription.getCategory().equalsIgnoreCase(event.getCategory());

            boolean locationMatch =
                    subscription.getLocation().equalsIgnoreCase(event.getLocation());

            boolean quantityMatch =
                    event.getQuantity() >= subscription.getMinimumQuantity();

            boolean priceMatch =
                    event.getExpectedPrice() <= subscription.getMaximumPrice();

            if (cropNameMatch &&categoryMatch &&locationMatch &&quantityMatch &&priceMatch) {

                CropMatchFoundEvent matchEvent = new CropMatchFoundEvent();

                matchEvent.setDealerId(subscription.getDealerId());
                matchEvent.setCropId(event.getCropId());
                matchEvent.setCropName(event.getCropName());
                matchEvent.setMessage(
                        "Matching crop found: " + event.getCropName());

                cropMatchEventProducer.publishCropMatch(matchEvent);
            }
        }
    }
}