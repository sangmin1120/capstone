package smu.capstone.domain.member.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.member.dto.AuthRequestDto;
import smu.capstone.domain.member.dto.TokenResponseDto;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;
import smu.capstone.intrastructure.jwt.service.TokenProvider;
import smu.capstone.intrastructure.redis.domain.RefreshTokenCache;
import smu.capstone.intrastructure.redis.repository.RefreshTokenCacheRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static smu.capstone.common.errorcode.AuthExceptionCode.WITHDRAW_ID;
import static smu.capstone.common.errorcode.AuthExceptionCode.INVALID_ID_OR_PASSWORD;
import static smu.capstone.intrastructure.jwt.TokenType.ACCESS_TOKEN;
import static smu.capstone.intrastructure.jwt.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final TokenProvider tokenProvider;

    private final UserRepository userRepository;
    private final RefreshTokenCacheRepository refreshTokenCacheRepository;

    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    @Value("${spring.domain}")
    String domain;

    @Transactional
    public TokenResponseDto login(HttpServletResponse response, AuthRequestDto.Login authRequestDto) {


        String accountId = authRequestDto.getAccountId();
        UserEntity user = userRepository.findByAccountId(accountId).orElseThrow(() ->
                new RestApiException(INVALID_ID_OR_PASSWORD));

        // 탈퇴 회원 추가
        if (user.isDeleted()) {
            throw new RestApiException(WITHDRAW_ID);
        }

        if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
            throw new RestApiException(INVALID_ID_OR_PASSWORD);
        }

        // JWT token 처리
        String accessToken = tokenProvider.createToken(ACCESS_TOKEN, user.getId(), accountId, user.getAuthority().name());
        Authentication authentication = tokenProvider.createAuthenticationByAccessToken(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String refreshToken = tokenProvider.createToken(REFRESH_TOKEN, user.getId(), accountId, user.getAuthority().name());
        saveRefreshToken(refreshToken);
        setRefreshTokenCookie(response, refreshToken);

        // FCM 토큰 저장
        user.setFcmToken(authRequestDto.getFcmToken());
        userRepository.save(user); // 로그인 할 때마다 fcm 값을 새로 저장해줌

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
        cookie.setSecure(true);
        cookie.setDomain(domain);

        int maxAge = cookie.getMaxAge();
        String encodedValue = cookie.getValue(); // 이미 URLEncoded 상태여야 함

        // 직접 Set-Cookie 헤더 작성 (SameSite=None 필수)
        String setCookieHeader = String.format(
                "%s=%s; Max-Age=%d; Path=/; Domain=%s; Secure; HttpOnly; SameSite=None",
                cookie.getName(),
                encodedValue,
                maxAge,
                cookie.getDomain()
        );

        response.addHeader("Set-Cookie", setCookieHeader);
    }

    private void setRefreshTokenCookieWhenLocal(HttpServletResponse response, Cookie cookie) {
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.addCookie(cookie);
    }
}
