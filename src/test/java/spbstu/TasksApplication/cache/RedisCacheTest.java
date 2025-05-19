package spbstu.TasksApplication.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.NotificationRepository;
import spbstu.TasksApplication.repository.TaskRepository;
import spbstu.TasksApplication.repository.UserRepository;
import spbstu.TasksApplication.service.NotificationService;
import spbstu.TasksApplication.service.TaskService;
import spbstu.TasksApplication.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RedisCacheTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private Long testUserId;
    private Long testTaskId;
    private Long testNotificationId;

    @BeforeEach
    void setUp() {
        // Clear all caches
        cacheManager.getCache("tasks").clear();
        cacheManager.getCache("users").clear();
        cacheManager.getCache("notifications").clear();

        // Remove all data
        notificationRepository.deleteAll();
        taskRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        User testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        testUser = userRepository.save(testUser);
        testUserId = testUser.getUserId();

        // Create test task
        Task testTask = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .userId(testUserId)
                .isCompleted(false)
                .isDeleted(false)
                .creationDate(LocalDateTime.now())
                .targetDate(LocalDateTime.now().plusDays(1))
                .build();
        testTask = taskRepository.save(testTask);
        testTaskId = testTask.getTaskId();

        // Create test notification
        Notification testNotification = Notification.builder()
                .message("Test notification")
                .userId(testUserId)
                .date(LocalDateTime.now())
                .isRead(false)
                .build();
        testNotification = notificationRepository.save(testNotification);
        testNotificationId = testNotification.getNotificationId();
    }

    @Test
    void testTaskCaching() {
        // First call - should hit the database
        long startTime1 = System.currentTimeMillis();
        List<Task> tasks1 = taskService.getAllTasks(testUserId);
        long endTime1 = System.currentTimeMillis();
        long firstCallTime = endTime1 - startTime1;

        // Second call - should hit the cache
        long startTime2 = System.currentTimeMillis();
        List<Task> tasks2 = taskService.getAllTasks(testUserId);
        long endTime2 = System.currentTimeMillis();
        long secondCallTime = endTime2 - startTime2;

        // Verify results are the same
        assertEquals(tasks1, tasks2);

        // Verify second call was faster (cached)
        assertTrue(secondCallTime < firstCallTime, 
            "Second call should be faster than first call. First call: " + firstCallTime + 
            "ms, Second call: " + secondCallTime + "ms");

        // Verify cache entry exists
        assertNotNull(cacheManager.getCache("tasks").get(testUserId.toString()));
    }

    @Test
    void testTaskByIdCaching() {
        // First call - should hit the database
        long startTime1 = System.currentTimeMillis();
        Task task1 = taskService.getTaskById(testTaskId);
        long endTime1 = System.currentTimeMillis();
        long firstCallTime = endTime1 - startTime1;

        // Second call - should hit the cache
        long startTime2 = System.currentTimeMillis();
        Task task2 = taskService.getTaskById(testTaskId);
        long endTime2 = System.currentTimeMillis();
        long secondCallTime = endTime2 - startTime2;

        // Verify results are the same
        assertEquals(task1, task2);

        // Verify second call was faster (cached)
        assertTrue(secondCallTime < firstCallTime,
            "Second call should be faster than first call. First call: " + firstCallTime +
            "ms, Second call: " + secondCallTime + "ms");

        // Verify cache entry exists
        assertNotNull(cacheManager.getCache("tasks").get(testTaskId.toString()));
    }

    @Test
    void testUserCaching() {
        // First call - should hit the database
        long startTime1 = System.currentTimeMillis();
        User user1 = userService.getUserById(testUserId);
        long endTime1 = System.currentTimeMillis();
        long firstCallTime = endTime1 - startTime1;

        // Second call - should hit the cache
        long startTime2 = System.currentTimeMillis();
        User user2 = userService.getUserById(testUserId);
        long endTime2 = System.currentTimeMillis();
        long secondCallTime = endTime2 - startTime2;

        // Verify results are the same
        assertEquals(user1, user2);

        // Verify second call was faster (cached)
        assertTrue(secondCallTime < firstCallTime,
            "Second call should be faster than first call. First call: " + firstCallTime +
            "ms, Second call: " + secondCallTime + "ms");

        // Verify cache entry exists
        assertNotNull(cacheManager.getCache("users").get(testUserId.toString()));
    }

    @Test
    void testNotificationCaching() {
        // First call - should hit the database
        long startTime1 = System.currentTimeMillis();
        List<Notification> notifications1 = notificationService.getAllNotifications(testUserId);
        long endTime1 = System.currentTimeMillis();
        long firstCallTime = endTime1 - startTime1;

        // Second call - should hit the cache
        long startTime2 = System.currentTimeMillis();
        List<Notification> notifications2 = notificationService.getAllNotifications(testUserId);
        long endTime2 = System.currentTimeMillis();
        long secondCallTime = endTime2 - startTime2;

        // Verify results are the same
        assertEquals(notifications1, notifications2);

        // Verify second call was faster (cached)
        assertTrue(secondCallTime < firstCallTime,
            "Second call should be faster than first call. First call: " + firstCallTime +
            "ms, Second call: " + secondCallTime + "ms");

        // Verify cache entry exists
        assertNotNull(cacheManager.getCache("notifications").get(testUserId.toString()));
    }

    @Test
    void testCacheEviction() {
        // First get the task
        Task task = taskService.getTaskById(testTaskId);
        
        // Verify it's in cache
        assertNotNull(cacheManager.getCache("tasks").get(testTaskId.toString()));

        // Update the task
        task.setTitle("Updated Title");
        taskService.updateTask(testTaskId, task);

        // Verify cache was evicted
        assertNull(cacheManager.getCache("tasks").get(testTaskId.toString()));

        // Verify cache is updated after new fetch
        Task updatedTask = taskService.getTaskById(testTaskId);
        assertEquals("Updated Title", updatedTask.getTitle());
        assertNotNull(cacheManager.getCache("tasks").get(testTaskId.toString()));
    }

    @Test
    void testCacheEvictionOnDelete() {
        // First get the task
        Task task = taskService.getTaskById(testTaskId);
        
        // Verify it's in cache
        assertNotNull(cacheManager.getCache("tasks").get(testTaskId.toString()));

        // Delete the task
        taskService.deleteTask(testTaskId);

        // Verify cache was evicted
        assertNull(cacheManager.getCache("tasks").get(testTaskId.toString()));
    }
} 