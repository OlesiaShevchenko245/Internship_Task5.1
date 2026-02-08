package com.cosmorum.gateway.security;

import java.net.URI;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(
                        ServerHttpSecurity http,
                        JwtCookieWebFilter jwtCookieWebFilter,
                        ServerAuthenticationSuccessHandler oauth2SuccessHandler,
                        ServerLogoutSuccessHandler logoutSuccessHandler) {

                return http
                                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                                .requestCache(cache -> cache.requestCache(NoOpServerRequestCache.getInstance()))

                                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                                .cors(Customizer.withDefaults())

                                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                                .addFilterAt(jwtCookieWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                                .authorizeExchange(ex -> ex
                                                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                                                .pathMatchers("/actuator/**").permitAll()

                                                .pathMatchers("/oauth2/**", "/login/**").permitAll()
                                                .pathMatchers("/logout").permitAll()

                                                .pathMatchers("/profile").authenticated()
                                                .pathMatchers("/api/**").authenticated()

                                                .anyExchange().permitAll())
                                .exceptionHandling(ex -> ex.authenticationEntryPoint((exchange, e) -> {
                                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                        return exchange.getResponse().setComplete();
                                }))
                                .oauth2Login(o -> o.authenticationSuccessHandler(oauth2SuccessHandler))
                                .logout(l -> l.logoutSuccessHandler(logoutSuccessHandler))
                                .build();
        }

        @Bean
        public ServerAuthenticationSuccessHandler oauth2SuccessHandler(JwtService jwtService, Environment env) {
                return (webFilterExchange, authentication) -> {
                        ServerWebExchange exchange = webFilterExchange.getExchange();

                        String frontend = env.getProperty("frontend.url", "http://localhost:8088");

                        OAuth2User user = (OAuth2User) authentication.getPrincipal();
                        String email = user.getAttribute("email");
                        String name = user.getAttribute("name");

                        String token = jwtService.createToken(
                                        email != null ? email : "user",
                                        Map.of("email", email, "name", name));

                        boolean secureCookie = Boolean.parseBoolean(env.getProperty("secure.cookie", "false"));
                        String sameSite = env.getProperty("cookie.samesite", "Lax");

                        ResponseCookie cookie = ResponseCookie.from(jwtService.getCookieName(), token)
                                        .httpOnly(true)
                                        .secure(secureCookie)
                                        .sameSite(sameSite)
                                        .path("/")
                                        .maxAge(120 * 60)
                                        .build();

                        exchange.getResponse().addCookie(cookie);

                        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                        exchange.getResponse().getHeaders().setLocation(URI.create(frontend + "/observations"));
                        return exchange.getResponse().setComplete();
                };
        }

        @Bean
        public ServerLogoutSuccessHandler logoutSuccessHandler(JwtService jwtService, Environment env) {
                return (webFilterExchange, authentication) -> {
                        String frontend = env.getProperty("frontend.url", "http://localhost:8088");

                        boolean secureCookie = Boolean.parseBoolean(env.getProperty("secure.cookie", "false"));
                        String sameSite = env.getProperty("cookie.samesite", "Lax");

                        ResponseCookie delete = ResponseCookie.from(jwtService.getCookieName(), "")
                                        .httpOnly(true)
                                        .secure(secureCookie)
                                        .sameSite(sameSite)
                                        .path("/")
                                        .maxAge(0)
                                        .build();

                        webFilterExchange.getExchange().getResponse().addCookie(delete);

                        webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                        webFilterExchange.getExchange().getResponse().getHeaders()
                                        .setLocation(URI.create(frontend + "/login"));
                        return webFilterExchange.getExchange().getResponse().setComplete();
                };
        }

        @Bean
        public CorsWebFilter corsWebFilter(Environment env) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowCredentials(true);

                String frontend = env.getProperty("frontend.url", "http://localhost:8088");
                config.addAllowedOrigin(frontend);

                config.addAllowedHeader("*");
                config.addAllowedMethod("*");

                config.addExposedHeader("Location");
                config.addExposedHeader("Set-Cookie");

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);

                return new CorsWebFilter(source);
        }
}
