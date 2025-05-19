package spbstu.TasksApplication.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Table(name = "tasks")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;
    
    @NonNull
    @Column(nullable = false)
    private String title;
    
    @NonNull
    @Column(nullable = false)
    private String description;
    
    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    
    @NonNull
    @Column(nullable = false)
    private LocalDateTime targetDate;
    
    @NonNull
    @Column(nullable = false)
    private Long userId;
    
    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;
    
    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
} 