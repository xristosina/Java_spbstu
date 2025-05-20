package spbstu.TasksApplication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.service.TaskService;
import spbstu.TasksApplication.service.UserService;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final TaskService taskService;
    private final UserService userService;

    @PostMapping("/task")
    public ResponseEntity<Task> createTestTask() {
        // Create a test user if not exists
        User testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        try {
            testUser = userService.createUser(testUser);
        } catch (Exception e) {
            // User might already exist, try to get by username
            testUser = userService.getUserByUsername("testuser");
        }

        // Create a test task
        Task testTask = Task.builder()
                .title("Test Task " + System.currentTimeMillis())
                .description("This is a test task created at " + System.currentTimeMillis())
                .userId(testUser.getUserId())
                .targetDate(java.time.LocalDateTime.now().plusDays(1))
                .build();

        Task createdTask = taskService.createTask(testTask);
        return ResponseEntity.ok(createdTask);
    }
} 