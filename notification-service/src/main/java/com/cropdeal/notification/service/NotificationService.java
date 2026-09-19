package com.cropdeal.notification.service;



import java.util.List;

import com.cropdeal.notification.dto.NotificationResponse;

public interface NotificationService {

    List<NotificationResponse> getNotificationsByUser(Long userId);

    List<NotificationResponse> getUnreadNotifications(Long userId);

    NotificationResponse markAsRead(Long id);

    void deleteNotification(Long id);
}