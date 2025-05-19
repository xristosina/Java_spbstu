package spbstu.TasksApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.UserRepository;
import spbstu.TasksApplication.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .build();
    }

    @Test
    void createUser_ShouldCreateValidUser() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User createdUser = userService.createUser(testUser);

        assertNotNull(createdUser);
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertEquals(testUser.getEmail(), createdUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(testUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(testUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals(testUser.getUserId(), foundUser.getUserId());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void registerUser_ShouldCreateValidUser() {
        // Arrange
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User registered = userService.registerUser(testUser);

        // Assert
        assertNotNull(registered);
        assertEquals(testUser.getUsername(), registered.getUsername());
        assertEquals(testUser.getPassword(), registered.getPassword());
        assertEquals(testUser.getEmail(), registered.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameIsEmpty() {
        // Arrange
        testUser.setUsername("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameIsTooShort() {
        // Arrange
        testUser.setUsername("ab");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordIsEmpty() {
        // Arrange
        testUser.setPassword("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordIsTooShort() {
        // Arrange
        testUser.setPassword("12345");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailIsInvalid() {
        // Arrange
        testUser.setEmail("invalid-email");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreValid() {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Act
        User loggedIn = userService.login(testUser.getUsername(), testUser.getPassword());

        // Assert
        assertNotNull(loggedIn);
        assertEquals(testUser.getUsername(), loggedIn.getUsername());
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        // Arrange
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.login(testUser.getUsername(), "wrongpassword"));
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
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