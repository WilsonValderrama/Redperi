package com.periferia.auth.service;

import com.periferia.auth.dto.LoginRequest;
import com.periferia.auth.dto.LoginResponse;
import com.periferia.auth.entity.User;
import com.periferia.auth.repository.UserRepository;
import com.periferia.auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_conCredencialesValidas_devuelveToken() {
        // un usuario y el comportamiento de los mocks
        User user = User.builder()
                .id(1L)
                .username("wvalderrama")
                .password("hashGuardado")
                .alias("wilson")
                .build();

        when(userRepository.findByUsername("wvalderrama")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "hashGuardado")).thenReturn(true);
        when(jwtService.generateToken(1L, "wvalderrama")).thenReturn("token-jwt-simulado");

        // act
        LoginResponse response = authService.login(new LoginRequest("wvalderrama", "Password123"));

        // assert
        assertThat(response.token()).isEqualTo("token-jwt-simulado");
        assertThat(response.username()).isEqualTo("wvalderrama");
        assertThat(response.alias()).isEqualTo("wilson");
    }

    @Test
    void login_conClaveIncorrecta_lanzaExcepcion() {
        User user = User.builder()
                .id(1L)
                .username("wvalderrama")
                .password("hashGuardado")
                .build();

        when(userRepository.findByUsername("wvalderrama")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("claveMala", "hashGuardado")).thenReturn(false);

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("wvalderrama", "claveMala")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Credenciales inválidas");

        // no se generó un token
        verify(jwtService, never()).generateToken(anyLong(), anyString());
    }

    @Test
    void login_conUsuarioInexistente_lanzaExcepcion() {
        when(userRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("noexiste", "cualquiera")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Credenciales inválidas");
    }
}
