package com.example.ketari.controller;

import com.example.ketari.dto.ApiResponse;
import com.example.ketari.dto.PageResponse;
import com.example.ketari.dto.notification.NotificationResponse;
import com.example.ketari.model.User;
import com.example.ketari.service.AuthService;
import com.example.ketari.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling user notifications, counts, and read statuses.
 */
@Tag(name = "Notifications", description = "Endpoints for user alerts, real-time push, and notification status")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    @Operation(summary = "Get current user notifications ordered by newest first")
    @GetMapping
    public PageResponse<NotificationResponse> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        return this.notificationService.getUserNotifications(currentUser.getId(), page, size);
    }

    @Operation(summary = "Get count of unread notifications for current user")
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        long count = this.notificationService.getUnreadCount(currentUser.getId());
        return ApiResponse.success("Unread count retrieved", count);
    }

    @Operation(summary = "Mark a single notification as read")
    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        this.notificationService.markAsRead(id, currentUser.getId());
        return ApiResponse.success("Notification marked as read", null);
    }

    @Operation(summary = "Mark all notifications as read for current user")
    @PatchMapping("/mark-all-read")
    public ApiResponse<Void> markAllAsRead() {
        User currentUser = this.authService.getCurrentAuthenticatedUser();
        this.notificationService.markAllAsRead(currentUser.getId());
        return ApiResponse.success("All notifications marked as read", null);
    }
}
