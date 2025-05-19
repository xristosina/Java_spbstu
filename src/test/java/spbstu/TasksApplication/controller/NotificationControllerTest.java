package spbstu.TasksApplication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.service.NotificationService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private Notification notification1;
    private Notification notification2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        notification1 = Notification.builder()
                .notificationId(1L)
                .message("Test notification 1")
                .date(LocalDateTime.now())
                .userId(1L)
                .isRead(false)
                .build();

        notification2 = Notification.builder()
                .notificationId(2L)
                .message("Test notification 2")
                .date(LocalDateTime.now())
                .userId(1L)
                .isRead(true)
                .build();
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotifications() {
        // Arrange
        List<Notification> expectedNotifications = Arrays.asList(notification1, notification2);
        when(notificationService.getAllNotifications(1L)).thenReturn(expectedNotifications);

        // Act
        ResponseEntity<List<Notification>> response = notificationController.getAllNotifications(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedNotifications, response.getBody());
        verify(notificationService, times(1)).getAllNotifications(1L);
    }

    @Test
    void getPendingNotifications_ShouldReturnOnlyUnreadNotifications() {
        // Arrange
        List<Notification> expectedNotifications = List.of(notification1);
        when(notificationService.getPendingNotifications(1L)).thenReturn(expectedNotifications);

        // Act
        ResponseEntity<List<Notification>> response = notificationController.getPendingNotifications(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedNotifications, response.getBody());
        verify(notificationService, times(1)).getPendingNotifications(1L);
    }
} 