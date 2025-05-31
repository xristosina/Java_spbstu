package spbstu.TasksApplication.repository.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.repository.NotificationRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("inmemory")
public class InMemoryNotificationRepository implements NotificationRepository {
    private final Map<Long, Notification> notifications = new HashMap<>();
    private final AtomicLong notificationIdCounter = new AtomicLong(1);

    @Override
    public Optional<Notification> findById(Long notificationId) {
        return Optional.ofNullable(notifications.get(notificationId));
    }

    @Override
    public List<Notification> findByUserIdOrderByDateDesc(Long userId) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : notifications.values()) {
            if (n.getUserId().equals(userId)) {
                result.add(n);
            }
        }
        result.sort(Comparator.comparing(Notification::getDate).reversed());
        return result;
    }

    @Override
    public List<Notification> findByUserIdAndIsReadFalseOrderByDateDesc(Long userId) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : notifications.values()) {
            if (n.getUserId().equals(userId) && !n.getIsRead()) {
                result.add(n);
            }
        }
        result.sort(Comparator.comparing(Notification::getDate).reversed());
        return result;
    }
}