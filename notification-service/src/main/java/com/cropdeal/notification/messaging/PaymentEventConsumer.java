package com.cropdeal.notification.messaging;

import com.cropdeal.notification.entity.Notification;
import com.cropdeal.notification.entity.NotificationType;
import com.cropdeal.notification.repository.NotificationRepository;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentEventConsumer {

    private final NotificationRepository notificationRepository;

    public PaymentEventConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @RabbitListener(queues = RabbitConfig.PAYMENT_QUEUE)
    public void receivePaymentEvent(PaymentEvent event) {

        if ("SUCCESS".equalsIgnoreCase(event.getStatus())) {
            saveNotification(event.getDealerId(), NotificationType.PAYMENT_SUCCESS, event.getMessage());
            saveNotification(event.getFarmerId(), NotificationType.PAYMENT_SUCCESS, event.getMessage());
        }

        else if ("FAILED".equalsIgnoreCase(event.getStatus())) {
            saveNotification(event.getDealerId(), NotificationType.PAYMENT_FAILED, event.getMessage());
            saveNotification(event.getFarmerId(), NotificationType.PAYMENT_FAILED, event.getMessage());
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
