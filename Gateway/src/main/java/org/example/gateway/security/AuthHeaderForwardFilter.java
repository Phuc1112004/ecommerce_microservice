//package org.example.gateway.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.ws.rs.core.HttpHeaders;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//@Component
//public class AuthHeaderForwardFilter implements GlobalFilter {
//
//    private final String secretKey = "nhaptenphuctrinhvaolachayduoctoken";
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
////        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
//
////        if (token != null && !token.isEmpty()) {
////            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
////                    .header("Authorization", token)
////                    .build();
////            return chain.filter(exchange.mutate().request(mutatedRequest).build());
////        }
//
//        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if(exchange.getRequest().getPath().toString().startsWith("/auth")) {
//            return chain.filter(exchange);
//        }
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
////        String token = authHeader.substring(7);
////        try {
////            Claims claims = validateToken(token);
////            // Optional: inject user info into headers for downstream services
////            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
////                    .header("Authorization", token)
////                    .build();
////            return chain.filter(exchange.mutate().request(mutatedRequest).build());
////        } catch (Exception e) {
////            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
////            return exchange.getResponse().setComplete();
////        }
////
//        return chain.filter(exchange);
//    }
//
//    public Claims validateToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//}
