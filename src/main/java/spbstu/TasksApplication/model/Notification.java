package spbstu.TasksApplication.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @NonNull
    @Column(nullable = false)
    private String message;

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    @NonNull
    @Column(nullable = false)
    private Long userId;

    @NonNull
    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;
} 