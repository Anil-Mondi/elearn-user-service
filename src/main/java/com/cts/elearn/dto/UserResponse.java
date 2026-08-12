package com.cts.elearn.dto;

public record UserResponse(

        Long userId,

        String name,

        String contactNumber,

        String email

) {
}