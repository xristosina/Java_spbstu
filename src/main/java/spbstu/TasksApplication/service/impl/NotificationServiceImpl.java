package spbstu.TasksApplication.service.impl;

import org.springframework.stereotype.Service;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.service.NotificationService;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final Map<Long, Notification> notifications = new HashMap<>();
    private final AtomicLong notificationIdCounter = new AtomicLong(1);

    @Override
    public List<Notification> getAllNotifications(Long userId) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .sorted(Comparator.comparing(Notification::getDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> getPendingNotifications(Long userId) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId) && !notification.getIsRead())
                .sorted(Comparator.comparing(Notification::getDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Notification getNotificationById(Long notificationId) {
        Notification notification = notifications.get(notificationId);
        if (notification == null) {
            throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
        }
        return notification;
    }

    @Override
    public Notification createNotification(Notification notification) {
        validateNotification(notification);
        notification.setNotificationId(notificationIdCounter.getAndIncrement());
        notification.setDate(LocalDateTime.now());
        notifications.put(notification.getNotificationId(), notification);
        return notification;
    }

    @Override
    public Notification markAsRead(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notification.setIsRead(true);
        notifications.put(notificationId, notification);
        return notification;
    }

    @Override
    public void markAllAsRead(Long userId) {
        notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId) && !notification.getIsRead())
                .forEach(notification -> {
                    notification.setIsRead(true);
                    notifications.put(notification.getNotificationId(), notification);
                });
    }

    @Override
    public void deleteNotification(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notifications.remove(notificationId);
    }

    private void validateNotification(Notification notification) {
        if (notification.getText() == null || notification.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Notification text cannot be empty");
        }
        if (notification.getTaskId() == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }
        if (notification.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
} 