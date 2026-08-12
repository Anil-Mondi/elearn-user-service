package com.cts.elearn.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 50)
        String name,

        @NotBlank(message = "Email is required")
        @Email
        String email,

        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
        String contactNumber,

        @NotBlank(message = "Password is required")
        @Size(min = 5, max = 20, message = "Password must be between 5 and 20 characters")
        String password
) {
}
