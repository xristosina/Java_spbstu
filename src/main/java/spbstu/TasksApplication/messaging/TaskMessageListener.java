package spbstu.TasksApplication.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import spbstu.TasksApplication.config.RabbitMQConfig;
import spbstu.TasksApplication.messaging.TaskCreatedMessage;
import spbstu.TasksApplication.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskMessageListener {
    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.TASK_CREATED_QUEUE)
    public void handleTaskCreated(TaskCreatedMessage message) {
        log.info("Received task created message: {}", message);

        // Create notification for the task creator
        notificationService.createNotificationFromMessage(
            "New task created: " + message.getTitle(),
            message.getUserId()
        );
        log.info("Created notification for task: {}", message.getTaskId());
    }
} 