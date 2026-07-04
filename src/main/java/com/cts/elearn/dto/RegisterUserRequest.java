package com.cts.elearn.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
        @NotBlank(message = "Name is required")
        String name,

        @Email
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Contact number is required")
        String contactNumber,

        @NotBlank(message = "Password is required")
        String password
) {
}
