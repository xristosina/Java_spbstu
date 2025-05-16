package spbstu.TasksApplication.model;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long userId;
    
    @NonNull
    private String username;
    
    @NonNull
    private String password;
    
    @NonNull
    private String email;
} 