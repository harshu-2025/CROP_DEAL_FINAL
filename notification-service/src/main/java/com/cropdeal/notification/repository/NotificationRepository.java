package com.cropdeal.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cropdeal.notification.entity.Notification;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndReadFalse(Long userId);
}