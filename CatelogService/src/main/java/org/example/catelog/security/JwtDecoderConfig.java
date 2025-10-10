//package org.example.catelog.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//
//import javax.crypto.spec.SecretKeySpec;
//
//@Configuration
//public class JwtDecoderConfig {
//
//    private static final String SECRET_KEY = "nhaptenphuctrinhvaolachayduoctoken";
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        byte[] keyBytes = SECRET_KEY.getBytes();
//        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
//        return NimbusJwtDecoder.withSecretKey(secretKeySpec).build();
//    }
//}