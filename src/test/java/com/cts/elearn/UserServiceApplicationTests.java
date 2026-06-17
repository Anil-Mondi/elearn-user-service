package com.cts.elearn;

import static org.junit.jupiter.api.Assertions.*;

import com.cts.elearn.entity.User;
import com.cts.elearn.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest(classes = UserServiceApplication.class)
@DisplayName("User Service Application Integration Tests")
class UserServiceApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        assertNotNull(userRepository);
    }

    @Test
    @DisplayName("Save and find user by email")
    public void testSaveAndFindUserByEmail() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setContactNumber("1234567890");
        user.setPassword("password123");
        user.setRole("LEARNER");
        user.setStatus(User.Status.Active);

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("Test User", foundUser.get().getName());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Find non-existent user by email")
    public void testFindNonExistentUserByEmail() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("Save multiple users")
    public void testSaveMultipleUsers() {
        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@example.com");
        user1.setRole("LEARNER");
        user1.setStatus(User.Status.Active);

        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@example.com");
        user2.setRole("ADMIN");
        user2.setStatus(User.Status.Active);

        userRepository.save(user1);
        userRepository.save(user2);

        long count = userRepository.count();
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Update user")
    public void testUpdateUser() {
        User user = new User();
        user.setName("Original Name");
        user.setEmail("update@example.com");
        user.setRole("LEARNER");
        user.setStatus(User.Status.Active);

        User savedUser = userRepository.save(user);
        savedUser.setName("Updated Name");
        userRepository.save(savedUser);

        Optional<User> updatedUser = userRepository.findById(savedUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals("Updated Name", updatedUser.get().getName());
    }

    @Test
    @DisplayName("Delete user by id")
    public void testDeleteUser() {
        User user = new User();
        user.setName("To Delete");
        user.setEmail("delete@example.com");
        user.setRole("LEARNER");
        user.setStatus(User.Status.Active);

        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        userRepository.deleteById(userId);

        Optional<User> deletedUser = userRepository.findById(userId);
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    @DisplayName("User status enum test")
    public void testUserStatusEnum() {
        User user = new User();
        user.setStatus(User.Status.Active);
        assertEquals(User.Status.Active, user.getStatus());

        user.setStatus(User.Status.Inactive);
        assertEquals(User.Status.Inactive, user.getStatus());

        user.setStatus(User.Status.Blocked);
        assertEquals(User.Status.Blocked, user.getStatus());
    }

    @Test
    @DisplayName("User with reset token")
    public void testUserWithResetToken() {
        User user = new User();
        user.setName("Reset Token User");
        user.setEmail("resettoken@example.com");
        user.setResetToken("some_random_token_123");
        user.setRole("LEARNER");
        user.setStatus(User.Status.Active);

        User savedUser = userRepository.save(user);

        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals("some_random_token_123", retrievedUser.get().getResetToken());
    }

    @Test
    @DisplayName("Find user after deletion")
    public void testFindUserAfterDeletion() {
        User user = new User();
        user.setName("Temporary User");
        user.setEmail("temporary@example.com");
        user.setRole("LEARNER");
        user.setStatus(User.Status.Active);

        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        assertTrue(userRepository.findById(userId).isPresent());

        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent());
    }

    @Test
    @DisplayName("Verify all users deleted")
    public void testAllUsersDeleted() {
        userRepository.deleteAll();
        long count = userRepository.count();
        assertEquals(0, count);
    }
}
