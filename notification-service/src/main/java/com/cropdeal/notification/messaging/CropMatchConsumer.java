package com.cropdeal.notification.messaging;

import com.cropdeal.notification.entity.Notification;
import com.cropdeal.notification.entity.NotificationType;
import com.cropdeal.notification.repository.NotificationRepository;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CropMatchConsumer {

    private final NotificationRepository notificationRepository;

    public CropMatchConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @RabbitListener(queues = RabbitConfig.CROP_MATCH_QUEUE)
    public void receiveCropMatch(CropMatchFoundEvent event) {
        if (event == null || event.getDealerId() == null) {
            return;
        }

        Notification notification = new Notification();

        notification.setUserId(event.getDealerId());
        notification.setType(NotificationType.CROP_MATCH_FOUND);
        notification.setMessage(event.getMessage());
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}