package spbstu.TasksApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.repository.NotificationRepository;
import spbstu.TasksApplication.service.impl.NotificationServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification1;
    private Notification notification2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        notification1 = Notification.builder()
                .message("Test notification 1")
                .userId(1L)
                .isRead(false)
                .build();

        notification2 = Notification.builder()
                .message("Test notification 2")
                .userId(1L)
                .isRead(true)
                .build();
    }

    @Test
    void createNotification_ShouldCreateValidNotification() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification1);

        // Act
        Notification created = notificationService.createNotification(notification1);

        // Assert
        assertNotNull(created);
        assertEquals(notification1.getMessage(), created.getMessage());
        assertEquals(notification1.getUserId(), created.getUserId());
        assertFalse(created.getIsRead());
        assertNotNull(created.getDate());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void createNotification_ShouldThrowException_WhenMessageIsEmpty() {
        // Arrange
        notification1.setMessage("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> notificationService.createNotification(notification1));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void createNotification_ShouldThrowException_WhenUserIdIsNull() {
        // Arrange
        notification1.setUserId(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> notificationService.createNotification(notification1));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotificationsForUser() {
        // Arrange
        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(notifications);

        // Act
        List<Notification> result = notificationService.getAllNotifications(1L);

        // Assert
        assertEquals(2, result.size());
        verify(notificationRepository, times(1)).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    void getPendingNotifications_ShouldReturnOnlyUnreadNotifications() {
        // Arrange
        List<Notification> notifications = List.of(notification1);
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(1L)).thenReturn(notifications);

        // Act
        List<Notification> result = notificationService.getPendingNotifications(1L);

        // Assert
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsRead());
        verify(notificationRepository, times(1)).findByUserIdAndIsReadFalseOrderByDateDesc(1L);
    }

    @Test
    void getNotificationById_ShouldReturnNotification_WhenExists() {
        // Arrange
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification1));

        // Act
        Notification found = notificationService.getNotificationById(1L);

        // Assert
        assertNotNull(found);
        assertEquals(notification1.getMessage(), found.getMessage());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void getNotificationById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(999L));
        verify(notificationRepository, times(1)).findById(999L);
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        // Arrange
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification1));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification1);

        // Act
        Notification marked = notificationService.markAsRead(1L);

        // Assert
        assertTrue(marked.getIsRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void markAllAsRead_ShouldMarkAllNotificationsAsRead() {
        // Arrange
        List<Notification> notifications = List.of(notification1);
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(1L)).thenReturn(notifications);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification1);

        // Act
        notificationService.markAllAsRead(1L);

        // Assert
        assertTrue(notification1.getIsRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void deleteNotification_ShouldDeleteNotification() {
        // Arrange
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification1));

        // Act
        notificationService.deleteNotification(1L);

        // Assert
        verify(notificationRepository, times(1)).delete(notification1);
    }
} 