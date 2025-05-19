package spbstu.TasksApplication.service.impl;

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
    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Override
    public List<Notification> getPendingNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(userId);
    }

    @Override
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
    }

    @Override
    public Notification createNotification(Notification notification) {
        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message cannot be empty");
        }
        if (notification.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(notification.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + notification.getUserId()));
        notification.setDate(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    @Override
    public Notification markAsRead(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(userId);
        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

    @Override
    public void deleteNotification(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notificationRepository.delete(notification);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(userId);
    }
} 