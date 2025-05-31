package spbstu.TasksApplication.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("h2")
class JpaNotificationRepositoryTest {
    @Autowired
    private NotificationRepository repository;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = Notification.builder()
                .userId(1L)
                .taskId(1L)
                .text("Test notification")
                .date(LocalDateTime.now())
                .isRead(false)
                .build();
    }

    @Test
    void save_ShouldCreateNewNotification() {
        Notification savedNotification = repository.save(testNotification);
        
        assertNotNull(savedNotification.getNotificationId());
        assertEquals(testNotification.getText(), savedNotification.getText());
        assertEquals(testNotification.getUserId(), savedNotification.getUserId());
    }

    @Test
    void findById_ShouldReturnNotification_WhenExists() {
        Notification savedNotification = repository.save(testNotification);
        Optional<Notification> foundNotification = repository.findById(savedNotification.getNotificationId());
        
        assertTrue(foundNotification.isPresent());
        assertEquals(savedNotification.getNotificationId(), foundNotification.get().getNotificationId());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<Notification> foundNotification = repository.findById(999L);
        assertTrue(foundNotification.isEmpty());
    }

    @Test
    void findByUserIdOrderByDateDesc_ShouldReturnNotifications() {
        repository.save(testNotification);
        Notification notification2 = Notification.builder()
                .userId(1L)
                .taskId(2L)
                .text("Another notification")
                .date(LocalDateTime.now().plusHours(1))
                .isRead(false)
                .build();
        repository.save(notification2);

        List<Notification> notifications = repository.findByUserIdOrderByDateDesc(1L);
        
        assertEquals(2, notifications.size());
        assertTrue(notifications.get(0).getDate().isAfter(notifications.get(1).getDate()));
    }

    @Test
    void findByUserIdAndIsReadFalseOrderByDateDesc_ShouldReturnOnlyUnreadNotifications() {
        Notification unreadNotification = repository.save(testNotification);
        Notification readNotification = Notification.builder()
                .userId(1L)
                .taskId(2L)
                .text("Read notification")
                .date(LocalDateTime.now())
                .isRead(true)
                .build();
        repository.save(readNotification);

        List<Notification> notifications = repository.findByUserIdAndIsReadFalseOrderByDateDesc(1L);
        
        assertEquals(1, notifications.size());
        assertEquals(unreadNotification.getNotificationId(), notifications.get(0).getNotificationId());
    }
} 