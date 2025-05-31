package spbstu.TasksApplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Cacheable(value = "tasks", key = "#userId.toString()", unless = "#result.isEmpty()")
    public List<Task> getAllTasks(Long userId) {
        return taskRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    @Cacheable(value = "tasks", key = "'pending_' + #userId.toString()", unless = "#result.isEmpty()")
    public List<Task> getPendingTasks(Long userId) {
        return taskRepository.findByUserIdAndIsCompletedFalseAndIsDeletedFalse(userId);
    }

    @Cacheable(value = "tasks", key = "#taskId.toString()", unless = "#result == null")
    public Task getTaskById(Long taskId) {
        return taskRepository.findByTaskIdAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task createTask(Task task) {
        validateTask(task);
        task.setCreationDate(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findByTaskIdAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        task.setIsDeleted(true);
        taskRepository.save(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void completeTask(Long taskId) {
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
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

    @CacheEvict(value = "tasks", allEntries = true)
    public Task updateTask(Long taskId, Task updatedTask) {
        updatedTask.setTaskId(taskId);
        return taskRepository.save(updatedTask);
    }
}