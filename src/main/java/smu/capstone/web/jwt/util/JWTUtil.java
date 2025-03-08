package smu.capstone.web.jwt.util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    @Value("${jwt.token.access-expire-length:3600000}") // 기본값 1시간 (3600000ms)
    private long ACCESS_EXPIRE;
    @Value("${jwt.token.refresh-expire-length:7200000}") // 기본값 2시간 (7200000ms)
    private long REFRESH_EXPIRE;


    public JWTUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email",String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role",String.class);
    }

    public String getCategory(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category",String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public String createJwt(String category, String email, String role) {
        Long expiredMs = category.equals("access") ? ACCESS_EXPIRE : REFRESH_EXPIRE;

        return Jwts.builder()
                .claim("category", category)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+expiredMs))
                .signWith(secretKey)
                .compact();

    }
}
