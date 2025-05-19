package spbstu.TasksApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.service.impl.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        
        user = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
    }

    @Test
    void registerUser_ShouldCreateValidUser() {
        // Act
        User registered = userService.registerUser(user);

        // Assert
        assertNotNull(registered);
        assertNotNull(registered.getUserId());
        assertEquals(user.getUsername(), registered.getUsername());
        assertEquals(user.getPassword(), registered.getPassword());
        assertEquals(user.getEmail(), registered.getEmail());
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameIsEmpty() {
        // Arrange
        user.setUsername("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameIsTooShort() {
        // Arrange
        user.setUsername("ab");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordIsEmpty() {
        // Arrange
        user.setPassword("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordIsTooShort() {
        // Arrange
        user.setPassword("12345");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailIsInvalid() {
        // Arrange
        user.setEmail("invalid-email");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Arrange
        userService.registerUser(user);
        User duplicateUser = User.builder()
                .username("testuser")
                .password("different123")
                .email("different@example.com")
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(duplicateUser));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        userService.registerUser(user);
        User duplicateUser = User.builder()
                .username("differentuser")
                .password("different123")
                .email("test@example.com")
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(duplicateUser));
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreValid() {
        // Arrange
        User registered = userService.registerUser(user);

        // Act
        User loggedIn = userService.login("testuser", "password123");

        // Assert
        assertNotNull(loggedIn);
        assertEquals(registered.getUserId(), loggedIn.getUserId());
        assertEquals(registered.getUsername(), loggedIn.getUsername());
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        // Arrange
        userService.registerUser(user);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.login("testuser", "wrongpassword"));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        // Arrange
        User registered = userService.registerUser(user);

        // Act
        User found = userService.getUserById(registered.getUserId());

        // Assert
        assertNotNull(found);
        assertEquals(registered.getUserId(), found.getUserId());
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotFound() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void updateUser_ShouldUpdateUser() {
        // Arrange
        User registered = userService.registerUser(user);
        User updatedUser = User.builder()
                .username("updateduser")
                .password("newpassword123")
                .email("updated@example.com")
                .build();

        // Act
        User updated = userService.updateUser(registered.getUserId(), updatedUser);

        // Assert
        assertEquals(updatedUser.getUsername(), updated.getUsername());
        assertEquals(updatedUser.getPassword(), updated.getPassword());
        assertEquals(updatedUser.getEmail(), updated.getEmail());
    }

    @Test
    void updateUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Arrange
        User firstUser = User.builder()
                .username("firstuser")
                .password("password123")
                .email("first@example.com")
                .build();
        User secondUser = User.builder()
                .username("seconduser")
                .password("password123")
                .email("second@example.com")
                .build();
        userService.registerUser(firstUser);
        User registered = userService.registerUser(secondUser);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            userService.updateUser(registered.getUserId(), firstUser));
    }
} 