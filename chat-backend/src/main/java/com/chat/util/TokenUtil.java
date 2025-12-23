package com.chat.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token工具类
 */
public class TokenUtil {

    private static final String SECRET = "chat-secret-key-2024-online-chat-room-system";
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7天

    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 生成Token
     */
    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return createToken(claims, EXPIRATION);
    }

    /**
     * 创建Token
     */
    private static String createToken(Map<String, Object> claims, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证Token
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public static Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Integer userId = (Integer) claims.get("userId");
            return userId != null ? userId.longValue() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Token中获取用户名
     */
    public static String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return (String) claims.get("username");
        } catch (Exception e) {
            return null;
        }
    }

}
