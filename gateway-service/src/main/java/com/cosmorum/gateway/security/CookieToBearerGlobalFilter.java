package com.cosmorum.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CookieToBearerGlobalFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    public CookieToBearerGlobalFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var cookies = exchange.getRequest().getCookies();
        var cookie = cookies.getFirst(jwtService.getCookieName());

        if (cookie == null || cookie.getValue() == null || cookie.getValue().isBlank()) {
            return chain.filter(exchange);
        }

        String token = cookie.getValue();

        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .headers(h -> {
                    if (!h.containsKey(HttpHeaders.AUTHORIZATION)) {
                        h.setBearerAuth(token);
                    }
                })
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
