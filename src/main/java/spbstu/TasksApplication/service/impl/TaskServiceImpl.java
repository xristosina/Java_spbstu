package spbstu.TasksApplication.service.impl;

import org.springframework.stereotype.Service;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Task;
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

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Task> getAllTasks(Long userId) {
        return taskRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    @Override
    public List<Task> getPendingTasks(Long userId) {
        return taskRepository.findByUserIdAndIsCompletedFalseAndIsDeletedFalse(userId);
    }

    @Override
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .filter(task -> !task.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    @Override
    public Task createTask(Task task) {
        validateTask(task);
        User user = userRepository.findById(task.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + task.getUserId()));
        task.setCreationDate(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long taskId, Task updatedTask) {
        Task existingTask = getTaskById(taskId);
        validateTask(updatedTask);
        
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setTargetDate(updatedTask.getTargetDate());
        existingTask.setIsCompleted(updatedTask.getIsCompleted());
        
        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = getTaskById(taskId);
        task.setIsDeleted(true);
        taskRepository.save(task);
    }

    @Override
    public void completeTask(Long taskId) {
        Task task = getTaskById(taskId);
        task.setIsCompleted(true);
        taskRepository.save(task);
    }

    private void validateTask(Task task) {
        if (task.getUserId() == null) {
            throw new IllegalArgumentException("Task userId cannot be null");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }
        if (task.getTargetDate() == null) {
            throw new IllegalArgumentException("Task target date cannot be null");
        }
        if (task.getTargetDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Task target date cannot be in the past");
        }
    }
} 