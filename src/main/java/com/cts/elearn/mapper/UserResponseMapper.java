package com.cts.elearn.mapper;

import com.cts.elearn.dto.UserResponse;
import com.cts.elearn.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserResponseMapper {
    // MAPPER
    public UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId().intValue(),
                user.getName(),
                user.getContactNumber(),
                user.getEmail()
        );
    }
}
