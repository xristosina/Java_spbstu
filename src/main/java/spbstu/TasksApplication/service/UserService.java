package spbstu.TasksApplication.service;

import spbstu.TasksApplication.model.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long userId);
    User getUserByUsername(String username);
    User createUser(User user);
    User updateUser(Long userId, User updatedUser);
    void deleteUser(Long userId);
}