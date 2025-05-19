package spbstu.TasksApplication.service;

import spbstu.TasksApplication.model.User;

public interface UserService {
    User registerUser(User user);
    User login(String username, String password);
    User getUserById(Long userId);
    User updateUser(Long userId, User updatedUser);
    User createUser(User user);
}