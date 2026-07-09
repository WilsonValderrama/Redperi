package com.periferia.posts.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePostRequest(
        @NotBlank(message = "El mensaje es obligatorio") String mensaje
) {}
