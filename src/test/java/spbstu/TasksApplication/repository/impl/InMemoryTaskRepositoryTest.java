package spbstu.TasksApplication.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.model.Task;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskRepositoryTest {
    private InMemoryTaskRepository repository;
    private Task testTask;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskRepository();
        testTask = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .build();
    }

    @Test
    void save_ShouldCreateNewTask() {
        Task savedTask = repository.save(testTask);
        
        assertNotNull(savedTask.getTaskId());
        assertEquals(testTask.getTitle(), savedTask.getTitle());
        assertEquals(testTask.getDescription(), savedTask.getDescription());
        assertEquals(testTask.getUserId(), savedTask.getUserId());
    }

    @Test
    void findById_ShouldReturnTask_WhenExists() {
        Task savedTask = repository.save(testTask);
        Optional<Task> foundTask = repository.findById(savedTask.getTaskId());
        
        assertTrue(foundTask.isPresent());
        assertEquals(savedTask.getTaskId(), foundTask.get().getTaskId());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<Task> foundTask = repository.findById(999L);
        assertTrue(foundTask.isEmpty());
    }

    @Test
    void findByUserIdAndIsDeletedFalse_ShouldReturnOnlyNotDeletedTasks() {
        Task task1 = repository.save(testTask);
        Task task2 = Task.builder()
                .title("Deleted Task")
                .description("Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .isDeleted(true)
                .build();
        repository.save(task2);

        List<Task> tasks = repository.findByUserIdAndIsDeletedFalse(1L);
        
        assertEquals(1, tasks.size());
        assertEquals(task1.getTaskId(), tasks.get(0).getTaskId());
    }

    @Test
    void findByUserIdAndIsCompletedFalseAndIsDeletedFalse_ShouldReturnOnlyActiveTasks() {
        Task activeTask = repository.save(testTask);
        Task completedTask = Task.builder()
                .title("Completed Task")
                .description("Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .isCompleted(true)
                .build();
        repository.save(completedTask);

        List<Task> tasks = repository.findByUserIdAndIsCompletedFalseAndIsDeletedFalse(1L);
        
        assertEquals(1, tasks.size());
        assertEquals(activeTask.getTaskId(), tasks.get(0).getTaskId());
    }
} 