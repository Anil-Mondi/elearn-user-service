package com.cts.elearn.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.elearn.dto.ForgotPasswordRequest;
import com.cts.elearn.dto.LoginRequest;
import com.cts.elearn.dto.LoginResponse;
import com.cts.elearn.dto.UserResponse;
import com.cts.elearn.entity.User;
import com.cts.elearn.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/users/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Service is working!"));
    }

    @Test
    void testRegisterUser_setsStatusIfNull_andReturnsCreated() throws Exception {
        // send JSON without status
        String json = "{\"name\": \"John Doe\", \"email\": \"john@example.com\"}";

        // prepare returned user from service with an id and status
        User returned = new User();
        returned.setId(1);
        returned.setName("John Doe");
        returned.setEmail("john@example.com");
        returned.setStatus(User.Status.Active);

        when(userService.registerUser(any(User.class))).thenReturn(returned);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(returned)));

        // verify controller set status before calling service
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).registerUser(captor.capture());
        User passed = captor.getValue();
        // status must be set to ACTIVE by controller when input doesn't contain it
        assert passed.getStatus() == User.Status.Active;
    }

    @Test
    void testLoginUser_returnsLoginResponse() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("john@example.com");
        req.setPassword("secret");

        LoginResponse resp = new LoginResponse();
        resp.setToken("jwt-token");
        resp.setMessage("Login successful");

        when(userService.loginUser(any(LoginRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));
    }

    @Test
    void testGetUserById_returnsUserResponse() throws Exception {
        UserResponse ur = new UserResponse();
        ur.setId(1);
        ur.setName("Jane");
        ur.setEmail("jane@example.com");

        when(userService.getUserById(1)).thenReturn(ur);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ur)));
    }

    @Test
    void testUpdateUser_returnsOk() throws Exception {
        User toUpdate = new User();
        toUpdate.setId(2);
        toUpdate.setName("Updated");
        toUpdate.setEmail("up@example.com");

        when(userService.updateUser(any(User.class))).thenReturn(toUpdate);

        mockMvc.perform(put("/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(toUpdate)));
    }

    @Test
    void testDeleteUser_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/users/delete/5"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(5);
    }

    @Test
    void testGetUsers_returnsList() throws Exception {
        User u = new User();
        u.setId(3);
        u.setName("ListUser");
        u.setEmail("list@example.com");
        List<User> users = Collections.singletonList(u);

        when(userService.getUsers(0, 10)).thenReturn(users);

        mockMvc.perform(get("/users/getUsers").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    void testResetPassword_returnsMessage() throws Exception {
        ForgotPasswordRequest fpr = new ForgotPasswordRequest();
        fpr.setEmail("someone@example.com");

        when(userService.resetPassword(any(ForgotPasswordRequest.class))).thenReturn("Reset link sent");

        mockMvc.perform(post("/users/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fpr)))
                .andExpect(status().isOk())
                .andExpect(content().string("Reset link sent"));
    }
}
