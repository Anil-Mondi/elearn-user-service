package com.cts.elearn.service;

import com.cts.elearn.domain.event.PasswordResetRequestedEvent;
import com.cts.elearn.domain.event.UserRegisteredEvent;
import com.cts.elearn.dto.*;
import com.cts.elearn.entity.Role;
import com.cts.elearn.entity.Status;
import com.cts.elearn.entity.User;
import com.cts.elearn.event.DomainEventPublisher;
import com.cts.elearn.exception.EmailAlreadyExistsException;
import com.cts.elearn.exception.UserNotFoundException;
import com.cts.elearn.mapper.UserResponseMapper;
import com.cts.elearn.repository.UserRepository;
import com.cts.elearn.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserResponseMapper userResponseMapper;

    private final DomainEventPublisher eventPublisher;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // REGISTER
    public UserResponse registerUser(RegisterUserRequest request) {
        log.info("Register request received for email: {}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(
                    request.email() + " already exists");
        }

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setContactNumber(request.contactNumber());
        user.setRole(Role.LEARNER);
        user.setStatus(Status.ACTIVE);

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        User saved = userRepository.save(user);
        log.info("User created successfully with id {}", saved.getId());
        eventPublisher.publish(
                new UserRegisteredEvent(
                        saved.getId(),
                        saved.getEmail()));

        return userResponseMapper.mapToResponse(saved);
    }

    // LOGIN
    public LoginResponse loginUser(LoginRequest request) {
        log.info("Login attempt for {}", request.email());
        User user = getUserByEmailOrThrow(request.email());

        // Match password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Invalid login for {}", request.email());
            throw new UserNotFoundException("User not found");
        }

        // Generate JWT
        String token = jwtUtil.generateToken(
                user.getEmail(),           // subject
                user.getRole().name()            // role (LEARNER / ADMIN)
        );

        return new LoginResponse(token, "ROLE_" + user.getRole(), user.getStatus().name());
    }

    // GET USER
    public UserResponse getUserById(Long id) {
        User user = getUserByIdOrThrow(id);

        return userResponseMapper.mapToResponse(user);
    }

    public UserResponse getUserByEmail(String email) {

        User user = getUserByEmailOrThrow(email);

        return userResponseMapper.mapToResponse(user);

    }

    public Page<UserResponse> getUsers(int page, int size) {

        return userRepository.findAll(PageRequest.of(page, size)).map(userResponseMapper::mapToResponse);
    }

    // UPDATE
    public UserResponse updateUser(User user) {

        return userResponseMapper.mapToResponse(userRepository.save(user));
    }

    // DELETE
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // RESET PASSWORD
    public String resetPassword(ForgotPasswordRequest request) {

        User user = getUserByEmailOrThrow(request.email());

        String token = generateResetToken();
        user.setResetToken(token);
        userRepository.save(user);

        eventPublisher.publish(
                new PasswordResetRequestedEvent(user.getId(), user.getEmail(), token)
        );

        return "Password reset link sent successfully";
    }


    public List<UserResponse> getActiveUsers() {
        return userRepository.findByStatus(Status.ACTIVE)
                .stream()
                .map(userResponseMapper::mapToResponse)
                .toList();
    }

    public List<UserResponse> getBlockedUsers(){
        return userRepository.findByStatus(Status.BLOCKED)
                .stream()
                .map(userResponseMapper::mapToResponse)
                .toList();
    }

    public List<UserResponse> getLearners(){

        return userRepository.findByRole(Role.LEARNER)
                .stream()
                .map(userResponseMapper::mapToResponse)
                .toList();
    }


    //Helper methods
    private User getUserByEmailOrThrow(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    private User getUserByIdOrThrow(Long id){

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found : " + id));
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }
}