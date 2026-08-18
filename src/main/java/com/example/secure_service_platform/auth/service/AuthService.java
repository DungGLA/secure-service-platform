package com.example.secure_service_platform.auth.service;

import com.example.secure_service_platform.auth.dto.LoginRequest;
import com.example.secure_service_platform.auth.dto.LoginResponse;
import com.example.secure_service_platform.auth.dto.RegisterRequest;
import com.example.secure_service_platform.auth.dto.RegisterResponse;
import com.example.secure_service_platform.common.exception.EmailAlreadyExistsException;
import com.example.secure_service_platform.role.entity.Role;
import com.example.secure_service_platform.role.repository.RoleRepository;
import com.example.secure_service_platform.security.jwt.JwtService;
import com.example.secure_service_platform.user.entity.User;
import com.example.secure_service_platform.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

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


        return new LoginResponse(accessToken, "Bearer");
    }
}
