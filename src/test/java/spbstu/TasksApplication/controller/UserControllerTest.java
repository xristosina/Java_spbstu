package spbstu.TasksApplication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.service.UserService;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private List<User> expectedUsers;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");

        expectedUsers = Arrays.asList(testUser);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedUsers, response.getBody());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        when(userService.getUserById(1L)).thenReturn(testUser);

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserByUsername_ShouldReturnUser() {
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);

        ResponseEntity<User> response = userController.getUserByUsername("testuser");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).getUserByUsername("testuser");
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        ResponseEntity<User> response = userController.createUser(testUser);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).createUser(testUser);
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(testUser);

        ResponseEntity<User> response = userController.updateUser(1L, testUser);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).updateUser(1L, testUser);
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(userService, times(1)).deleteUser(1L);
    }
} 