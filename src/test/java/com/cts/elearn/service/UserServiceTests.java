package com.cts.elearn.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.cts.elearn.domain.event.PasswordResetRequestedEvent;
import com.cts.elearn.domain.event.UserRegisteredEvent;
import com.cts.elearn.dto.ForgotPasswordRequest;
import com.cts.elearn.dto.LoginRequest;
import com.cts.elearn.dto.LoginResponse;
import com.cts.elearn.dto.UserResponse;
import com.cts.elearn.entity.User;
import com.cts.elearn.event.DomainEventPublisher;
import com.cts.elearn.exception.UserNotFoundException;
import com.cts.elearn.repository.UserRepository;
import com.cts.elearn.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private LoginRequest loginRequest;
    private ForgotPasswordRequest forgotPasswordRequest;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setContactNumber("1234567890");
        testUser.setPassword("raw_password");
        testUser.setRole("LEARNER");
        testUser.setStatus(User.Status.Active);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail("john@example.com");
    }

    @Test
    @DisplayName("Register user successfully")
    public void testRegisterUserSuccess() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(eventPublisher).publish(any(UserRegisteredEvent.class));

        User result = userService.registerUser(testUser);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publish(any(UserRegisteredEvent.class));
    }

    @Test
    @DisplayName("Register user - password encoded correctly")
    public void testRegisterUserPasswordEncoded() {
        when(passwordEncoder.encode("raw_password")).thenReturn("encoded_password_123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(eventPublisher).publish(any(UserRegisteredEvent.class));

        userService.registerUser(testUser);

        verify(passwordEncoder, times(1)).encode("raw_password");
    }

    @Test
    @DisplayName("Register user - event published")
    public void testRegisterUserEventPublished() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(eventPublisher).publish(any(UserRegisteredEvent.class));

        userService.registerUser(testUser);

        verify(eventPublisher, times(1)).publish(any(UserRegisteredEvent.class));
    }

    @Test
    @DisplayName("Login user successfully")
    public void testLoginUserSuccess() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("john@example.com", "LEARNER")).thenReturn("jwt_token");

        LoginResponse result = userService.loginUser(loginRequest);

        assertNotNull(result);
        assertEquals("jwt_token", result.getToken());
        assertEquals("ROLE_LEARNER", result.getRole());
        assertEquals("Active", result.getStatus());
        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtUtil, times(1)).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Login user - invalid email")
    public void testLoginUserInvalidEmail() {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("nonexistent@example.com");
        invalidRequest.setPassword("password");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.loginUser(invalidRequest);
        });

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Login user - invalid password")
    public void testLoginUserInvalidPassword() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong_password", testUser.getPassword())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            LoginRequest wrongPasswordRequest = new LoginRequest();
            wrongPasswordRequest.setEmail("john@example.com");
            wrongPasswordRequest.setPassword("wrong_password");
            userService.loginUser(wrongPasswordRequest);
        });

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Get user by id successfully")
    public void testGetUserByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("1234567890", result.getContactNumber());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get user by id - user not found")
    public void testGetUserByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(999);
        });

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Get users with pagination - first page")
    public void testGetUsersFirstPage() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");

        List<User> userList = Arrays.asList(testUser, user2);
        Page<User> page = new PageImpl<>(userList, PageRequest.of(0, 10), 2);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<User> result = userService.getUsers(0, 10);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getNumber());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Get users with pagination - second page")
    public void testGetUsersSecondPage() {
        User user3 = new User();
        user3.setId(3L);
        user3.setName("Bob Smith");

        List<User> userList = Arrays.asList(user3);
        Page<User> page = new PageImpl<>(userList, PageRequest.of(1, 10), 3);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<User> result = userService.getUsers(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getNumber());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Get users - empty result")
    public void testGetUsersEmpty() {
        Page<User> emptyPage = new PageImpl<>(Arrays.asList());

        when(userRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<User> result = userService.getUsers(0, 10);

        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Update user successfully")
    public void testUpdateUserSuccess() {
        testUser.setName("Updated Name");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(testUser);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Update user - multiple fields")
    public void testUpdateUserMultipleFields() {
        testUser.setName("New Name");
        testUser.setContactNumber("9999999999");
        testUser.setRole("ADMIN");

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(testUser);

        assertEquals("New Name", result.getName());
        assertEquals("9999999999", result.getContactNumber());
        assertEquals("ADMIN", result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Delete user successfully")
    public void testDeleteUserSuccess() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Delete user - multiple deletions")
    public void testDeleteMultipleUsers() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteUser(1);
        userService.deleteUser(2);
        userService.deleteUser(3);

        verify(userRepository, times(3)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Reset password successfully")
    public void testResetPasswordSuccess() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(eventPublisher).publish(any(PasswordResetRequestedEvent.class));

        String result = userService.resetPassword(forgotPasswordRequest);

        assertNotNull(result);
        assertEquals("Password reset link sent successfully", result);
        assertNotNull(testUser.getResetToken());
        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publish(any(PasswordResetRequestedEvent.class));
    }

    @Test
    @DisplayName("Reset password - user not found")
    public void testResetPasswordUserNotFound() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.resetPassword(request);
        });

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Reset password - token generated")
    public void testResetPasswordTokenGenerated() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(eventPublisher).publish(any(PasswordResetRequestedEvent.class));

        userService.resetPassword(forgotPasswordRequest);

        assertNotNull(testUser.getResetToken());
        assertFalse(testUser.getResetToken().isEmpty());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Reset password - event published with correct details")
    public void testResetPasswordEventPublished() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(eventPublisher).publish(any(PasswordResetRequestedEvent.class));

        userService.resetPassword(forgotPasswordRequest);

        verify(eventPublisher, times(1)).publish(any(PasswordResetRequestedEvent.class));
    }

    @Test
    @DisplayName("Get user by id - mapping test")
    public void testGetUserByIdMapping() {
        testUser.setContactNumber("5555555555");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse result = userService.getUserById(1);

        assertEquals(1, result.getUserId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("5555555555", result.getContactNumber());
    }

    @Test
    @DisplayName("Get users with different page sizes")
    public void testGetUsersDifferentPageSizes() {
        User user = new User();
        user.setId(1L);
        List<User> userList = Arrays.asList(user);
        Page<User> page = new PageImpl<>(userList, PageRequest.of(0, 5), 1);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<User> result = userService.getUsers(0, 5);

        assertEquals(5, result.getPageable().getPageSize());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Login response contains all required fields")
    public void testLoginResponseFields() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("john@example.com", "LEARNER")).thenReturn("jwt_token");

        LoginResponse result = userService.loginUser(loginRequest);

        assertNotNull(result.getToken());
        assertNotNull(result.getRole());
        assertNotNull(result.getStatus());
        assertTrue(result.getToken().length() > 0);
        assertTrue(result.getRole().contains("LEARNER"));
        assertTrue(result.getStatus().equals("Active"));
    }

    @Test
    @DisplayName("Register user with null password")
    public void testRegisterUserNullPassword() {
        testUser.setPassword(null);
        when(passwordEncoder.encode(null)).thenReturn("encoded_null");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(eventPublisher).publish(any(UserRegisteredEvent.class));

        User result = userService.registerUser(testUser);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Update user preserves ID")
    public void testUpdateUserPreservesId() {
        User userToUpdate = new User();
        userToUpdate.setId(5L);
        userToUpdate.setName("Updated User");

        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        User result = userService.updateUser(userToUpdate);

        assertEquals(5L, result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }
}

