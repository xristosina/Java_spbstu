package spbstu.TasksApplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.repository.NotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByDateDesc(userId);
    }

    public List<Notification> getPendingNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(userId);
    }

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
}