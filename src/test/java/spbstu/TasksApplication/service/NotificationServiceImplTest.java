package spbstu.TasksApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.NotificationRepository;
import spbstu.TasksApplication.repository.UserRepository;
import spbstu.TasksApplication.service.impl.NotificationServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification testNotification;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .build();

        testNotification = Notification.builder()
                .notificationId(1L)
                .message("Test notification")
                .date(LocalDateTime.now())
                .isRead(false)
                .userId(testUser.getUserId())
                .build();
    }

    @Test
    void createNotification_ShouldCreateValidNotification() {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification createdNotification = notificationService.createNotification(testNotification);

        assertNotNull(createdNotification);
        assertEquals(testNotification.getMessage(), createdNotification.getMessage());
        assertEquals(testNotification.getUserId(), createdNotification.getUserId());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotification_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.createNotification(testNotification));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void createNotification_ShouldThrowException_WhenMessageIsEmpty() {
        testNotification.setMessage("");

        assertThrows(IllegalArgumentException.class, () -> notificationService.createNotification(testNotification));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotificationsForUser() {
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUserIdOrderByDateDesc(testUser.getUserId())).thenReturn(notifications);

        List<Notification> foundNotifications = notificationService.getAllNotifications(testUser.getUserId());

        assertNotNull(foundNotifications);
        assertEquals(1, foundNotifications.size());
        verify(notificationRepository).findByUserIdOrderByDateDesc(testUser.getUserId());
    }

    @Test
    void getUnreadNotifications_ShouldReturnUnreadNotificationsForUser() {
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(testUser.getUserId()))
                .thenReturn(notifications);

        List<Notification> foundNotifications = notificationService.getUnreadNotifications(testUser.getUserId());

        assertNotNull(foundNotifications);
        assertEquals(1, foundNotifications.size());
        verify(notificationRepository).findByUserIdAndIsReadFalseOrderByDateDesc(testUser.getUserId());
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        when(notificationRepository.findById(testNotification.getNotificationId()))
                .thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification readNotification = notificationService.markAsRead(testNotification.getNotificationId());

        assertTrue(readNotification.getIsRead());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void markAsRead_ShouldThrowException_WhenNotificationNotFound() {
        when(notificationRepository.findById(testNotification.getNotificationId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(testNotification.getNotificationId()));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void deleteNotification_ShouldDeleteNotification() {
        when(notificationRepository.findById(testNotification.getNotificationId()))
                .thenReturn(Optional.of(testNotification));

        notificationService.deleteNotification(testNotification.getNotificationId());

        verify(notificationRepository).delete(testNotification);
    }

    @Test
    void deleteNotification_ShouldThrowException_WhenNotificationNotFound() {
        when(notificationRepository.findById(testNotification.getNotificationId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.deleteNotification(testNotification.getNotificationId()));
        verify(notificationRepository, never()).deleteById(anyLong());
    }
} 