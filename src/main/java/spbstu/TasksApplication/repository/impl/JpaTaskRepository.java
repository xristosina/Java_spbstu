package spbstu.TasksApplication.repository.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.repository.TaskRepository;

import java.util.List;

@Repository
@Profile({"h2", "postgres"})
public interface JpaTaskRepository extends JpaRepository<Task, Long>, TaskRepository {
    List<Task> findByUserIdAndIsDeletedFalse(Long userId);
    List<Task> findByUserIdAndIsCompletedFalseAndIsDeletedFalse(Long userId);
    
//    @Query("SELECT t FROM Task t WHERE t.userId = ?1 AND t.isDeleted = false AND t.targetDate <= ?2")
//    List<Task> findOverdueTasks(Long userId, LocalDateTime now);
//
//    @Query("SELECT t FROM Task t WHERE t.userId = ?1 AND t.isDeleted = false AND t.isCompleted = false AND t.targetDate <= ?2")
//    List<Task> findActiveOverdueTasks(Long userId, LocalDateTime now);
//
//    @Query("SELECT t FROM Task t WHERE t.userId = ?1 AND t.isDeleted = false AND t.isCompleted = true")
//    List<Task> findCompletedTasks(Long userId);
} 