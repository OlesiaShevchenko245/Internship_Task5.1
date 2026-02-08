package com.cosmorum.gateway.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cosmorum.gateway.security.JwtUser;

@RestController
public class ProfileController {

    @GetMapping("/profile")
    public Map<String, Object> profile(Authentication authentication) {
        Object principal = authentication != null ? authentication.getPrincipal() : null;

        if (principal instanceof JwtUser user) {
            return Map.of(
                    "name", user.name(),
                    "email", user.email(),
                    "subject", user.subject());
        }

        return Map.of("name", null, "email", null, "subject", null);
    }
}
