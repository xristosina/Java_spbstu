package spbstu.TasksApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.TaskRepository;
import spbstu.TasksApplication.repository.UserRepository;
import spbstu.TasksApplication.service.impl.TaskServiceImpl;
import spbstu.TasksApplication.config.RabbitMQConfig;
import spbstu.TasksApplication.messaging.TaskCreatedMessage;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task testTask;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .build();

        testTask = Task.builder()
                .taskId(1L)
                .title("Test Task")
                .description("Test Description")
                .creationDate(LocalDateTime.now())
                .targetDate(LocalDateTime.now().plusDays(1))
                .isCompleted(false)
                .isDeleted(false)
                .userId(testUser.getUserId())
                .build();
    }

    @Test
    void createTask_ShouldCreateValidTask() {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task createdTask = taskService.createTask(testTask);

        assertNotNull(createdTask);
        assertEquals(testTask.getTitle(), createdTask.getTitle());
        assertEquals(testTask.getDescription(), createdTask.getDescription());
        verify(taskRepository).save(any(Task.class));
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConfig.TASK_CREATED_EXCHANGE),
            eq(RabbitMQConfig.TASK_CREATED_ROUTING_KEY),
            any(TaskCreatedMessage.class)
        );
    }

    @Test
    void createTask_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(testTask));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenExists() {
        when(taskRepository.findById(testTask.getTaskId())).thenReturn(Optional.of(testTask));

        Task foundTask = taskService.getTaskById(testTask.getTaskId());

        assertNotNull(foundTask);
        assertEquals(testTask.getTaskId(), foundTask.getTaskId());
        verify(taskRepository).findById(testTask.getTaskId());
    }

    @Test
    void getTaskById_ShouldThrowException_WhenNotExists() {
        when(taskRepository.findById(testTask.getTaskId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(testTask.getTaskId()));
        verify(taskRepository).findById(testTask.getTaskId());
    }

    @Test
    void getAllTasks_ShouldReturnAllTasksForUser() {
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findByUserIdAndIsDeletedFalse(testUser.getUserId())).thenReturn(tasks);

        List<Task> foundTasks = taskService.getAllTasks(testUser.getUserId());

        assertNotNull(foundTasks);
        assertEquals(1, foundTasks.size());
        verify(taskRepository).findByUserIdAndIsDeletedFalse(testUser.getUserId());
    }

    @Test
    void getPendingTasks_ShouldReturnPendingTasksForUser() {
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findByUserIdAndIsCompletedFalseAndIsDeletedFalse(testUser.getUserId()))
                .thenReturn(tasks);

        List<Task> foundTasks = taskService.getPendingTasks(testUser.getUserId());

        assertNotNull(foundTasks);
        assertEquals(1, foundTasks.size());
        verify(taskRepository).findByUserIdAndIsCompletedFalseAndIsDeletedFalse(testUser.getUserId());
    }

    @Test
    void updateTask_ShouldUpdateTask() {
        Task updatedTask = Task.builder()
                .taskId(1L)
                .title("Updated Task")
                .description("Updated Description")
                .creationDate(LocalDateTime.now())
                .targetDate(LocalDateTime.now().plusDays(2))
                .isCompleted(true)
                .isDeleted(false)
                .userId(testUser.getUserId())
                .build();

        when(taskRepository.findById(testTask.getTaskId())).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = taskService.updateTask(testTask.getTaskId(), updatedTask);

        assertNotNull(result);
        assertEquals(updatedTask.getTitle(), result.getTitle());
        assertEquals(updatedTask.getDescription(), result.getDescription());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldMarkTaskAsDeleted() {
        when(taskRepository.findById(testTask.getTaskId())).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        taskService.deleteTask(testTask.getTaskId());

        verify(taskRepository).save(any(Task.class));
    }
} 