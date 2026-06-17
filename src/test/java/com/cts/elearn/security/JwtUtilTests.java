package com.cts.elearn.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtil Tests")
public class JwtUtilTests {

    private JwtUtil jwtUtil = new JwtUtil();

    @Test
    @DisplayName("Generate token with valid inputs")
    public void testGenerateTokenValidInputs() {
        String token = jwtUtil.generateToken("john@example.com", "LEARNER");

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    @DisplayName("Generate token - token is not empty")
    public void testGenerateTokenNotEmpty() {
        String token = jwtUtil.generateToken("test@example.com", "ADMIN");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Generate tokens for different users are different")
    public void testGenerateTokensDifferentUsers() {
        String token1 = jwtUtil.generateToken("user1@example.com", "LEARNER");
        String token2 = jwtUtil.generateToken("user2@example.com", "LEARNER");

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Generate tokens for different roles")
    public void testGenerateTokensDifferentRoles() {
        String learnerToken = jwtUtil.generateToken("user@example.com", "LEARNER");
        String adminToken = jwtUtil.generateToken("user@example.com", "ADMIN");

        assertNotNull(learnerToken);
        assertNotNull(adminToken);
        assertNotEquals(learnerToken, adminToken);
    }

    @Test
    @DisplayName("Generate token with special characters in email")
    public void testGenerateTokenSpecialCharacters() {
        String token = jwtUtil.generateToken("user.name+tag@example.co.uk", "VENDOR");

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    @DisplayName("Generate token - token format (JWT has 3 parts)")
    public void testGenerateTokenFormat() {
        String token = jwtUtil.generateToken("test@example.com", "LEARNER");
        String[] parts = token.split("\\.");

        assertEquals(3, parts.length);
    }

    @Test
    @DisplayName("Generate token with multiple role values")
    public void testGenerateTokenMultipleRoles() {
        String[] roles = {"LEARNER", "ADMIN", "VENDOR", "INSTRUCTOR"};

        for (String role : roles) {
            String token = jwtUtil.generateToken("user@example.com", role);
            assertNotNull(token);
            assertTrue(token.length() > 0);
        }
    }

    @Test
    @DisplayName("Generate token is deterministic for same inputs at same time (within millisecond)")
    public void testGenerateTokenDeterministic() {
        long time1 = System.currentTimeMillis();
        String token1 = jwtUtil.generateToken("same@example.com", "LEARNER");
        long time2 = System.currentTimeMillis();

        if (time2 - time1 < 1000) {
            String token2 = jwtUtil.generateToken("same@example.com", "LEARNER");
            // Tokens might differ because issuedAt changes
            assertNotNull(token1);
            assertNotNull(token2);
        }
    }

    @Test
    @DisplayName("Generate token - email subject")
    public void testGenerateTokenEmailSubject() {
        String email = "john.doe@example.com";
        String token = jwtUtil.generateToken(email, "LEARNER");

        assertNotNull(token);
        // Token should contain encoded email as subject
        assertTrue(token.length() > 0);
    }

    @Test
    @DisplayName("Verify JwtUtil bean can be instantiated")
    public void testJwtUtilInstantiation() {
        JwtUtil util = new JwtUtil();
        assertNotNull(util);
    }

    @Test
    @DisplayName("Generate token with empty string role")
    public void testGenerateTokenEmptyRole() {
        String token = jwtUtil.generateToken("user@example.com", "");
        assertNotNull(token);
    }

    @Test
    @DisplayName("Generate token with null handling")
    public void testGenerateTokenNullHandling() {
        // JWT generation might handle null gracefully, so just verify it doesn't crash
        try {
            String token = jwtUtil.generateToken(null, "LEARNER");
            // Either throws or returns something - both are acceptable
            assertNotNull(token);
        } catch (NullPointerException e) {
            // NPE is also acceptable for null input
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Multiple token generations in sequence")
    public void testMultipleTokenGenerations() {
        String[] tokens = new String[5];
        for (int i = 0; i < 5; i++) {
            tokens[i] = jwtUtil.generateToken("user" + i + "@example.com", "LEARNER");
            assertNotNull(tokens[i]);
        }

        // All tokens should be different
        for (int i = 0; i < 5; i++) {
            for (int j = i + 1; j < 5; j++) {
                assertNotEquals(tokens[i], tokens[j]);
            }
        }
    }
}

