package com.example.communityProject.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey; // 환경 변수에서 JWT 비밀 키 가져오기

    private final long expirationTime = 1000 * 60 * 30; // 15분 유효

    // 서명 키 생성
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // userId 기반으로 토큰 생성
    public String generateToken(Long userId, String role) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId)); // 사용자 ID를 String으로 설정
        claims.put("role", role); // 역할 추가
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime); // 15분 유효

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSigningKey())
                .compact();
    }

    // 토큰 검증 (userId 기반)
    public boolean validateToken(String token, Long userId) {
        try {
            Long extractedUserId = extractUserId(token);
            return (extractedUserId.equals(userId) && !isTokenExpired(token));
        } catch (JwtException e) {
            return false; // 토큰이 유효하지 않으면 false 반환
        }
    }

    // 토큰에서 userId 추출
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        // 'sub' 필드를 Long으로 변환하는 부분
        return Long.valueOf(claims.getSubject());
    }

    public String extractRole(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .get("role", String.class); // 역할을 추출
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // 비밀키로 서명 검증
                .build()
                .parseClaimsJws(token.replace("Bearer ", "")) // "Bearer " 제거 후 파싱
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public Long getUserIdFromToken(String token) {
        Long userId = extractUserId(token); // claims에서 userId 추출
        return userId;
    }
}
