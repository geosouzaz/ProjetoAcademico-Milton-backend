package com.exemplo.gatewayservice.config;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtHeadersRelayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> {
                    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                        Jwt jwt = jwtAuth.getToken();
                        String subject = jwt.getSubject();
                        String role = extractRole(jwt.getClaims());

                        ServerWebExchange mutated = exchange.mutate()
                                .request(request -> request.headers(headers -> {
                                    if (subject != null && !subject.isBlank()) {
                                        headers.set("X-Auth-User", subject);
                                    }
                                    if (role != null && !role.isBlank()) {
                                        headers.set("X-Auth-Role", role);
                                    }
                                }))
                                .build();

                        return chain.filter(mutated);
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private String extractRole(Map<String, Object> claims) {
        Object roleClaim = claims.get("role");
        if (roleClaim instanceof String role) {
            return role;
        }
        if (roleClaim instanceof List<?> roles && !roles.isEmpty()) {
            Object first = roles.get(0);
            return first != null ? first.toString() : null;
        }
        return null;
    }
}
