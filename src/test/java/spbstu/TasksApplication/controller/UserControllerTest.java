package spbstu.TasksApplication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        user = User.builder()
                .userId(1L)
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
    }

    @Test
    void registerUser_ShouldRegisterNewUser() {
        // Arrange
        when(userService.registerUser(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.registerUser(user);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).registerUser(user);
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreValid() {
        // Arrange
        when(userService.login("testuser", "password123")).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.login("testuser", "password123");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).login("testuser", "password123");
    }
} 