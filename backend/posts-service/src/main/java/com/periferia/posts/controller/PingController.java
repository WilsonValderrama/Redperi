package com.periferia.posts.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PingController {

    @GetMapping("/ping")
    public Map<String, Object> ping(Authentication authentication) {
        return Map.of(
                "message", "posts-service activo",
                "username", authentication.getName(),
                "userId", authentication.getDetails()
        );
    }
}
