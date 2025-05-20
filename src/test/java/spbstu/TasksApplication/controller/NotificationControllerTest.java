package spbstu.TasksApplication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.service.NotificationService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private Notification testNotification;
    private List<Notification> expectedNotifications;

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setNotificationId(1L);
        testNotification.setMessage("Test notification");
        testNotification.setUserId(1L);
        testNotification.setDate(LocalDateTime.now());
        testNotification.setIsRead(false);

        expectedNotifications = Arrays.asList(testNotification);
    }

    @Test
    void getAllNotifications_ShouldReturnNotifications() {
        when(notificationService.getAllNotifications(1L)).thenReturn(expectedNotifications);

        ResponseEntity<List<Notification>> response = notificationController.getAllNotifications(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedNotifications, response.getBody());
        verify(notificationService, times(1)).getAllNotifications(1L);
    }

    @Test
    void getUnreadNotifications_ShouldReturnUnreadNotifications() {
        when(notificationService.getUnreadNotifications(1L)).thenReturn(expectedNotifications);

        ResponseEntity<List<Notification>> response = notificationController.getUnreadNotifications(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedNotifications, response.getBody());
        verify(notificationService, times(1)).getUnreadNotifications(1L);
    }

    @Test
    void getNotificationById_ShouldReturnNotification() {
        when(notificationService.getNotificationById(1L)).thenReturn(testNotification);

        ResponseEntity<Notification> response = notificationController.getNotificationById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testNotification, response.getBody());
        verify(notificationService, times(1)).getNotificationById(1L);
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        when(notificationService.markAsRead(1L)).thenReturn(testNotification);

        ResponseEntity<Notification> response = notificationController.markAsRead(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testNotification, response.getBody());
        verify(notificationService, times(1)).markAsRead(1L);
    }

    @Test
    void deleteNotification_ShouldDeleteNotification() {
        doNothing().when(notificationService).deleteNotification(1L);

        ResponseEntity<Void> response = notificationController.deleteNotification(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(notificationService, times(1)).deleteNotification(1L);
    }
} 