package spbstu.TasksApplication.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @NonNull
    @Column(unique = true, nullable = false)
    private String username;
    
    @NonNull
    @Column(nullable = false)
    private String password;
    
    @NonNull
    @Column(unique = true, nullable = false)
    private String email;
} 