package spbstu.TasksApplication.repository;

import spbstu.TasksApplication.model.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(Long notificationId);
    List<Notification> findByUserIdOrderByDateDesc(Long userId);
    List<Notification> findByUserIdAndIsReadFalseOrderByDateDesc(Long userId);
    void delete(Notification notification);
    void deleteById(Long notificationId);
}