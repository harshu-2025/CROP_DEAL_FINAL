package com.cropdeal.notification.messaging;

import com.cropdeal.notification.entity.Notification;
import com.cropdeal.notification.entity.NotificationType;
import com.cropdeal.notification.repository.NotificationRepository;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderEventConsumer {

    private final NotificationRepository notificationRepository;

    public OrderEventConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE)
    public void receiveOrderEvent(OrderEvent event) {

        if ("CREATED".equalsIgnoreCase(event.getStatus())) {
            saveNotification(event.getFarmerId(), NotificationType.ORDER_CREATED, event.getMessage());
            saveNotification(event.getDealerId(), NotificationType.ORDER_CREATED, event.getMessage());
        }

        else if ("ACCEPTED".equalsIgnoreCase(event.getStatus())) {
            saveNotification(event.getDealerId(), NotificationType.ORDER_ACCEPTED, event.getMessage());
            saveNotification(event.getFarmerId(), NotificationType.ORDER_ACCEPTED, event.getMessage());
        }

        else if ("REJECTED".equalsIgnoreCase(event.getStatus())) {
            saveNotification(event.getDealerId(), NotificationType.ORDER_REJECTED, event.getMessage());
            saveNotification(event.getFarmerId(), NotificationType.ORDER_REJECTED, event.getMessage());
        }

        else if ("COMPLETED".equalsIgnoreCase(event.getStatus())) {
            saveNotification(event.getDealerId(), NotificationType.ORDER_COMPLETED, event.getMessage());
            saveNotification(event.getFarmerId(), NotificationType.ORDER_COMPLETED, event.getMessage());
        }
    }

    private void saveNotification(Long userId, NotificationType type, String message) {
        if (userId == null) {
            return;
        }
        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}
