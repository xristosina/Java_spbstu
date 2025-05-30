package spbstu.TasksApplication.repository.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spbstu.TasksApplication.model.Notification;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Profile("h2")
public interface JpaNotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByDateDesc(Long userId);
    List<Notification> findByUserIdAndIsReadFalseOrderByDateDesc(Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.userId = ?1 AND n.date >= ?2 ORDER BY n.date DESC")
    List<Notification> findRecentNotifications(Long userId, LocalDateTime since);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = ?1 AND n.isRead = false")
    long countUnreadNotifications(Long userId);
    
    @Query("DELETE FROM Notification n WHERE n.userId = ?1 AND n.isRead = true")
    void deleteReadNotifications(Long userId);
} 