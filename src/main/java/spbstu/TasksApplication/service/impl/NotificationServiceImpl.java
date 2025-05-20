package spbstu.TasksApplication.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.NotificationRepository;
import spbstu.TasksApplication.repository.UserRepository;
import spbstu.TasksApplication.service.NotificationService;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable(value = "notifications", key = "#userId.toString()", unless = "#result.isEmpty()")
    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Override
    @Cacheable(value = "notifications", key = "'unread_' + #userId.toString()", unless = "#result.isEmpty()")
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(userId);
    }

    @Override
    @Cacheable(value = "notifications", key = "#notificationId.toString()", unless = "#result == null")
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
    }

    @Override
    public Notification createNotificationFromMessage(String message, Long userId) {
        Notification notification = Notification.builder()
                .message(message)
                .userId(userId)
                .isRead(false)
                .build();
        return createNotification(notification);
    }

    // Package-private method for message listener to use
    @CacheEvict(value = "notifications", allEntries = true)
    Notification createNotification(Notification notification) {
        validateNotification(notification);
        User user = userRepository.findById(notification.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + notification.getUserId()));
        notification.setDate(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    @Override
    @CacheEvict(value = "notifications", allEntries = true)
    public Notification markAsRead(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    @CacheEvict(value = "notifications", allEntries = true)
    public void deleteNotification(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notificationRepository.delete(notification);
    }

    private void validateNotification(Notification notification) {
        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message cannot be empty");
        }
        if (notification.getUserId() == null) {
            throw new IllegalArgumentException("Notification userId cannot be null");
        }
    }
} 