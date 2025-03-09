package smu.capstone.object.member.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.object.member.dto.AuthRequestDto;
import smu.capstone.object.member.dto.TokenResponseDto;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.respository.UserRepository;
import smu.capstone.web.jwt.TokenProvider;
import smu.capstone.web.jwt.redisdomain.RefreshTokenCache;
import smu.capstone.web.jwt.redisrepository.RefreshTokenCacheRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static smu.capstone.common.errorcode.AuthExceptionCode.INVALID_MAIL_OR_PASSWORD;
import static smu.capstone.web.jwt.TokenType.ACCESS_TOKEN;
import static smu.capstone.web.jwt.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final TokenProvider tokenProvider;

    private final UserRepository userRepository;
    private final RefreshTokenCacheRepository refreshTokenCacheRepository;

    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    public TokenResponseDto login(HttpServletResponse response, AuthRequestDto.Login authRequestDto) {
        UserEntity user = userRepository.findByEmail(authRequestDto.getEmail()).orElseThrow(() ->
                new RestApiException(INVALID_MAIL_OR_PASSWORD));

        if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
            throw new RestApiException(INVALID_MAIL_OR_PASSWORD);
        }

        String accessToken = tokenProvider.createToken(ACCESS_TOKEN, user.getId(), user.getAuthority().name());
        Authentication authentication = tokenProvider.createAuthenticationByAccessToken(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String refreshToken = tokenProvider.createToken(REFRESH_TOKEN, user.getId(), user.getAuthority().name());
        saveRefreshToken(refreshToken);
        setRefreshTokenCookie(response, refreshToken);

        return TokenResponseDto.builder()
                .tokenType(ACCESS_TOKEN)
                .token(accessToken)
                .build();
    }

    private void saveRefreshToken(String refreshToken) {
        refreshTokenCacheRepository.save(RefreshTokenCache.builder()
                .refreshToken(refreshToken)
                .expiration(tokenProvider.getExpirationSeconds(REFRESH_TOKEN, refreshToken))
                .build());
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        String cookieValue = "Bearer " + refreshToken;
        cookieValue = URLEncoder.encode(cookieValue, StandardCharsets.UTF_8);
        Cookie cookie = new Cookie(REFRESH_TOKEN.getHeader(), cookieValue);
        int expirationSeconds = (int) tokenProvider.getExpirationSeconds(REFRESH_TOKEN, refreshToken);
        cookie.setMaxAge(expirationSeconds);

        if (isProd()) {
            setRefreshTokenCookieWhenProd(response, cookie);
        }

        if (!isProd()) {
            setRefreshTokenCookieWhenLocal(response, cookie);
        }
    }

    private boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    private void setRefreshTokenCookieWhenProd(HttpServletResponse response, Cookie cookie) {
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("smnavi.me");
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    private void setRefreshTokenCookieWhenLocal(HttpServletResponse response, Cookie cookie) {
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.addCookie(cookie);
    }
}
