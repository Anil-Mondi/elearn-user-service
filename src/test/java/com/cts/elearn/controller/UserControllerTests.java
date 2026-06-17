package com.cts.elearn.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.cts.elearn.dto.ForgotPasswordRequest;
import com.cts.elearn.dto.LoginRequest;
import com.cts.elearn.dto.LoginResponse;
import com.cts.elearn.dto.UserResponse;
import com.cts.elearn.entity.User;
import com.cts.elearn.exception.UserNotFoundException;
import com.cts.elearn.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
public class UserControllerTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private UserResponse userResponse;
    private ForgotPasswordRequest forgotPasswordRequest;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setContactNumber("1234567890");
        testUser.setPassword("encoded_password");
        testUser.setRole("LEARNER");
        testUser.setStatus(User.Status.Active);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        loginResponse = new LoginResponse("jwt_token", "ROLE_LEARNER", "Active");

        userResponse = new UserResponse();
        userResponse.setUserId(1);
        userResponse.setName("John Doe");
        userResponse.setEmail("john@example.com");
        userResponse.setContactNumber("1234567890");

        forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail("john@example.com");
    }

    @Test
    @DisplayName("Test endpoint availability - /users/test")
    public void testEndpoint() {
        String result = userController.test();
        assertEquals("User Service is working!", result);
    }

    @Test
    @DisplayName("Register user successfully")
    public void testRegisterUserSuccess() {
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("jane@example.com");
        newUser.setContactNumber("9876543210");
        newUser.setPassword("password123");
        newUser.setRole("LEARNER");

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setName("Jane Doe");
        savedUser.setEmail("jane@example.com");
        savedUser.setContactNumber("9876543210");
        savedUser.setPassword("encoded_password");
        savedUser.setRole("LEARNER");
        savedUser.setStatus(User.Status.Active);

        when(userService.registerUser(any(User.class))).thenReturn(savedUser);

        ResponseEntity<User> response = userController.registerUser(newUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(2L, response.getBody().getId());
        assertEquals("Jane Doe", response.getBody().getName());
        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    @DisplayName("Register user with null status - should default to Active")
    public void testRegisterUserWithNullStatus() {
        User newUser = new User();
        newUser.setName("Test User");
        newUser.setEmail("test@example.com");
        newUser.setContactNumber("5555555555");
        newUser.setPassword("password123");
        newUser.setRole("LEARNER");
        newUser.setStatus(null);

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setStatus(User.Status.Active);

        when(userService.registerUser(any(User.class))).thenReturn(savedUser);

        ResponseEntity<User> response = userController.registerUser(newUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    @DisplayName("Login user successfully")
    public void testLoginUserSuccess() {
        when(userService.loginUser(any(LoginRequest.class))).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = userController.loginUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt_token", response.getBody().getToken());
        assertEquals("ROLE_LEARNER", response.getBody().getRole());
        assertEquals("Active", response.getBody().getStatus());
        verify(userService, times(1)).loginUser(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Login user with invalid credentials")
    public void testLoginUserInvalidCredentials() {
        when(userService.loginUser(any(LoginRequest.class)))
                .thenThrow(new UserNotFoundException("Invalid credentials"));

        assertThrows(UserNotFoundException.class, () -> {
            userController.loginUser(loginRequest);
        });

        verify(userService, times(1)).loginUser(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Get user by id successfully")
    public void testGetUserByIdSuccess() {
        when(userService.getUserById(1)).thenReturn(userResponse);

        UserResponse response = userController.getUserById(1);

        assertNotNull(response);
        assertEquals(1, response.getUserId());
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("1234567890", response.getContactNumber());
        verify(userService, times(1)).getUserById(1);
    }

    @Test
    @DisplayName("Get user by id - user not found")
    public void testGetUserByIdNotFound() {
        when(userService.getUserById(999))
                .thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> {
            userController.getUserById(999);
        });

        verify(userService, times(1)).getUserById(999);
    }

    @Test
    @DisplayName("Update user successfully")
    public void testUpdateUserSuccess() {
        testUser.setName("Updated Name");
        when(userService.updateUser(any(User.class))).thenReturn(testUser);

        ResponseEntity<User> response = userController.updateUser(testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Name", response.getBody().getName());
        assertEquals(1L, response.getBody().getId());
        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    @DisplayName("Delete user successfully")
    public void testDeleteUserSuccess() {
        doNothing().when(userService).deleteUser(1);

        ResponseEntity<Void> response = userController.deleteUser(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1);
    }

    @Test
    @DisplayName("Get all users with default pagination")
    public void testGetUsersWithDefaultPagination() {
        List<User> userList = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(userList);

        when(userService.getUsers(0, 10)).thenReturn(userPage);

        ResponseEntity<List<User>> response = userController.getUsers(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        verify(userService, times(1)).getUsers(0, 10);
    }

    @Test
    @DisplayName("Get all users with custom pagination")
    public void testGetUsersWithCustomPagination() {
        List<User> userList = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(userList);

        when(userService.getUsers(1, 5)).thenReturn(userPage);

        ResponseEntity<List<User>> response = userController.getUsers(1, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).getUsers(1, 5);
    }

    @Test
    @DisplayName("Get empty user list")
    public void testGetEmptyUserList() {
        Page<User> emptyPage = new PageImpl<>(Arrays.asList());

        when(userService.getUsers(0, 10)).thenReturn(emptyPage);

        ResponseEntity<List<User>> response = userController.getUsers(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
        verify(userService, times(1)).getUsers(0, 10);
    }

    @Test
    @DisplayName("Reset password successfully")
    public void testResetPasswordSuccess() {
        when(userService.resetPassword(any(ForgotPasswordRequest.class)))
                .thenReturn("Password reset link sent successfully");

        ResponseEntity<String> response = userController.resetPassword(forgotPasswordRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset link sent successfully", response.getBody());
        verify(userService, times(1)).resetPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    @DisplayName("Reset password for non-existent user")
    public void testResetPasswordUserNotFound() {
        when(userService.resetPassword(any(ForgotPasswordRequest.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> {
            userController.resetPassword(forgotPasswordRequest);
        });

        verify(userService, times(1)).resetPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    @DisplayName("Test multiple users in list")
    public void testGetMultipleUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");

        List<User> userList = Arrays.asList(testUser, user2);
        Page<User> userPage = new PageImpl<>(userList);

        when(userService.getUsers(0, 10)).thenReturn(userPage);

        ResponseEntity<List<User>> response = userController.getUsers(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(2L, response.getBody().get(1).getId());
        verify(userService, times(1)).getUsers(0, 10);
    }
}

