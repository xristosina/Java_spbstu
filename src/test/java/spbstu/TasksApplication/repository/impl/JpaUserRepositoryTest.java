package spbstu.TasksApplication.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("h2")
class JpaUserRepositoryTest {
    @Autowired
    private UserRepository repository;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
    }

    @Test
    void save_ShouldCreateNewUser() {
        User savedUser = repository.save(testUser);
        
        assertNotNull(savedUser.getUserId());
        assertEquals(testUser.getUsername(), savedUser.getUsername());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        User savedUser = repository.save(testUser);
        Optional<User> foundUser = repository.findById(savedUser.getUserId());
        
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getUserId(), foundUser.get().getUserId());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<User> foundUser = repository.findById(999L);
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        repository.save(testUser);
        Optional<User> foundUser = repository.findByUsername("testuser");
        
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotExists() {
        Optional<User> foundUser = repository.findByUsername("nonexistent");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        repository.save(testUser);
        Optional<User> foundUser = repository.findByEmail("test@example.com");
        
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenNotExists() {
        Optional<User> foundUser = repository.findByEmail("nonexistent@example.com");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        repository.save(testUser);
        assertTrue(repository.existsByUsername("testuser"));
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenUsernameNotExists() {
        assertFalse(repository.existsByUsername("nonexistent"));
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        repository.save(testUser);
        assertTrue(repository.existsByEmail("test@example.com"));
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailNotExists() {
        assertFalse(repository.existsByEmail("nonexistent@example.com"));
    }
} 