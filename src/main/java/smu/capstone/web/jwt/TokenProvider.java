package smu.capstone.web.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import smu.capstone.common.exception.RestApiException;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static smu.capstone.common.errorcode.AuthExceptionCode.*;
import static smu.capstone.web.jwt.TokenType.ACCESS_TOKEN;
import static smu.capstone.web.jwt.TokenType.REFRESH_TOKEN;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class TokenProvider {
    private static final String TOKEN_TYPE = "Bearer";
    private static final String AUTHORITY_KEY = "auth";

    @Value("${JWT_ACCESS_TOKEN_SECRET_KEY}")
    private String accessTokenSecretKey;
    @Value("${JWT_REFRESH_TOKEN_SECRET_KEY}")
    private String refreshTokenSecretKey;

    private Key accessTokenKey;
    private Key refreshTokenKey;

    @PostConstruct
    public void init() {
        accessTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessTokenSecretKey));
        refreshTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshTokenSecretKey));
    }

    public String createAccessTokenByRefreshToken(String refreshToken) {
        Claims claims = parseTokenClaims(REFRESH_TOKEN, refreshToken);
        String userId = claims.getSubject();
        String authority = claims.get(AUTHORITY_KEY).toString();
        return createToken(REFRESH_TOKEN, Long.parseLong(userId), authority);
    }

    public String createToken(TokenType tokenType, Long userId, String authority) {
        long nowMillisecond = new Date().getTime();

        return Jwts.builder()
                .setIssuer("poodle")
                .setSubject(userId.toString())
                .setExpiration(new Date(nowMillisecond + tokenType.getValidMillisecond()))
                .claim(AUTHORITY_KEY, authority)
                .signWith(getKey(tokenType), SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication createAuthenticationByAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken, accessTokenKey);

        if (ObjectUtils.isEmpty(claims.get(AUTHORITY_KEY))) {
            throw new RestApiException(INVALID_TOKEN);
        }

        Collection<? extends GrantedAuthority> authority = Collections.singleton(new SimpleGrantedAuthority(claims.get(AUTHORITY_KEY).toString()));
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authority);
    }

    public long getExpirationSeconds(TokenType tokenType, String token) {
        Claims claims = parseTokenClaims(tokenType, token);
        long expirationTime = claims.getExpiration().getTime();
        return (expirationTime - System.currentTimeMillis()) / 1000;
    }

    public void validateToken(TokenType tokenType, String token) {
        parseTokenClaims(tokenType, token);
    }

    private Claims parseTokenClaims(TokenType tokenType, String token) {
        return parseClaims(token, getKey(tokenType));
    }

    private Claims parseClaims(String token, Key key) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new RestApiException(EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new RestApiException(INVALID_TOKEN);
        }
    }

    public String getAccessToken(HttpServletRequest request) {
        String token = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION)).orElseThrow(() ->
                new RestApiException(AUTHORIZATION_REQUIRED)
        );

        if (!StringUtils.hasText(token) || !StringUtils.startsWithIgnoreCase(token, TOKEN_TYPE)) {
            throw new RestApiException(INVALID_TOKEN);
        }

        return token.substring(7);
    }

    public String getRefreshToken(HttpServletRequest request) {
        String token = getCookieByName(request, REFRESH_TOKEN.getHeader()).orElseThrow(() ->
                new RestApiException(REFRESH_TOKEN_NOT_EXIST)
        );

        if (!StringUtils.hasText(token) || !StringUtils.startsWithIgnoreCase(token, TOKEN_TYPE)) {
            throw new RestApiException(INVALID_TOKEN);
        }

        return token.substring(7);
    }

    private Optional<String> getCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    private Key getKey(TokenType tokenType) {
        if (tokenType == ACCESS_TOKEN) {
            return accessTokenKey;
        } else if (tokenType == REFRESH_TOKEN) {
            return refreshTokenKey;
        }
        return null;
    }
}
