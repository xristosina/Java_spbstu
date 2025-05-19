package spbstu.TasksApplication.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
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