package spbstu.TasksApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.UserRepository;
import spbstu.TasksApplication.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        user = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
    }

    @Test
    void registerUser_ShouldCreateValidUser() {
        // Arrange
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User registered = userService.registerUser(user);

        // Assert
        assertNotNull(registered);
        assertEquals(user.getUsername(), registered.getUsername());
        assertEquals(user.getPassword(), registered.getPassword());
        assertEquals(user.getEmail(), registered.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameIsEmpty() {
        // Arrange
        user.setUsername("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameIsTooShort() {
        // Arrange
        user.setUsername("ab");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordIsEmpty() {
        // Arrange
        user.setPassword("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordIsTooShort() {
        // Arrange
        user.setPassword("12345");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailIsInvalid() {
        // Arrange
        user.setEmail("invalid-email");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreValid() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // Act
        User loggedIn = userService.login(user.getUsername(), user.getPassword());

        // Assert
        assertNotNull(loggedIn);
        assertEquals(user.getUsername(), loggedIn.getUsername());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        // Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.login(user.getUsername(), "wrongpassword"));
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User found = userService.getUserById(1L);

        // Assert
        assertNotNull(found);
        assertEquals(user.getUsername(), found.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void updateUser_ShouldUpdateUser() {
        // Arrange
        User existingUser = User.builder()
                .userId(1L)
                .username("olduser")
                .password("oldpass")
                .email("old@example.com")
                .build();

        User updatedUser = User.builder()
                .username("updateduser")
                .password("newpass")
                .email("updated@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User updated = userService.updateUser(1L, updatedUser);

        // Assert
        assertEquals(updatedUser.getUsername(), updated.getUsername());
        assertEquals(updatedUser.getPassword(), updated.getPassword());
        assertEquals(updatedUser.getEmail(), updated.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Arrange
        User existingUser = User.builder()
                .userId(1L)
                .username("olduser")
                .password("oldpass")
                .email("old@example.com")
                .build();

        User updatedUser = User.builder()
                .username("existinguser")
                .password("newpass")
                .email("updated@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername(updatedUser.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, updatedUser));
        verify(userRepository, never()).save(any(User.class));
    }
} 