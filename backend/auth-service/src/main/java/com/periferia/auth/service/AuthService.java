package com.periferia.auth.service;

import com.periferia.auth.dto.LoginRequest;
import com.periferia.auth.dto.LoginResponse;
import com.periferia.auth.dto.ProfileResponse;
import com.periferia.auth.entity.User;
import com.periferia.auth.repository.UserRepository;
import com.periferia.auth.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(token, user.getUsername(), user.getAlias());
    }

    public ProfileResponse getProfile(String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    return new ProfileResponse(
            user.getId(),
            user.getUsername(),
            user.getNombres(),
            user.getApellidos(),
            user.getFechaNacimiento(),
            user.getAlias()
    );
}

}