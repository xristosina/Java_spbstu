package spbstu.TasksApplication.repository;

import spbstu.TasksApplication.model.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Optional<Notification> findById(Long notificationId);
    List<Notification> findByUserIdOrderByDateDesc(Long userId);
    List<Notification> findByUserIdAndIsReadFalseOrderByDateDesc(Long userId);
}