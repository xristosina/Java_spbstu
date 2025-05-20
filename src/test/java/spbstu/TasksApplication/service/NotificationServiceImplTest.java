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
                .userId(testUser.getUserId())
                .date(LocalDateTime.now())
                .isRead(false)
                .build();
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
    void getUnreadNotifications_ShouldReturnOnlyUnreadNotifications() {
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByDateDesc(testUser.getUserId()))
                .thenReturn(notifications);

        List<Notification> foundNotifications = notificationService.getUnreadNotifications(testUser.getUserId());

        assertNotNull(foundNotifications);
        assertEquals(1, foundNotifications.size());
        verify(notificationRepository).findByUserIdAndIsReadFalseOrderByDateDesc(testUser.getUserId());
    }

    @Test
    void getNotificationById_ShouldReturnNotification_WhenExists() {
        when(notificationRepository.findById(testNotification.getNotificationId()))
                .thenReturn(Optional.of(testNotification));

        Notification foundNotification = notificationService.getNotificationById(testNotification.getNotificationId());

        assertNotNull(foundNotification);
        assertEquals(testNotification.getNotificationId(), foundNotification.getNotificationId());
        verify(notificationRepository).findById(testNotification.getNotificationId());
    }

    @Test
    void getNotificationById_ShouldThrowException_WhenNotExists() {
        when(notificationRepository.findById(testNotification.getNotificationId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.getNotificationById(testNotification.getNotificationId()));
        verify(notificationRepository).findById(testNotification.getNotificationId());
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        when(notificationRepository.findById(testNotification.getNotificationId()))
                .thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification markedNotification = notificationService.markAsRead(testNotification.getNotificationId());

        assertTrue(markedNotification.getIsRead());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void deleteNotification_ShouldDeleteNotification() {
        when(notificationRepository.findById(testNotification.getNotificationId()))
                .thenReturn(Optional.of(testNotification));

        notificationService.deleteNotification(testNotification.getNotificationId());

        verify(notificationRepository).delete(testNotification);
    }
} 