package com.example.secure_service_platform.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @GetMapping("/me")
    public ResponseEntity<String> me(Authentication authentication) {

        return ResponseEntity.ok(authentication.getName());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public String getUsers() {
        return "Users";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public String deleteUser(@PathVariable Long id) {

        return "Deleted";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "Admin area";
    }
}
