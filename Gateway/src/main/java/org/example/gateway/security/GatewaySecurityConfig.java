package org.example.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
//import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {
//    private static final String SECRET_KEY = "nhaptenphuctrinhvaolachayduoctoken";

    @Bean
    public SecurityWebFilterChain SpringSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
//                        .pathMatchers("/auth/**").permitAll()
                        .anyExchange().permitAll());
        return http.build();
    }

//    @Bean
//    public ReactiveJwtDecoder jwtDecoder() {
//        return NimbusReactiveJwtDecoder.withSecretKey(
//                new javax.crypto.spec.SecretKeySpec(
//                        SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
//                )
//        ).build();
//    }
}
