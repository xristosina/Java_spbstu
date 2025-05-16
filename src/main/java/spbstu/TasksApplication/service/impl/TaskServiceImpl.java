package spbstu.TasksApplication.service.impl;

import org.springframework.stereotype.Service;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.service.TaskService;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class TaskServiceImpl implements TaskService {
    private final Map<Long, Task> tasks = new HashMap<>();
    private final AtomicLong taskIdCounter = new AtomicLong(1);

    @Override
    public List<Task> getAllTasks(Long userId) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId) && !task.getIsDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPendingTasks(Long userId) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId) && !task.getIsCompleted() && !task.getIsDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Task getTaskById(Long taskId) {
        Task task = tasks.get(taskId);
        if (task == null || task.getIsDeleted()) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        validateTask(task);
        task.setTaskId(taskIdCounter.getAndIncrement());
        task.setCreationDate(LocalDateTime.now());
        tasks.put(task.getTaskId(), task);
        return task;
    }

    @Override
    public Task updateTask(Long taskId, Task updatedTask) {
        Task existingTask = getTaskById(taskId);
        validateTask(updatedTask);
        
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setTargetDate(updatedTask.getTargetDate());
        existingTask.setIsCompleted(updatedTask.getIsCompleted());
        
        tasks.put(taskId, existingTask);
        return existingTask;
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = getTaskById(taskId);
        task.setIsDeleted(true);
        tasks.put(taskId, task);
    }

    @Override
    public void completeTask(Long taskId) {
        Task task = getTaskById(taskId);
        task.setIsCompleted(true);
        tasks.put(taskId, task);
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