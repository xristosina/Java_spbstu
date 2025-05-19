package spbstu.TasksApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.service.impl.TaskServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceImplTest {

    private TaskServiceImpl taskService;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl();
        
        task1 = Task.builder()
                .title("Test task 1")
                .description("Description 1")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .isCompleted(false)
                .isDeleted(false)
                .build();

        task2 = Task.builder()
                .title("Test task 2")
                .description("Description 2")
                .targetDate(LocalDateTime.now().plusDays(2))
                .userId(1L)
                .isCompleted(true)
                .isDeleted(false)
                .build();
    }

    @Test
    void createTask_ShouldCreateValidTask() {
        // Act
        Task created = taskService.createTask(task1);

        // Assert
        assertNotNull(created);
        assertNotNull(created.getTaskId());
        assertEquals(task1.getTitle(), created.getTitle());
        assertEquals(task1.getDescription(), created.getDescription());
        assertEquals(task1.getTargetDate(), created.getTargetDate());
        assertEquals(task1.getUserId(), created.getUserId());
        assertFalse(created.getIsCompleted());
        assertFalse(created.getIsDeleted());
        assertNotNull(created.getCreationDate());
    }

    @Test
    void createTask_ShouldThrowException_WhenTitleIsEmpty() {
        // Arrange
        task1.setTitle("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task1));
    }

    @Test
    void createTask_ShouldThrowException_WhenDescriptionIsEmpty() {
        // Arrange
        task1.setDescription("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task1));
    }

    @Test
    void createTask_ShouldThrowException_WhenTargetDateIsInPast() {
        // Arrange
        task1.setTargetDate(LocalDateTime.now().minusDays(1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task1));
    }

    @Test
    void getAllTasks_ShouldReturnAllTasksForUser() {
        // Arrange
        taskService.createTask(task1);
        taskService.createTask(task2);

        // Act
        List<Task> tasks = taskService.getAllTasks(1L);

        // Assert
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(t -> t.getUserId().equals(1L) && !t.getIsDeleted()));
    }

    @Test
    void getPendingTasks_ShouldReturnOnlyPendingTasks() {
        // Arrange
        taskService.createTask(task1);
        taskService.createTask(task2);

        // Act
        List<Task> tasks = taskService.getPendingTasks(1L);

        // Assert
        assertEquals(1, tasks.size());
        assertTrue(tasks.stream().allMatch(t -> !t.getIsCompleted() && !t.getIsDeleted()));
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenExists() {
        // Arrange
        Task created = taskService.createTask(task1);

        // Act
        Task found = taskService.getTaskById(created.getTaskId());

        // Assert
        assertNotNull(found);
        assertEquals(created.getTaskId(), found.getTaskId());
    }

    @Test
    void getTaskById_ShouldThrowException_WhenNotFound() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    void updateTask_ShouldUpdateTask() {
        // Arrange
        Task created = taskService.createTask(task1);
        Task updatedTask = Task.builder()
                .title("Updated title")
                .description("Updated description")
                .targetDate(LocalDateTime.now().plusDays(3))
                .userId(1L)
                .isCompleted(false)
                .build();

        // Act
        Task updated = taskService.updateTask(created.getTaskId(), updatedTask);

        // Assert
        assertEquals(updatedTask.getTitle(), updated.getTitle());
        assertEquals(updatedTask.getDescription(), updated.getDescription());
        assertEquals(updatedTask.getTargetDate(), updated.getTargetDate());
    }

    @Test
    void deleteTask_ShouldMarkTaskAsDeleted() {
        // Arrange
        Task created = taskService.createTask(task1);

        // Act
        taskService.deleteTask(created.getTaskId());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(created.getTaskId()));
    }

    @Test
    void completeTask_ShouldMarkTaskAsCompleted() {
        // Arrange
        Task created = taskService.createTask(task1);

        // Act
        taskService.completeTask(created.getTaskId());

        // Assert
        Task completed = taskService.getTaskById(created.getTaskId());
        assertTrue(completed.getIsCompleted());
    }
} 