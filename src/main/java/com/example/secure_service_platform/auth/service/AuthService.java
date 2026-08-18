package com.example.secure_service_platform.auth.service;

import com.example.secure_service_platform.auth.dto.*;
import com.example.secure_service_platform.auth.entity.RefreshToken;
import com.example.secure_service_platform.auth.repository.RefreshTokenRepository;
import com.example.secure_service_platform.common.exception.EmailAlreadyExistsException;
import com.example.secure_service_platform.role.entity.Role;
import com.example.secure_service_platform.role.repository.RoleRepository;
import com.example.secure_service_platform.security.jwt.JwtService;
import com.example.secure_service_platform.security.user.CustomUserDetails;
import com.example.secure_service_platform.user.entity.User;
import com.example.secure_service_platform.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.email());
        }

        User user = new User();

        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new IllegalStateException("USER role not found"));

        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName()
        );
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);

        User user = userRepository
                .findByEmailWithRolesAndPermissions(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String refreshToken = refreshTokenService.create(user);

        return new LoginResponse(accessToken, refreshToken, "Bearer");
    }

    public LoginResponse refresh(RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(request.getRefreshToken())
                        .orElseThrow(() ->
                                new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        User user = refreshToken.getUser();

        // Revoke old refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // Generate new refresh token
        String newRefreshToken =
                refreshTokenService.create(user);

        // Generate new access token
        UserDetails userDetails =
                new CustomUserDetails(user);

        String newAccessToken =
                jwtService.generateToken(userDetails);

        return new LoginResponse(newAccessToken, newRefreshToken, "Bearer");
    }
}
