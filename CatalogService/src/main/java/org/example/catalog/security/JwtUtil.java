//package org.example.catelog.security;
//
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Date;
//import java.util.List;
//import java.util.function.Function;
//
//@Component
//public class JwtUtil {
//
//    // Secret string (>= 32 ký tự)
//    private static final String SECRET = "nhaptenphuctrinhvaolachayduoctoken";
//
//    // Sinh Key chuẩn HS256
//    private final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
//
//    // Thời gian sống (ms)
//    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60 * 24;   // 1 ngày
//    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 100; // 100 ngày
//
//    // Tạo Access Token
//    public String generateAccessToken(UserDetails userDetails) {
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(a -> a.getAuthority())
//                .toList();
//
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .claim("roles", roles)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
//
//    // Tạo Refresh Token (không cần roles)
//    public String generateRefreshToken(UserDetails userDetails) {
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
//
//    // Lấy username từ token
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    // Lấy roles từ token
//    public List<String> extractRoles(String token) {
//        Claims claims = extractAllClaims(token);
//        return claims.get("roles", List.class);
//    }
//
//    // Kiểm tra Access Token hợp lệ
//    public boolean validateToken(String token, UserDetails userDetails) {
//        String username = extractUsername(token);
//        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
//    }
//
//    // Validate riêng Refresh Token (không cần roles)
//    public boolean validateRefreshToken(String token) {
//        try {
//            extractAllClaims(token); // sẽ quăng ExpiredJwtException nếu hết hạn
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            return false;
//        }
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        return claimsResolver.apply(extractAllClaims(token));
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .setSigningKey(SECRET_KEY)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//}
