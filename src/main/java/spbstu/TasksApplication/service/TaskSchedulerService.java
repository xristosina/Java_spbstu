package spbstu.TasksApplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.repository.TaskRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskSchedulerService {

    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 100000) // Run every 100 seconds
    public void checkOverdueTasks() {
        log.info("Checking for overdue tasks...");
        List<Task> overdueTasks = findOverdueTasks();

        for (Task task : overdueTasks) {
            processOverdueTask(task);
        }
    }

    @Async
    public void processOverdueTask(Task task) {
        log.info("Processing overdue task: {}", task.getTaskId());

        // Update task status
        task.setIsCompleted(true);
        taskService.updateTask(task.getTaskId(), task);

        // Send notification
        notificationService.createNotificationFromMessage(
                "Task '" + task.getTitle() + "' is overdue!",
                task.getUserId()
        );
    }

    public List<Task> findOverdueTasks() {
        return taskRepository.findByIsCompleted(false);
    }
} 