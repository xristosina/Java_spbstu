package spbstu.TasksApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.repository.TaskRepository;
import spbstu.TasksApplication.service.impl.TaskServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
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
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        // Act
        Task created = taskService.createTask(task1);

        // Assert
        assertNotNull(created);
        assertEquals(task1.getTitle(), created.getTitle());
        assertEquals(task1.getDescription(), created.getDescription());
        assertEquals(task1.getTargetDate(), created.getTargetDate());
        assertEquals(task1.getUserId(), created.getUserId());
        assertFalse(created.getIsCompleted());
        assertFalse(created.getIsDeleted());
        assertNotNull(created.getCreationDate());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_ShouldThrowException_WhenTitleIsEmpty() {
        // Arrange
        task1.setTitle("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task1));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_ShouldThrowException_WhenDescriptionIsEmpty() {
        // Arrange
        task1.setDescription("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task1));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_ShouldThrowException_WhenTargetDateIsInPast() {
        // Arrange
        task1.setTargetDate(LocalDateTime.now().minusDays(1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task1));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getAllTasks_ShouldReturnAllNonDeletedTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findByUserIdAndIsDeletedFalse(1L)).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getAllTasks(1L);

        // Assert
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findByUserIdAndIsDeletedFalse(1L);
    }

    @Test
    void getPendingTasks_ShouldReturnOnlyIncompleteAndNonDeletedTasks() {
        // Arrange
        List<Task> tasks = List.of(task1);
        when(taskRepository.findByUserIdAndIsCompletedFalseAndIsDeletedFalse(1L)).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getPendingTasks(1L);

        // Assert
        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsCompleted());
        verify(taskRepository, times(1)).findByUserIdAndIsCompletedFalseAndIsDeletedFalse(1L);
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenExists() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        // Act
        Task found = taskService.getTaskById(1L);

        // Assert
        assertNotNull(found);
        assertEquals(task1.getTitle(), found.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(999L));
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    void updateTask_ShouldUpdateTask() {
        // Arrange
        Task existingTask = Task.builder()
                .taskId(1L)
                .title("Old title")
                .description("Old description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .isCompleted(false)
                .isDeleted(false)
                .build();

        Task updatedTask = Task.builder()
                .title("New title")
                .description("New description")
                .targetDate(LocalDateTime.now().plusDays(2))
                .userId(1L)
                .isCompleted(true)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        // Act
        Task updated = taskService.updateTask(1L, updatedTask);

        // Assert
        assertEquals(updatedTask.getTitle(), updated.getTitle());
        assertEquals(updatedTask.getDescription(), updated.getDescription());
        assertEquals(updatedTask.getTargetDate(), updated.getTargetDate());
        assertEquals(updatedTask.getIsCompleted(), updated.getIsCompleted());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldMarkTaskAsDeleted() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        // Act
        taskService.deleteTask(1L);

        // Assert
        assertTrue(task1.getIsDeleted());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void completeTask_ShouldMarkTaskAsCompleted() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        // Act
        taskService.completeTask(1L);

        // Assert
        assertTrue(task1.getIsCompleted());
        verify(taskRepository, times(1)).save(any(Task.class));
    }
} 