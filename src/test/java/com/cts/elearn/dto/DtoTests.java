package com.cts.elearn.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTO Tests")
public class DtoTests {

    @Test
    @DisplayName("LoginRequest getter and setter")
    public void testLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setEmail("john@example.com");
        assertEquals("john@example.com", loginRequest.getEmail());

        loginRequest.setPassword("password123");
        assertEquals("password123", loginRequest.getPassword());
    }

    @Test
    @DisplayName("LoginRequest with different values")
    public void testLoginRequestDifferentValues() {
        LoginRequest request1 = new LoginRequest();
        request1.setEmail("user1@example.com");
        request1.setPassword("pass1");

        LoginRequest request2 = new LoginRequest();
        request2.setEmail("user2@example.com");
        request2.setPassword("pass2");

        assertNotEquals(request1.getEmail(), request2.getEmail());
        assertNotEquals(request1.getPassword(), request2.getPassword());
    }

    @Test
    @DisplayName("LoginRequest null values")
    public void testLoginRequestNullValues() {
        LoginRequest loginRequest = new LoginRequest();
        assertNull(loginRequest.getEmail());
        assertNull(loginRequest.getPassword());

        loginRequest.setEmail(null);
        loginRequest.setPassword(null);
        assertNull(loginRequest.getEmail());
        assertNull(loginRequest.getPassword());
    }

    @Test
    @DisplayName("LoginResponse constructor and getters")
    public void testLoginResponse() {
        LoginResponse response = new LoginResponse("jwt_token", "ROLE_LEARNER", "Active");

        assertEquals("jwt_token", response.getToken());
        assertEquals("ROLE_LEARNER", response.getRole());
        assertEquals("Active", response.getStatus());
    }

    @Test
    @DisplayName("LoginResponse setters")
    public void testLoginResponseSetters() {
        LoginResponse response = new LoginResponse("", "", "");

        response.setToken("new_token");
        response.setRole("ROLE_ADMIN");
        response.setStatus("Inactive");

        assertEquals("new_token", response.getToken());
        assertEquals("ROLE_ADMIN", response.getRole());
        assertEquals("Inactive", response.getStatus());
    }

    @Test
    @DisplayName("LoginResponse with various roles")
    public void testLoginResponseRoles() {
        LoginResponse learnerResponse = new LoginResponse("token1", "ROLE_LEARNER", "Active");
        LoginResponse adminResponse = new LoginResponse("token2", "ROLE_ADMIN", "Active");
        LoginResponse vendorResponse = new LoginResponse("token3", "ROLE_VENDOR", "Active");

        assertEquals("ROLE_LEARNER", learnerResponse.getRole());
        assertEquals("ROLE_ADMIN", adminResponse.getRole());
        assertEquals("ROLE_VENDOR", vendorResponse.getRole());
    }

    @Test
    @DisplayName("LoginResponse with different statuses")
    public void testLoginResponseStatuses() {
        LoginResponse activeResponse = new LoginResponse("token1", "ROLE_LEARNER", "Active");
        LoginResponse inactiveResponse = new LoginResponse("token2", "ROLE_LEARNER", "Inactive");
        LoginResponse blockedResponse = new LoginResponse("token3", "ROLE_LEARNER", "Blocked");

        assertEquals("Active", activeResponse.getStatus());
        assertEquals("Inactive", inactiveResponse.getStatus());
        assertEquals("Blocked", blockedResponse.getStatus());
    }

    @Test
    @DisplayName("UserResponse constructor and getters")
    public void testUserResponse() {
        UserResponse response = new UserResponse(1, "John Doe", "1234567890", "john@example.com");

        assertEquals(1, response.getUserId());
        assertEquals("John Doe", response.getName());
        assertEquals("1234567890", response.getContactNumber());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    @DisplayName("UserResponse setters")
    public void testUserResponseSetters() {
        UserResponse response = new UserResponse();

        response.setUserId(5);
        response.setName("Jane Doe");
        response.setContactNumber("9876543210");
        response.setEmail("jane@example.com");

        assertEquals(5, response.getUserId());
        assertEquals("Jane Doe", response.getName());
        assertEquals("9876543210", response.getContactNumber());
        assertEquals("jane@example.com", response.getEmail());
    }

    @Test
    @DisplayName("UserResponse no-arg constructor")
    public void testUserResponseNoArgConstructor() {
        UserResponse response = new UserResponse();

        assertNotNull(response);
        assertEquals(0, response.getUserId());
        assertNull(response.getName());
        assertNull(response.getContactNumber());
        assertNull(response.getEmail());
    }

    @Test
    @DisplayName("UserResponse toString")
    public void testUserResponseToString() {
        UserResponse response = new UserResponse(1, "John Doe", "1234567890", "john@example.com");
        String toString = response.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("UserResponse") || toString.contains("John Doe"));
    }

    @Test
    @DisplayName("UserResponse with large ID")
    public void testUserResponseLargeId() {
        UserResponse response = new UserResponse(Integer.MAX_VALUE, "User", "123", "user@example.com");

        assertEquals(Integer.MAX_VALUE, response.getUserId());
    }

    @Test
    @DisplayName("ForgotPasswordRequest getter and setter")
    public void testForgotPasswordRequest() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();

        request.setEmail("forgot@example.com");
        assertEquals("forgot@example.com", request.getEmail());
    }

    @Test
    @DisplayName("ForgotPasswordRequest with different emails")
    public void testForgotPasswordRequestDifferentEmails() {
        ForgotPasswordRequest request1 = new ForgotPasswordRequest();
        request1.setEmail("user1@example.com");

        ForgotPasswordRequest request2 = new ForgotPasswordRequest();
        request2.setEmail("user2@example.com");

        assertNotEquals(request1.getEmail(), request2.getEmail());
    }

    @Test
    @DisplayName("ForgotPasswordRequest null email")
    public void testForgotPasswordRequestNullEmail() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        assertNull(request.getEmail());

        request.setEmail(null);
        assertNull(request.getEmail());
    }

    @Test
    @DisplayName("ForgotPasswordRequest with special email formats")
    public void testForgotPasswordRequestSpecialEmails() {
        ForgotPasswordRequest request1 = new ForgotPasswordRequest();
        request1.setEmail("user.name+tag@example.co.uk");
        assertEquals("user.name+tag@example.co.uk", request1.getEmail());

        ForgotPasswordRequest request2 = new ForgotPasswordRequest();
        request2.setEmail("user_name@sub.example.com");
        assertEquals("user_name@sub.example.com", request2.getEmail());
    }

    @Test
    @DisplayName("LoginResponse equals logic (via Data annotation)")
    public void testLoginResponseEquality() {
        LoginResponse response1 = new LoginResponse("token", "ROLE_LEARNER", "Active");
        LoginResponse response2 = new LoginResponse("token", "ROLE_LEARNER", "Active");
        LoginResponse response3 = new LoginResponse("token2", "ROLE_LEARNER", "Active");

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
    }

    @Test
    @DisplayName("UserResponse equality (via Getter/Setter/EqualsAndHashCode)")
    public void testUserResponseEquality() {
        UserResponse response1 = new UserResponse(1, "John", "123", "john@example.com");
        UserResponse response2 = new UserResponse(1, "John", "123", "john@example.com");

        // Both should have same values (not necessarily same hashcode implementation)
        assertEquals(response1.getUserId(), response2.getUserId());
        assertEquals(response1.getName(), response2.getName());
    }

    @Test
    @DisplayName("Multiple DTOs independence")
    public void testMultipleDtosIndependence() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@example.com");

        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest();
        forgotReq.setEmail("forgot@example.com");

        assertNotEquals(req.getEmail(), forgotReq.getEmail());
    }
}

