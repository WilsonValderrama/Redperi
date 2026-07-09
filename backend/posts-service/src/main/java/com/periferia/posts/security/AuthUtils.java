package com.periferia.posts.security;

import org.springframework.security.core.Authentication;

public class AuthUtils {
    public static Long getUserId(Authentication auth) {
        return (Long) auth.getDetails();
    }
}

