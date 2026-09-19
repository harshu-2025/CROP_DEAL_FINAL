package com.cropdeal.notification.service;

import com.cropdeal.notification.dto.NotificationResponse;
import com.cropdeal.notification.dto.UserResponse;
import com.cropdeal.notification.client.UserClient;
import com.cropdeal.notification.security.SecurityUtils;
import com.cropdeal.notification.entity.Notification;
import com.cropdeal.notification.exception.NotificationNotFoundException;
import com.cropdeal.notification.repository.NotificationRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserClient userClient;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserClient userClient) {
        this.notificationRepository = notificationRepository;
        this.userClient = userClient;
    }

    @Override
    public List<NotificationResponse> getNotificationsByUser(Long userId) {

        ensureUserOwnerOrAdmin(userId);
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(Long userId) {

        ensureUserOwnerOrAdmin(userId);
        return notificationRepository.findByUserIdAndReadFalse(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public NotificationResponse markAsRead(Long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification not found with id: " + id
                ));

        ensureUserOwnerOrAdmin(notification.getUserId());
        notification.setRead(true);

        Notification updatedNotification = notificationRepository.save(notification);

        return mapToResponse(updatedNotification);
    }

    @Override
    public void deleteNotification(Long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification not found with id: " + id
                ));

        ensureUserOwnerOrAdmin(notification.getUserId());
        notificationRepository.delete(notification);
    }

    private void ensureUserOwnerOrAdmin(Long userId) {
        if (SecurityUtils.hasRole("ADMIN")) {
            return;
        }
        Long currentAuthUserId = SecurityUtils.getCurrentAuthUserId();
        if (currentAuthUserId != null && currentAuthUserId.equals(userId)) {
            return;
        }
        try {
            UserResponse user = userClient.getUserById(userId);
            if (user != null && currentAuthUserId != null && currentAuthUserId.equals(user.getAuthUserId())) {
                return;
            }
        } catch (Exception ignored) {
        }
        throw new AccessDeniedException("You can access only your own notifications");
    }

    private NotificationResponse mapToResponse(Notification notification) {

        NotificationResponse response = new NotificationResponse();

        response.setId(notification.getId());
        response.setUserId(notification.getUserId());
        response.setType(notification.getType());
        response.setMessage(notification.getMessage());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());

        return response;
    }
}