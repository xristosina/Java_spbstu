package spbstu.TasksApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.service.impl.NotificationServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceImplTest {

    private NotificationServiceImpl notificationService;
    private Notification notification1;
    private Notification notification2;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl();
        
        notification1 = Notification.builder()
                .text("Test notification 1")
                .taskId(1L)
                .userId(1L)
                .isRead(false)
                .build();

        notification2 = Notification.builder()
                .text("Test notification 2")
                .taskId(2L)
                .userId(1L)
                .isRead(true)
                .build();
    }

    @Test
    void createNotification_ShouldCreateValidNotification() {
        // Act
        Notification created = notificationService.createNotification(notification1);

        // Assert
        assertNotNull(created);
        assertNotNull(created.getNotificationId());
        assertEquals(notification1.getText(), created.getText());
        assertEquals(notification1.getTaskId(), created.getTaskId());
        assertEquals(notification1.getUserId(), created.getUserId());
        assertFalse(created.getIsRead());
        assertNotNull(created.getDate());
    }

    @Test
    void createNotification_ShouldThrowException_WhenTextIsEmpty() {
        // Arrange
        notification1.setText("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> notificationService.createNotification(notification1));
    }

    @Test
    void createNotification_ShouldThrowException_WhenTaskIdIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            notification1.setTaskId(null);
            notificationService.createNotification(notification1);
        });
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotificationsForUser() {
        // Arrange
        notificationService.createNotification(notification1);
        notificationService.createNotification(notification2);

        // Act
        List<Notification> notifications = notificationService.getAllNotifications(1L);

        // Assert
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().allMatch(n -> n.getUserId().equals(1L)));
    }

    @Test
    void getPendingNotifications_ShouldReturnOnlyUnreadNotifications() {
        // Arrange
        notificationService.createNotification(notification1);
        notificationService.createNotification(notification2);

        // Act
        List<Notification> notifications = notificationService.getPendingNotifications(1L);

        // Assert
        assertEquals(1, notifications.size());
        assertTrue(notifications.stream().allMatch(n -> !n.getIsRead()));
    }

    @Test
    void getNotificationById_ShouldReturnNotification_WhenExists() {
        // Arrange
        Notification created = notificationService.createNotification(notification1);

        // Act
        Notification found = notificationService.getNotificationById(created.getNotificationId());

        // Assert
        assertNotNull(found);
        assertEquals(created.getNotificationId(), found.getNotificationId());
    }

    @Test
    void getNotificationById_ShouldThrowException_WhenNotFound() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(999L));
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        // Arrange
        Notification created = notificationService.createNotification(notification1);

        // Act
        Notification marked = notificationService.markAsRead(created.getNotificationId());

        // Assert
        assertTrue(marked.getIsRead());
    }

    @Test
    void markAllAsRead_ShouldMarkAllUserNotificationsAsRead() {
        // Arrange
        notificationService.createNotification(notification1);
        notificationService.createNotification(notification2);

        // Act
        notificationService.markAllAsRead(1L);

        // Assert
        List<Notification> notifications = notificationService.getAllNotifications(1L);
        assertTrue(notifications.stream().allMatch(Notification::getIsRead));
    }

    @Test
    void deleteNotification_ShouldRemoveNotification() {
        // Arrange
        Notification created = notificationService.createNotification(notification1);

        // Act
        notificationService.deleteNotification(created.getNotificationId());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(created.getNotificationId()));
    }
} 