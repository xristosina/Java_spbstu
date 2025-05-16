package spbstu.TasksApplication.model;

import lombok.*;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private Long notificationId;

    @NonNull
    private String text;

    @NonNull
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    @NonNull
    private Long taskId;

    @NonNull
    private Long userId;

    @NonNull
    @Builder.Default
    private Boolean isRead = false;
} 