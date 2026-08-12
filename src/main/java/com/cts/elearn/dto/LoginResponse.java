package com.cts.elearn.dto;

public record LoginResponse(

        String token,

        String role,

        String status

) {
}