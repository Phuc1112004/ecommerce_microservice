//package org.example.gateway.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.ws.rs.core.HttpHeaders;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//
//
//@Component
//public class JwtAuthenticationFilter implements GlobalFilter {
//    private final String secretKey = "nhaptenphuctrinhvaolachayduoctoken";
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String path = exchange.getRequest().getURI().getPath();
//
//        // Bỏ qua check cho các API public
//        if (path.startsWith("/auth")) {
//            return chain.filter(exchange);
//        }
//
//        String bearer = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if (bearer != null && bearer.startsWith("Bearer ")) {
//            bearer = bearer.substring(7);
//        }
//        String token = bearer;
//        if (token == null || !isValid(token)) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        // Nếu token ok → forward xuống service
//        return chain.filter(exchange);
//    }
//
//    private boolean isValid(String token) {
//        try {
//            Jwts.parser()
//                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
//                    .build()
//                    .parseClaimsJws(token); // nếu lỗi sẽ throw
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}
