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
public class NotificationServiceImplTest {

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
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");

        testNotification = new Notification();
        testNotification.setNotificationId(1L);
        testNotification.setMessage("Test notification");
        testNotification.setUserId(testUser.getUserId());
        testNotification.setDate(LocalDateTime.now());
        testNotification.setIsRead(false);
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotificationsForUser() {
        List<Notification> expectedNotifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUserIdOrderByDateDesc(testUser.getUserId())).thenReturn(expectedNotifications);

        List<Notification> result = notificationService.getAllNotifications(testUser.getUserId());

        assertEquals(expectedNotifications, result);
        verify(notificationRepository, times(1)).findByUserIdOrderByDateDesc(testUser.getUserId());
    }

    @Test
    void getUnreadNotifications_ShouldReturnUnreadNotificationsForUser() {
        List<Notification> expectedNotifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(testUser.getUserId())).thenReturn(expectedNotifications);

        List<Notification> result = notificationService.getUnreadNotifications(testUser.getUserId());

        assertEquals(expectedNotifications, result);
        verify(notificationRepository, times(1)).findByUserIdAndIsReadFalseOrderByDateDesc(testUser.getUserId());
    }

    @Test
    void getNotificationById_ShouldReturnNotification_WhenExists() {
        when(notificationRepository.findById(testNotification.getNotificationId())).thenReturn(Optional.of(testNotification));

        Notification result = notificationService.getNotificationById(testNotification.getNotificationId());

        assertEquals(testNotification, result);
        verify(notificationRepository, times(1)).findById(testNotification.getNotificationId());
    }

    @Test
    void getNotificationById_ShouldThrowException_WhenNotExists() {
        when(notificationRepository.findById(testNotification.getNotificationId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(testNotification.getNotificationId()));
        verify(notificationRepository, times(1)).findById(testNotification.getNotificationId());
    }

    @Test
    void createNotification_ShouldCreateValidNotification() {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification result = notificationService.createNotification(testNotification);

        assertEquals(testNotification, result);
        verify(notificationRepository, times(1)).save(testNotification);
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
    void markAsRead_ShouldMarkNotificationAsRead() {
        when(notificationRepository.findById(testNotification.getNotificationId())).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification result = notificationService.markAsRead(testNotification.getNotificationId());

        assertTrue(result.getIsRead());
        verify(notificationRepository, times(1)).save(testNotification);
    }

    @Test
    void markAsRead_ShouldThrowException_WhenNotificationNotFound() {
        when(notificationRepository.findById(testNotification.getNotificationId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(testNotification.getNotificationId()));
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void deleteNotification_ShouldDeleteNotification() {
        when(notificationRepository.existsById(testNotification.getNotificationId())).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(testNotification.getNotificationId());

        notificationService.deleteNotification(testNotification.getNotificationId());

        verify(notificationRepository, times(1)).deleteById(testNotification.getNotificationId());
    }

    @Test
    void deleteNotification_ShouldThrowException_WhenNotificationNotFound() {
        when(notificationRepository.existsById(testNotification.getNotificationId())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> notificationService.deleteNotification(testNotification.getNotificationId()));
        verify(notificationRepository, never()).deleteById(anyLong());
    }
} 