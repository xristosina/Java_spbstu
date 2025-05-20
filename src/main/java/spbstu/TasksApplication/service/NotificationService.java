package spbstu.TasksApplication.service;

import spbstu.TasksApplication.model.Notification;
import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications(Long userId);
    List<Notification> getUnreadNotifications(Long userId);
    Notification getNotificationById(Long notificationId);
    Notification markAsRead(Long notificationId);
    void deleteNotification(Long notificationId);
    Notification createNotificationFromMessage(String message, Long userId);
}