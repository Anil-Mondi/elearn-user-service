package com.cts.elearn.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
public class UserTests {

    @Test
    @DisplayName("Create user with all fields")
    public void testCreateUserWithAllFields() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setContactNumber("1234567890");
        user.setPassword("password123");
        user.setRole("LEARNER");
        user.setStatus(User.Status.Active);
        user.setResetToken("token123");

        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("1234567890", user.getContactNumber());
        assertEquals("password123", user.getPassword());
        assertEquals("LEARNER", user.getRole());
        assertEquals(User.Status.Active, user.getStatus());
        assertEquals("token123", user.getResetToken());
    }

    @Test
    @DisplayName("User getters and setters")
    public void testUserGettersSetters() {
        User user = new User();

        user.setId(5L);
        assertEquals(5L, user.getId());

        user.setName("Jane Doe");
        assertEquals("Jane Doe", user.getName());

        user.setEmail("jane@example.com");
        assertEquals("jane@example.com", user.getEmail());

        user.setContactNumber("9876543210");
        assertEquals("9876543210", user.getContactNumber());

        user.setPassword("securepass");
        assertEquals("securepass", user.getPassword());

        user.setRole("ADMIN");
        assertEquals("ADMIN", user.getRole());

        user.setStatus(User.Status.Inactive);
        assertEquals(User.Status.Inactive, user.getStatus());

        user.setResetToken("reset_token_123");
        assertEquals("reset_token_123", user.getResetToken());
    }

    @Test
    @DisplayName("User Status enum values")
    public void testUserStatusEnum() {
        assertNotNull(User.Status.Active);
        assertNotNull(User.Status.Inactive);
        assertNotNull(User.Status.Blocked);

        assertEquals(User.Status.Active, User.Status.valueOf("Active"));
        assertEquals(User.Status.Inactive, User.Status.valueOf("Inactive"));
        assertEquals(User.Status.Blocked, User.Status.valueOf("Blocked"));
    }

    @Test
    @DisplayName("User with null values")
    public void testUserWithNullValues() {
        User user = new User();
        user.setId(null);
        user.setName(null);
        user.setEmail(null);
        user.setContactNumber(null);
        user.setPassword(null);
        user.setRole(null);
        user.setStatus(null);
        user.setResetToken(null);

        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getContactNumber());
        assertNull(user.getPassword());
        assertNull(user.getRole());
        assertNull(user.getStatus());
        assertNull(user.getResetToken());
    }

    @Test
    @DisplayName("User with special characters")
    public void testUserWithSpecialCharacters() {
        User user = new User();
        user.setName("John O'Doe-Smith");
        user.setEmail("john.doe+test@example.com");
        user.setContactNumber("+1-234-567-8900");
        user.setPassword("P@ssw0rd!#$%");

        assertEquals("John O'Doe-Smith", user.getName());
        assertEquals("john.doe+test@example.com", user.getEmail());
        assertEquals("+1-234-567-8900", user.getContactNumber());
        assertEquals("P@ssw0rd!#$%", user.getPassword());
    }

    @Test
    @DisplayName("User role variations")
    public void testUserRoleVariations() {
        User user1 = new User();
        user1.setRole("LEARNER");
        assertEquals("LEARNER", user1.getRole());

        User user2 = new User();
        user2.setRole("ADMIN");
        assertEquals("ADMIN", user2.getRole());

        User user3 = new User();
        user3.setRole("VENDOR");
        assertEquals("VENDOR", user3.getRole());
    }

    @Test
    @DisplayName("User status transitions")
    public void testUserStatusTransitions() {
        User user = new User();

        user.setStatus(User.Status.Active);
        assertEquals(User.Status.Active, user.getStatus());

        user.setStatus(User.Status.Inactive);
        assertEquals(User.Status.Inactive, user.getStatus());

        user.setStatus(User.Status.Blocked);
        assertEquals(User.Status.Blocked, user.getStatus());

        user.setStatus(User.Status.Active);
        assertEquals(User.Status.Active, user.getStatus());
    }

    @Test
    @DisplayName("User reset token update")
    public void testUserResetTokenUpdate() {
        User user = new User();
        assertNull(user.getResetToken());

        user.setResetToken("token_v1");
        assertEquals("token_v1", user.getResetToken());

        user.setResetToken("token_v2");
        assertEquals("token_v2", user.getResetToken());

        user.setResetToken(null);
        assertNull(user.getResetToken());
    }

    @Test
    @DisplayName("User long ID handling")
    public void testUserLongIdHandling() {
        User user = new User();

        user.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, user.getId());

        user.setId(1L);
        assertEquals(1L, user.getId());

        user.setId(0L);
        assertEquals(0L, user.getId());
    }
}

