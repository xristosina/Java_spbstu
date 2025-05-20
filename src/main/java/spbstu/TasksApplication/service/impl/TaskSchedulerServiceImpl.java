package spbstu.TasksApplication.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.model.TaskStatus;
import spbstu.TasksApplication.service.NotificationService;
import spbstu.TasksApplication.service.TaskSchedulerService;
import spbstu.TasksApplication.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskSchedulerServiceImpl implements TaskSchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(TaskSchedulerServiceImpl.class);

    private final TaskService taskService;
    private final NotificationService notificationService;

    @Autowired
    public TaskSchedulerServiceImpl(TaskService taskService, NotificationService notificationService) {
        this.taskService = taskService;
        this.notificationService = notificationService;
    }

    @Override
    @Scheduled(fixedRate = 100000) // Run every 100 seconds
    public void checkOverdueTasks() {
        logger.info("Checking for overdue tasks...");
        List<Task> overdueTasks = findOverdueTasks();
        
        for (Task task : overdueTasks) {
            processOverdueTask(task);
        }
    }

    @Override
    @Async
    public void processOverdueTask(Task task) {
        logger.info("Processing overdue task: {}", task.getTaskId());
        
        // Update task status
        task.setStatus(TaskStatus.OVERDUE);
        taskService.updateTask(task.getTaskId(), task);
        
        // Send notification
        notificationService.createNotificationFromMessage(
            "Task '" + task.getTitle() + "' is overdue!",
            task.getUserId()
        );
    }

    @Override
    public List<Task> findOverdueTasks() {
        return taskService.findByStatus(TaskStatus.TODO).stream()
            .filter(task -> !task.getIsCompleted() && !task.getIsDeleted())
            .filter(task -> task.getTargetDate().isBefore(LocalDateTime.now()))
            .toList();
    }
} 