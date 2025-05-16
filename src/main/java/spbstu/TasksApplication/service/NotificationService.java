package spbstu.TasksApplication.service;

import spbstu.TasksApplication.model.Notification;
import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications(Long userId);
    List<Notification> getPendingNotifications(Long userId);
    Notification getNotificationById(Long notificationId);
    Notification createNotification(Notification notification);
    Notification markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    void deleteNotification(Long notificationId);
}