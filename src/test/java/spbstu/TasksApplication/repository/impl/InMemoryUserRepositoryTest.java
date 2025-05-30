package spbstu.TasksApplication.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.model.User;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {
    private InMemoryUserRepository repository;
    private User testUser;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
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
} 