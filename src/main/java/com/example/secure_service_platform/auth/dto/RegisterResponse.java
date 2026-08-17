package com.example.secure_service_platform.auth.dto;

import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String email,
        String fullName
) {
}