package com.periferia.auth.controller;

import com.periferia.auth.dto.LoginRequest;
import com.periferia.auth.dto.LoginResponse;
import com.periferia.auth.dto.ProfileResponse;
import com.periferia.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> profile(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(authService.getProfile(username));
    }

}
