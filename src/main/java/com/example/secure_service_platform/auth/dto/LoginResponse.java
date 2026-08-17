package com.example.secure_service_platform.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType
) {
}
