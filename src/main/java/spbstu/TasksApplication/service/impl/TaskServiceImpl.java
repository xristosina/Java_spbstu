package spbstu.TasksApplication.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.config.RabbitMQConfig;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.messaging.TaskCreatedMessage;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.model.TaskStatus;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.TaskRepository;
import spbstu.TasksApplication.repository.UserRepository;
import spbstu.TasksApplication.service.TaskService;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, RabbitTemplate rabbitTemplate) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Cacheable(value = "tasks", key = "#userId.toString()", unless = "#result.isEmpty()")
    public List<Task> getAllTasks(Long userId) {
        return taskRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    @Override
    @Cacheable(value = "tasks", key = "'pending_' + #userId.toString()", unless = "#result.isEmpty()")
    public List<Task> getPendingTasks(Long userId) {
        return taskRepository.findByUserIdAndIsCompletedFalseAndIsDeletedFalse(userId);
    }

    @Override
    @Cacheable(value = "tasks", key = "#taskId.toString()", unless = "#result == null")
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .filter(task -> !task.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public Task createTask(Task task) {
        validateTask(task);
        User user = userRepository.findById(task.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + task.getUserId()));
        task.setCreationDate(LocalDateTime.now());
        Task savedTask = taskRepository.save(task);

        // Publish task created message
        TaskCreatedMessage message = TaskCreatedMessage.builder()
                .taskId(savedTask.getTaskId())
                .title(savedTask.getTitle())
                .userId(savedTask.getUserId())
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.TASK_CREATED_EXCHANGE, 
                RabbitMQConfig.TASK_CREATED_ROUTING_KEY, message);

        return savedTask;
    }

    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public Task updateTask(Long taskId, Task updatedTask) {
        Task existingTask = getTaskById(taskId);
        validateTask(updatedTask);
        
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setTargetDate(updatedTask.getTargetDate());
        existingTask.setIsCompleted(updatedTask.getIsCompleted());
        existingTask.setStatus(updatedTask.getStatus());
        
        return taskRepository.save(existingTask);
    }

    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public void deleteTask(Long taskId) {
        Task task = getTaskById(taskId);
        task.setIsDeleted(true);
        taskRepository.save(task);
    }

    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public void completeTask(Long taskId) {
        Task task = getTaskById(taskId);
        task.setIsCompleted(true);
        taskRepository.save(task);
    }

    @Override
    @Cacheable(value = "tasks", key = "'status_' + #status.name()", unless = "#result.isEmpty()")
    public List<Task> findByStatus(TaskStatus status) {
        return taskRepository.findByStatusAndIsDeletedFalse(status);
    }

    private void validateTask(Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }
        if (task.getTargetDate() == null) {
            throw new IllegalArgumentException("Task target date cannot be null");
        }
        if (task.getUserId() == null) {
            throw new IllegalArgumentException("Task userId cannot be null");
        }
    }
} 