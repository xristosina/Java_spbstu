package spbstu.TasksApplication.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.config.RabbitMQConfig;
import spbstu.TasksApplication.model.Task;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskMessagePublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishTaskCreated(Task task) {
        TaskCreatedMessage message = TaskCreatedMessage.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .userId(task.getUserId())
                .creationDate(task.getCreationDate())
                .targetDate(task.getTargetDate())
                .build();

        log.info("Publishing task created message: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TASK_CREATED_EXCHANGE,
                RabbitMQConfig.TASK_CREATED_ROUTING_KEY,
                message
        );
    }
} 