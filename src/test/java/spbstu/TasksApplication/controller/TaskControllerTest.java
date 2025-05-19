package spbstu.TasksApplication.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.service.TaskService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        task1 = Task.builder()
                .taskId(1L)
                .title("Test task 1")
                .description("Description 1")
                .creationDate(LocalDateTime.now())
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .isCompleted(false)
                .isDeleted(false)
                .build();

        task2 = Task.builder()
                .taskId(2L)
                .title("Test task 2")
                .description("Description 2")
                .creationDate(LocalDateTime.now())
                .targetDate(LocalDateTime.now().plusDays(2))
                .userId(1L)
                .isCompleted(true)
                .isDeleted(false)
                .build();
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        // Arrange
        List<Task> expectedTasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasks(1L)).thenReturn(expectedTasks);

        // Act
        ResponseEntity<List<Task>> response = taskController.getAllTasks(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedTasks, response.getBody());
        verify(taskService, times(1)).getAllTasks(1L);
    }

    @Test
    void getPendingTasks_ShouldReturnOnlyPendingTasks() {
        // Arrange
        List<Task> expectedTasks = List.of(task1);
        when(taskService.getPendingTasks(1L)).thenReturn(expectedTasks);

        // Act
        ResponseEntity<List<Task>> response = taskController.getPendingTasks(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedTasks, response.getBody());
        verify(taskService, times(1)).getPendingTasks(1L);
    }

    @Test
    void createTask_ShouldCreateNewTask() {
        // Arrange
        when(taskService.createTask(any(Task.class))).thenReturn(task1);

        // Act
        ResponseEntity<Task> response = taskController.createTask(task1);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(task1, response.getBody());
        verify(taskService, times(1)).createTask(task1);
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        // Act
        ResponseEntity<Void> response = taskController.deleteTask(1L);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(taskService, times(1)).deleteTask(1L);
    }
} 