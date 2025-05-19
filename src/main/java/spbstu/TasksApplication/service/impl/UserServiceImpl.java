package spbstu.TasksApplication.service.impl;

import org.springframework.stereotype.Service;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.UserRepository;
import spbstu.TasksApplication.service.UserService;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User user) {
        validateUser(user);
        checkUsernameExists(user.getUsername());
        checkEmailExists(user.getEmail());
        
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Override
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = getUserById(userId);
        validateUser(updatedUser);
        
        if (!existingUser.getUsername().equals(updatedUser.getUsername())) {
            checkUsernameExists(updatedUser.getUsername());
        }
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            checkEmailExists(updatedUser.getEmail());
        }
        
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        
        return userRepository.save(existingUser);
    }

    @Override
    public User createUser(User user) {
        validateUser(user);
        checkUsernameExists(user.getUsername());
        checkEmailExists(user.getEmail());
        return userRepository.save(user);
    }

    private void validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (user.getUsername().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void checkUsernameExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    private void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
} 