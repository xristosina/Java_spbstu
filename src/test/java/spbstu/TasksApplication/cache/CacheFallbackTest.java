package spbstu.TasksApplication.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.TaskRepository;
import spbstu.TasksApplication.repository.UserRepository;
import spbstu.TasksApplication.service.TaskService;
import spbstu.TasksApplication.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CacheFallbackTest {
    private static final Logger logger = LoggerFactory.getLogger(CacheFallbackTest.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;
    private Long testTaskId;

    @BeforeEach
    void setUp() {
        // Clear all caches
        cacheManager.getCache("tasks").clear();
        cacheManager.getCache("users").clear();

        // Remove all data
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
    }

    @Test
    void testCacheFallbackForTask() {
        logger.info("Starting cache fallback test for task");

        // First call - should hit the database
        logger.info("First call - should hit the database");
        long startTime1 = System.currentTimeMillis();
        Task task1 = taskService.getTaskById(testTaskId);
        long endTime1 = System.currentTimeMillis();
        long firstCallTime = endTime1 - startTime1;
        logger.info("First call completed in {} ms", firstCallTime);

        // Verify cache entry exists
        assertNotNull(cacheManager.getCache("tasks").get(testTaskId.toString()),
            "Cache entry should exist after first call");

        // Second call - should hit the cache
        logger.info("Second call - should hit the cache");
        long startTime2 = System.currentTimeMillis();
        Task task2 = taskService.getTaskById(testTaskId);
        long endTime2 = System.currentTimeMillis();
        long secondCallTime = endTime2 - startTime2;
        logger.info("Second call completed in {} ms", secondCallTime);

        // Verify results are the same
        assertEquals(task1, task2, "Cached and database results should be identical");

        // Verify second call was faster (cached)
        assertTrue(secondCallTime < firstCallTime,
            "Second call should be faster than first call. First call: " + firstCallTime +
            "ms, Second call: " + secondCallTime + "ms");

        // Clear the cache
        logger.info("Clearing the cache");
        cacheManager.getCache("tasks").clear();

        // Verify cache is empty
        assertNull(cacheManager.getCache("tasks").get(testTaskId.toString()),
            "Cache should be empty after clearing");

        // Third call - should hit the database again
        logger.info("Third call - should hit the database again");
        long startTime3 = System.currentTimeMillis();
        Task task3 = taskService.getTaskById(testTaskId);
        long endTime3 = System.currentTimeMillis();
        long thirdCallTime = endTime3 - startTime3;
        logger.info("Third call completed in {} ms", thirdCallTime);

        // Verify results are still the same
        assertEquals(task1, task3, "Results should be identical after cache clear");

        // Verify third call took similar time to first call
        assertTrue(Math.abs(thirdCallTime - firstCallTime) < 100,
            "Third call should take similar time to first call. First call: " + firstCallTime +
            "ms, Third call: " + thirdCallTime + "ms");
    }

    @Test
    void testCacheFallbackForUserTasks() {
        logger.info("Starting cache fallback test for user tasks");

        // First call - should hit the database
        logger.info("First call - should hit the database");
        long startTime1 = System.currentTimeMillis();
        List<Task> tasks1 = taskService.getAllTasks(testUserId);
        long endTime1 = System.currentTimeMillis();
        long firstCallTime = endTime1 - startTime1;
        logger.info("First call completed in {} ms", firstCallTime);

        // Verify cache entry exists
        assertNotNull(cacheManager.getCache("tasks").get(testUserId.toString()),
            "Cache entry should exist after first call");

        // Second call - should hit the cache
        logger.info("Second call - should hit the cache");
        long startTime2 = System.currentTimeMillis();
        List<Task> tasks2 = taskService.getAllTasks(testUserId);
        long endTime2 = System.currentTimeMillis();
        long secondCallTime = endTime2 - startTime2;
        logger.info("Second call completed in {} ms", secondCallTime);

        // Verify results are the same
        assertEquals(tasks1, tasks2, "Cached and database results should be identical");

        // Verify second call was faster (cached)
        assertTrue(secondCallTime < firstCallTime,
            "Second call should be faster than first call. First call: " + firstCallTime +
            "ms, Second call: " + secondCallTime + "ms");

        // Clear the cache
        logger.info("Clearing the cache");
        cacheManager.getCache("tasks").clear();

        // Verify cache is empty
        assertNull(cacheManager.getCache("tasks").get(testUserId.toString()),
            "Cache should be empty after clearing");

        // Third call - should hit the database again
        logger.info("Third call - should hit the database again");
        long startTime3 = System.currentTimeMillis();
        List<Task> tasks3 = taskService.getAllTasks(testUserId);
        long endTime3 = System.currentTimeMillis();
        long thirdCallTime = endTime3 - startTime3;
        logger.info("Third call completed in {} ms", thirdCallTime);

        // Verify results are still the same
        assertEquals(tasks1, tasks3, "Results should be identical after cache clear");

        // Verify third call took similar time to first call
        assertTrue(Math.abs(thirdCallTime - firstCallTime) < 100,
            "Third call should take similar time to first call. First call: " + firstCallTime +
            "ms, Third call: " + thirdCallTime + "ms");
    }
} 