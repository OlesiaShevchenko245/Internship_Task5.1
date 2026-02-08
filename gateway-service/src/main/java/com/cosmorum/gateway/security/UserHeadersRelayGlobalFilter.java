package com.cosmorum.gateway.security;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class UserHeadersRelayGlobalFilter implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange,
      org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();
    if (!path.startsWith("/api/")) {
      return chain.filter(exchange);
    }

    return exchange.getPrincipal()
        .cast(Authentication.class)
        .flatMap(auth -> {
          Object principal = auth.getPrincipal();
          if (principal instanceof JwtUser user) {
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-User-Email", user.email())
                .header("X-User-Name", user.name())
                .build();

            return chain.filter(exchange.mutate().request(mutated).build());
          }
          return chain.filter(exchange);
        })
        .switchIfEmpty(chain.filter(exchange));
  }

  @Override
  public int getOrder() {
    return -1;
  }
}
