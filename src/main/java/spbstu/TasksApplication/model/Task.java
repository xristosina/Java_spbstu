package spbstu.TasksApplication.model;

import lombok.*;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private Long taskId;
    
    @NonNull
    private String title;
    
    @NonNull
    private String description;
    
    @NonNull
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    
    @NonNull
    private LocalDateTime targetDate;
    
    @NonNull
    private Long userId;
    
    @NonNull
    @Builder.Default
    private Boolean isCompleted = false;
    
    @NonNull
    @Builder.Default
    private Boolean isDeleted = false;
} 