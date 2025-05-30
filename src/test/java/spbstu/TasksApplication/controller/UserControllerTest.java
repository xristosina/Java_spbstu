package spbstu.TasksApplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.service.UserService;
import spbstu.TasksApplication.exception.ResourceNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
    }

    @Test
    void registerUser_ShouldRegisterNewUser() throws Exception {
        when(userService.registerUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenUsernameIsEmpty() throws Exception {
        testUser.setUsername("");
        when(userService.registerUser(any(User.class))).thenThrow(new IllegalArgumentException("Username is empty"));

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreValid() throws Exception {
        when(userService.login("testuser", "password123")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/login")
                .param("username", "testuser")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void login_ShouldReturnNotFound_WhenCredentialsAreInvalid() throws Exception {
        when(userService.login("testuser", "wrongpassword")).thenReturn(null);

        mockMvc.perform(get("/api/users/login")
                .param("username", "testuser")
                .param("password", "wrongpassword"))
                .andExpect(status().isNotFound());
    }
} 