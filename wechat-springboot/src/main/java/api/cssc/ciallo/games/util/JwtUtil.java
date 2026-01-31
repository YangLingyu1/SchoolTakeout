package api.cssc.ciallo.games.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private String secretKey = "your-secret-key";
    private long expirationTime = 86400000; // 1 day in milliseconds

    // 获取密钥对象
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 生成token
    public String generateToken(Integer userId, String openid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("openid", openid);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    // 从token中提取claims
    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

    // 从token中提取userId
    public Integer extractUserId(String token) {
        return (Integer) extractClaims(token).get("userId");
    }

    // 从token中提取openid
    public String extractOpenid(String token) {
        return (String) extractClaims(token).get("openid");
    }

    // 验证token是否过期
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // 验证token
    public boolean validateToken(String token, Integer userId) {
        return (extractUserId(token).equals(userId) && !isTokenExpired(token));
    }
}
