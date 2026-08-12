package com.cts.elearn.controller;

import com.cts.elearn.common.response.ApiResponse;
import com.cts.elearn.dto.*;
import com.cts.elearn.entity.User;
import com.cts.elearn.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
@Tag(name = "User Management", description = "APIs for user operations")
public class UserController {

    private final UserService userService;
    
    @GetMapping("/test")
    public String test() {
        return "User Service is working!";
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> registerUser(
            @Valid
            @RequestBody RegisterUserRequest request) {

        UserResponse response = userService.registerUser(request);

        return new ApiResponse<>(
                true,
                "User registered successfully",
                response,
                LocalDateTime.now());
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> loginUser(
            @Valid
            @RequestBody LoginRequest loginRequest) {
        return new ApiResponse<>(
                true,
                "User logged in successfully",
                userService.loginUser(loginRequest),
                LocalDateTime.now());
    }
    
    @GetMapping("/{id}")   // This should match Feign Client path
    public ApiResponse<UserResponse> getUserById(
            @Positive
            @PathVariable Long id) {

        return new ApiResponse<>(
                true,
                "User found",
                userService.getUserById(id),
                LocalDateTime.now());
    }

    @GetMapping("/email/{email}")
    public ApiResponse<UserResponse> getUserByEmail(
            @PathVariable String email) {

        return new ApiResponse<>(
                true,
                "User found",
                userService.getUserByEmail(email),
                LocalDateTime.now());

    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'LEARNER')")
    public ApiResponse<UserResponse> updateUser(@RequestBody User user) {
        return new ApiResponse<>(
                true,
                "User updated successfully",
                userService.updateUser(user),
                LocalDateTime.now());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ApiResponse<>(
                true,
                "User deleted successfully",
                null,
                LocalDateTime.now());
    }

    @GetMapping("/getUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> getUsers(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return new ApiResponse<>(
                true,
                "Users retrieved successfully",
                userService.getUsers(page, size).getContent(),
                LocalDateTime.now());
    }
    
    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(
            @Valid
            @RequestBody ForgotPasswordRequest request) {
        return new ApiResponse<>(
                true,
                "Password reset successfully",
                userService.resetPassword(request),
                LocalDateTime.now());
    }

    @GetMapping("/active")
    public ApiResponse<List<UserResponse>> getActiveUsers() {
        return new ApiResponse<>(
                true,
                "Active users retrieved successfully",
                userService.getActiveUsers(),
                LocalDateTime.now());
    }

    @GetMapping("/blocked")
    public ApiResponse<List<UserResponse>> getBlockedUsers(){
        return new ApiResponse<>(
                true,
                "Blocked users retrieved successfully",
                userService.getBlockedUsers(),
                LocalDateTime.now());
    }

    @GetMapping("/learners")
    public ApiResponse<List<UserResponse>> getLearners(){
        return new ApiResponse<>(
                true,
                "Learners retrieved successfully",
                userService.getLearners(),
                LocalDateTime.now());
    }
}
