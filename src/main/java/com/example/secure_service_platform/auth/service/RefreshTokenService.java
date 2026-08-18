package com.example.secure_service_platform.auth.service;

import com.example.secure_service_platform.auth.entity.RefreshToken;
import com.example.secure_service_platform.auth.repository.RefreshTokenRepository;
import com.example.secure_service_platform.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public String create(User user) {
        String token = generateToken();

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(token)
                        .user(user)
                        .expiresAt(Instant.now().plus(Duration.ofDays(7)))
                        .revoked(false)
                        .createdAt(Instant.now())
                        .build();

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    private String generateToken() {

        byte[] bytes = new byte[64];

        new SecureRandom().nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
