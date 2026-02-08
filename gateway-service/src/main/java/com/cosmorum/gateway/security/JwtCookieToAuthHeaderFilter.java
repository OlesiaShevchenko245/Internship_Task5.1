package com.cosmorum.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtCookieToAuthHeaderFilter implements GlobalFilter {

    private final JwtService jwtService;

    public JwtCookieToAuthHeaderFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (!path.startsWith("/api/")) {
            return chain.filter(exchange);
        }

        var cookie = exchange.getRequest().getCookies().getFirst(jwtService.getCookieName());
        if (cookie == null || cookie.getValue() == null || cookie.getValue().isBlank()) {
            return chain.filter(exchange);
        }

        var mutatedRequest = exchange.getRequest().mutate()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + cookie.getValue())
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
