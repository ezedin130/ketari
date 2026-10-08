package com.example.ketari.service.impl;

import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.notification.NotificationResponse;
import com.example.ketari.enums.NotificationType;
import com.example.ketari.exception.ResourceNotFoundException;
import com.example.ketari.mapper.NotificationMapper;
import com.example.ketari.model.Notification;
import com.example.ketari.model.User;
import com.example.ketari.repository.NotificationRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of NotificationService managing persistence and WebSocket push delivery.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void sendNotification(
            User recipient,
            NotificationType type,
            String title,
            String message,
            Long relatedEntityId,
            String relatedEntityType
    ) {
        if (recipient == null) {
            log.warn("Cannot send notification to null recipient");
            return;
        }

        Notification notification = Notification.builder()
                .user(recipient)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .relatedEntityId(relatedEntityId)
                .relatedEntityType(relatedEntityType)
                .build();

        Notification saved = this.notificationRepository.save(notification);
        NotificationResponse response = NotificationMapper.toResponse(saved);

        // Dispatch via WebSocket to specific user and to general user topic
        try {
            this.messagingTemplate.convertAndSendToUser(
                    recipient.getEmail(),
                    "/queue/notifications",
                    response
            );
            this.messagingTemplate.convertAndSend("/topic/notifications/" + recipient.getId(), response);
        } catch (Exception e) {
            log.debug("WebSocket notification dispatch skipped or failed: {}", e.getMessage());
        }

        log.info("Sent notification ID: {} to user: {}", saved.getId(), recipient.getEmail());
    }

    @Override
    @Transactional
    public void sendNotification(
            Long recipientUserId,
            NotificationType type,
            String title,
            String message,
            Long relatedEntityId,
            String relatedEntityType
    ) {
        User recipient = this.userRepository.findById(recipientUserId).orElse(null);
        if (recipient != null) {
            sendNotification(recipient, type, title, message, relatedEntityId, relatedEntityType);
        } else {
            log.warn("Recipient user not found with ID: {}", recipientUserId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getUserNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Notification> notificationPage = this.notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(notificationPage, NotificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return this.notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = this.notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setIsRead(true);
        this.notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        this.notificationRepository.markAllAsReadByUserId(userId);
    }
}
