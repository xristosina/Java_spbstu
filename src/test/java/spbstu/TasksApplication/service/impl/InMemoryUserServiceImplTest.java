package spbstu.TasksApplication.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.User;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserServiceImplTest {
    private InMemoryUserServiceImpl userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new InMemoryUserServiceImpl();
        testUser = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
    }

    @Test
    void registerUser_ShouldCreateNewUser() {
        User registeredUser = userService.registerUser(testUser);
        
        assertNotNull(registeredUser.getUserId());
        assertEquals(testUser.getUsername(), registeredUser.getUsername());
        assertEquals(testUser.getEmail(), registeredUser.getEmail());
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameIsEmpty() {
        testUser.setUsername("");
        
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailIsInvalid() {
        testUser.setEmail("invalid-email");
        
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        userService.registerUser(testUser);
        
        User duplicateUser = User.builder()
                .username("testuser")
                .password("password456")
                .email("another@example.com")
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(duplicateUser));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        userService.registerUser(testUser);
        
        User duplicateUser = User.builder()
                .username("anotheruser")
                .password("password456")
                .email("test@example.com")
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(duplicateUser));
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreValid() {
        User registeredUser = userService.registerUser(testUser);
        User loggedInUser = userService.login("testuser", "password123");
        
        assertNotNull(loggedInUser);
        assertEquals(registeredUser.getUserId(), loggedInUser.getUserId());
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        userService.registerUser(testUser);
        
        assertThrows(ResourceNotFoundException.class, () -> userService.login("testuser", "wrongpassword"));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        User registeredUser = userService.registerUser(testUser);
        User foundUser = userService.getUserById(registeredUser.getUserId());
        
        assertNotNull(foundUser);
        assertEquals(registeredUser.getUserId(), foundUser.getUserId());
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }
} 