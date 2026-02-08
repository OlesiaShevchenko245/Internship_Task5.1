package com.cosmorum.gateway.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class OAuth2SuccessHandler implements ServerAuthenticationSuccessHandler {

    private final SecretKey key;
    private final String cookieName;
    private final long ttlMinutes;
    private final String frontendUrl;

    public OAuth2SuccessHandler(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.cookieName:COSMORUM_TOKEN}") String cookieName,
            @Value("${app.jwt.ttlMinutes:120}") long ttlMinutes,
            @Value("${frontend.url:http://localhost:3000}") String frontendUrl) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.cookieName = cookieName;
        this.ttlMinutes = ttlMinutes;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();

        if (!(authentication instanceof OAuth2AuthenticationToken oauth)) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not an OAuth2 authentication"));
        }

        Map<String, Object> attrs = oauth.getPrincipal().getAttributes();

        Object emailObj = attrs.get("email");
        String email = emailObj != null ? emailObj.toString() : null;

        if (email == null || email.isBlank()) {
            return Mono
                    .error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email not found in OAuth2 principal"));
        }

        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofMinutes(ttlMinutes));

        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("name", attrs.getOrDefault("name", ""))
                .claim("picture", attrs.getOrDefault("picture", ""))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(ttlMinutes))
                .sameSite("Lax")
                .build();

        exchange.getResponse().addCookie(cookie);
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(java.net.URI.create(frontendUrl));

        return exchange.getResponse().setComplete();
    }
}
