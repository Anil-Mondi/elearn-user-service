package com.cts.elearn.service;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.cts.elearn.dto.ForgotPasswordRequest;
import com.cts.elearn.dto.LoginRequest;
import com.cts.elearn.dto.LoginResponse;
import com.cts.elearn.dto.UserResponse;
import com.cts.elearn.entity.User;

@Service
@Primary
@Profile("dev")
public class FakeUserService extends UserService {

    public FakeUserService() {
        super(null, null, null, null); // dummy constructor
    }

    @Override
    public User registerUser(User user) {
        user.setId(1L);
        user.setStatus(User.Status.Active);
        return user;
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {
        return new LoginResponse("dummy-token", "ROLE_USER", "ACTIVE");
    }

    @Override
    public UserResponse getUserById(int id) {
        return new UserResponse(id, "Dummy User", "9999999999", "dummy@email.com");
    }

    @Override
    public List<User> getUsers(int page, int size) {
        User user = new User();
        user.setId(1L);
        user.setName("Dummy");
        user.setEmail("dummy@email.com");

        return List.of(user);
    }

    @Override
    public User updateUser(User user) {
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        // no-op
    }

    @Override
    public String resetPassword(ForgotPasswordRequest request) {
        return "Password reset link sent (dummy)";
    }
}
