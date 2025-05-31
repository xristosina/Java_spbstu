package spbstu.TasksApplication.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.repository.impl.InMemoryNotificationRepository;
import spbstu.TasksApplication.service.NotificationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {
    private NotificationService notificationService;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(new InMemoryNotificationRepository());
        testNotification = Notification.builder()
                .text("Test notification")
                .taskId(1L)
                .userId(1L)
                .isRead(false)
                .build();
    }

    @Test
    void createNotification_ShouldCreateNewNotification() {
        Notification createdNotification = notificationService.createNotification(testNotification);

        assertNotNull(createdNotification.getNotificationId());
        assertEquals(testNotification.getText(), createdNotification.getText());
        assertEquals(testNotification.getTaskId(), createdNotification.getTaskId());
        assertEquals(testNotification.getUserId(), createdNotification.getUserId());
        assertFalse(createdNotification.getIsRead());
        assertNotNull(createdNotification.getDate());
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotificationsForUser() {
        Notification notification1 = notificationService.createNotification(testNotification);
        Notification notification2 = Notification.builder()
                .text("Another notification")
                .taskId(2L)
                .userId(1L)
                .isRead(true)
                .build();
        notificationService.createNotification(notification2);

        List<Notification> notifications = notificationService.getAllNotifications(1L);
        
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().allMatch(n -> n.getUserId().equals(1L)));
    }

    @Test
    void getPendingNotifications_ShouldReturnOnlyUnreadNotifications() {
        Notification unreadNotification = notificationService.createNotification(testNotification);
        Notification readNotification = Notification.builder()
                .text("Read notification")
                .taskId(2L)
                .userId(1L)
                .isRead(true)
                .build();
        notificationService.createNotification(readNotification);

        List<Notification> notifications = notificationService.getPendingNotifications(1L);

        assertEquals(1, notifications.size());
        assertEquals(unreadNotification.getNotificationId(), notifications.get(0).getNotificationId());
    }

    @Test
    void getNotificationById_ShouldReturnNotification_WhenExists() {
        Notification createdNotification = notificationService.createNotification(testNotification);
        Notification foundNotification = notificationService.getNotificationById(createdNotification.getNotificationId());

        assertNotNull(foundNotification);
        assertEquals(createdNotification.getNotificationId(), foundNotification.getNotificationId());
    }

    @Test
    void getNotificationById_ShouldThrowException_WhenNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(999L));
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        Notification createdNotification = notificationService.createNotification(testNotification);
        Notification markedNotification = notificationService.markAsRead(createdNotification.getNotificationId());

        assertTrue(markedNotification.getIsRead());
    }

    @Test
    void markAllAsRead_ShouldMarkAllUserNotificationsAsRead() {
        Notification notification1 = notificationService.createNotification(testNotification);
        Notification notification2 = Notification.builder()
                .text("Another notification")
                .taskId(2L)
                .userId(1L)
                .isRead(false)
                .build();
        notificationService.createNotification(notification2);

        notificationService.markAllAsRead(1L);

        List<Notification> notifications = notificationService.getAllNotifications(1L);
        assertTrue(notifications.stream().allMatch(Notification::getIsRead));
    }

    @Test
    void deleteNotification_ShouldDeleteNotification() {
        Notification createdNotification = notificationService.createNotification(testNotification);
        notificationService.deleteNotification(createdNotification.getNotificationId());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(createdNotification.getNotificationId()));
    }
} 