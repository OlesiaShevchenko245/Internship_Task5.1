package com.cosmorum.gateway.security;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class JwtCookieWebFilter implements WebFilter {

    private final JwtService jwtService;

    public JwtCookieWebFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        var cookie = exchange.getRequest().getCookies().getFirst(jwtService.getCookieName());
        String token = cookie != null ? cookie.getValue() : null;

        if (token == null || token.isBlank() || !jwtService.isValid(token)) {
            return chain.filter(exchange);
        }

        String subject = jwtService.getSubject(token);
        if (subject == null || subject.isBlank()) {
            return chain.filter(exchange);
        }

        String email = jwtService.getStringClaim(token, "email");
        String name = jwtService.getStringClaim(token, "name");

        JwtUser user = new JwtUser(subject, email != null ? email : subject, name != null ? name : "User");

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user,
                token,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }
}
