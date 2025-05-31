package spbstu.TasksApplication.repository.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.UserRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("inmemory")
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong userIdCounter = new AtomicLong(1);

    @Override
    public User save(User user) {
        if (user.getUserId() == null) {
            user.setUserId(userIdCounter.getAndIncrement());
        }
        users.put(user.getUserId(), user);
        return users.get(user.getUserId());
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        return users.values().stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
} 