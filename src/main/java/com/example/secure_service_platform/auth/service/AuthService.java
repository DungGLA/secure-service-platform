package com.example.secure_service_platform.auth.service;

import com.example.secure_service_platform.auth.dto.LoginRequest;
import com.example.secure_service_platform.auth.dto.LoginResponse;
import com.example.secure_service_platform.auth.dto.RegisterRequest;
import com.example.secure_service_platform.auth.dto.RegisterResponse;
import com.example.secure_service_platform.common.exception.EmailAlreadyExistsException;
import com.example.secure_service_platform.user.entity.User;
import com.example.secure_service_platform.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.email());
        }

        User user = new User();

        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

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

        return new LoginResponse("Login successful for user: " + request.email() + "");
    }
}
