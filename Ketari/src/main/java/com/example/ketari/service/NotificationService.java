package com.example.ketari.service;

import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.notification.NotificationResponse;
import com.example.ketari.enums.NotificationType;
import com.example.ketari.model.User;

/**
 * Service contract for persistent and real-time notification dispatching.
 */
public interface NotificationService {

    void sendNotification(User recipient, NotificationType type, String title, String message, Long relatedEntityId, String relatedEntityType);

    void sendNotification(Long recipientUserId, NotificationType type, String title, String message, Long relatedEntityId, String relatedEntityType);

    PageResponse<NotificationResponse> getUserNotifications(Long userId, int page, int size);

    long getUnreadCount(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);
}
