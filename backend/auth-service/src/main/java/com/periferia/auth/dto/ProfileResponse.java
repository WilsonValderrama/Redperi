package com.periferia.auth.dto;

import java.time.LocalDate;

public record ProfileResponse(
        Long id,
        String username,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String alias
) {}
