package spbstu.TasksApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.model.TaskStatus;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdAndIsDeletedFalse(Long userId);
    List<Task> findByUserIdAndIsCompletedFalseAndIsDeletedFalse(Long userId);
    List<Task> findByStatusAndIsDeletedFalse(TaskStatus status);
} 