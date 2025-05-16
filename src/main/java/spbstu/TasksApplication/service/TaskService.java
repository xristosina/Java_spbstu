package spbstu.TasksApplication.service;

import spbstu.TasksApplication.model.Task;
import java.util.List;

public interface TaskService {
    List<Task> getAllTasks(Long userId);
    List<Task> getPendingTasks(Long userId);
    Task getTaskById(Long taskId);
    Task createTask(Task task);
    Task updateTask(Long taskId, Task updatedTask);
    void deleteTask(Long taskId);
    void completeTask(Long taskId);
}