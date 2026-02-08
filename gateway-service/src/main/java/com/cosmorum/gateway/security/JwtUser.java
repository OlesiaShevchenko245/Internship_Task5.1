package com.cosmorum.gateway.security;

public record JwtUser(String subject, String email, String name) {
}
