package spbstu.TasksApplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Cacheable(value = "notifications", key = "#userId.toString()", unless = "#result.isEmpty()")
    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Cacheable(value = "notifications", key = "'unread_' + #userId.toString()", unless = "#result.isEmpty()")
    public List<Notification> getPendingNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(userId);
    }

    @Cacheable(value = "notifications", key = "#notificationId.toString()", unless = "#result == null")
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
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

    @CacheEvict(value = "notifications", allEntries = true)
    public Notification createNotification(Notification testNotification) {
        return notificationRepository.save(testNotification);
    }

    @CacheEvict(value = "notifications", allEntries = true)
    public Notification markAsRead(Long notificationId) {
        Notification notification = ((NotificationService)AopContext.currentProxy()).getNotificationById(notificationId);
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    @CacheEvict(value = "notifications", allEntries = true)
    public void markAllAsRead(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(userId);
        list.forEach(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

    @CacheEvict(value = "notifications", allEntries = true)
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public Notification createNotificationFromMessage(String message, Long userId) {
        Notification notification = Notification.builder()
                .text(message)
                .userId(userId)
                .isRead(false)
                .build();
        return createNotification(notification);
    }
}